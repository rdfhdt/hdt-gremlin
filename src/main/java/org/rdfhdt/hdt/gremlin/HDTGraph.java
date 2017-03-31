package org.rdfhdt.hdt.gremlin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.wrapped.WrappedGraph;
import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.dictionary.DictionaryUtil;
import org.rdfhdt.hdt.enums.DictionarySectionRole;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.gremlin.element.HDTEdge;
import org.rdfhdt.hdt.gremlin.element.HDTVertex;
import org.rdfhdt.hdt.gremlin.iterators.HDTEdgeIterable;
import org.rdfhdt.hdt.gremlin.iterators.HDTVertexIterable;
import org.rdfhdt.hdt.gremlin.iterators.LiteralChecker;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.triples.TripleString;


public class HDTGraph implements Graph, WrappedGraph<HDT> {
	public static final String CONFIG_FILENAME = "gremlin.hdt.file";
	
	protected final HDT rawGraph;
	private boolean weLoaded;
	private long nshared,nsubjects;
	private LiteralChecker literalChecker;
	
    protected BaseConfiguration configuration = new BaseConfiguration();
	
	public HDTGraph(HDT hdt) {
		this.rawGraph = hdt;
		this.weLoaded=false;
		populateInternal();
	}
	
	public HDTGraph(String hdtFile) throws IOException {
		this.rawGraph = HDTManager.mapIndexedHDT(hdtFile, null);
		this.weLoaded=true;
		populateInternal();
	}
	
	public static HDTGraph open(Configuration config) throws IOException {
		String file = config.getString(CONFIG_FILENAME);
		return new HDTGraph(file);
	}
	
	private void populateInternal(){
		this.nshared = rawGraph.getDictionary().getNshared();
		this.nsubjects = rawGraph.getDictionary().getNsubjects()-nshared;
		this.literalChecker = new LiteralChecker(rawGraph.getDictionary());
	}
	

	@Override
	public Variables variables() {
		return null;
	}

	@Override
	public Configuration configuration() {
		return configuration;
	}

	@Override
	public HDT getBaseGraph() {
		return rawGraph;
	}
	
	@Override
	public Features features() {
		return (Features) new HDTGraphFeatures();
	}

	@Override
	public Vertex addVertex(Object... keyValues) {
		// FIXME: Should be addition but does not exist.
		throw Vertex.Exceptions.vertexRemovalNotSupported();
	}

	@Override
	public Iterator<Vertex> vertices(Object... vertexIds) {
		if(vertexIds==null || vertexIds.length==0) {
			return new HDTVertexIterable(this).iterator();
		}
		return Iter.map(Arrays.asList(vertexIds), a->getVertex(a));
	}

	@Override
	public Iterator<Edge> edges(Object... edgeIds) {
		if(edgeIds==null || edgeIds.length==0) {
			return new HDTEdgeIterable(this).iterator();
		}
		return Iter.map(Arrays.asList(edgeIds), a->getEdge(a));
	}

	@Override
	public Transaction tx() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
		if(weLoaded) {
			rawGraph.close();
		}		
	}


	// FIXME: Add cache
	protected Vertex getVertexID(TripleComponentRole role, int id) {
		return new HDTVertex(this, role, id);
	}
	
	// FIXME: Add cache
	public Vertex getVertexID(DictionarySectionRole role, int id) {
		return new HDTVertex(this, asTripleRole(role), id);
	}
	
	private TripleComponentRole asTripleRole(DictionarySectionRole role) {
		switch (role) {
		case SHARED:
		case SUBJECT:
			return TripleComponentRole.SUBJECT;
		case PREDICATE:
			return TripleComponentRole.PREDICATE;
		case OBJECT:
			return TripleComponentRole.OBJECT;
		}
		return null; // Non reachable
	}

	// FIXME: Add cache
	protected Vertex getVertexStr(String str) {
		Dictionary d = rawGraph.getDictionary();
		
		int id = d.stringToId(str, TripleComponentRole.SUBJECT);
		if(id!=-1) {
			return new HDTVertex(this, TripleComponentRole.SUBJECT, id);
		}
		
		id = d.stringToId(str, TripleComponentRole.OBJECT);
		if(id!=-1) {
			return new HDTVertex(this, TripleComponentRole.OBJECT, id);
		}
		
		return null;
	}

	public Vertex getVertex(Object id) {
		if(id instanceof String) {
			return getVertexStr((String)id);
		} else if(id instanceof HDTVertex) {
			return (Vertex) id;
		} else {
			int intId = -1;
			if(id instanceof Integer){
				intId = ((Integer) id).intValue();
			}
			
			if(id instanceof Long) {
				intId = ((Long) id).intValue();
			}
			
			if(intId==-1){
				return null;
			}
		
			// Note: When using numeric ids, we assume the following consecutive order: SHARED|SUBJECTS|OBJECTS
			if(intId<=getTotalSubjects()) {
				return new HDTVertex(this, TripleComponentRole.SUBJECT, intId);
			} else {
				// Translate absolute object id to local object id
				return new HDTVertex(this, TripleComponentRole.OBJECT, (int)(intId-getNSubjects()));
			}
		}
	}
	
	public Edge getEdge(Object id) {
		TripleString ts;
		if(id instanceof String) {
			try {
				ts = new TripleString();
				ts.read((String) id);
			} catch (ParserException e) {
				return null;
			}
		} else if(id instanceof TripleString) {
			ts = (TripleString) id;
		} else if(id instanceof Integer) {
			//TODO: Get a triple from its position in BitmapTriples.
			return null;
		} else {
			return null;
		}
		
		return new HDTEdge(this, DictionaryUtil.tripleStringtoTripleID(rawGraph.getDictionary(), ts));
	}

	public final long getNShared() {
		return this.nshared;
	}

	public final long getNSubjects() {
		return this.nsubjects;
	}

	public final long getTotalSubjects() {
		return this.nsubjects+this.nshared;
	}

	/**
	 * Returns whether a given object ID is literal or not.
	 * @param object
	 * @return
	 */
	public boolean isLiteral(int object) {
		return literalChecker.isLiteral(object);
	}

	@Override
	public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphComputer compute() throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

}
