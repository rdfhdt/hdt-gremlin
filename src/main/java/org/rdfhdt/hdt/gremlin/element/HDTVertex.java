package org.rdfhdt.hdt.gremlin.element;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.gremlin.HDTVertexQuery;
import org.rdfhdt.hdt.gremlin.iterators.TripleIDtoLiteral;
import org.rdfhdt.hdt.iterator.utils.Filter;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.triples.TripleID;

public class HDTVertex implements Vertex {
	protected HDTGraph graph;
	protected TripleComponentRole role;
	protected int id;
	
	public HDTVertex(HDTGraph graph, TripleComponentRole role, int id) {
		super();
		this.graph = graph;
		this.role = role;
		this.id = id;
	}

	public HDTVertexQuery query() {
		return new HDTVertexQuery(this);
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		// Simple Information (RDF Node)
		return label();
		
		// Detailed information (Including IDs)
//		StringBuilder b = new StringBuilder();
//		b.append(role).append(" [");
//		b.append(id).append("] ");
//		b.append(graph.rawGraph.getDictionary().idToString(id, role));
//		return b.toString();
	}
	
	public int getInternalId() {
		return id;
	}

	public TripleComponentRole getRole() {
		return role;
	}

	@Override
	public Object id() {
//		return (((long)role.ordinal())<<61)|id;

//		return (((long)id)<<2)|role.ordinal();
		
		// Note: When using numeric ids, we assume the following consecutive order: SHARED|SUBJECTS|OBJECTS
		if(role==TripleComponentRole.OBJECT && id>=graph.getNShared()) {
			// Move object ID just after the SUBJECTS
			return graph.getNSubjects()+id;
		}
		return id;
	}

	@Override
	public String label() {
		return graph.getBaseGraph().getDictionary().idToString(id, role).toString();
	}

	@Override
	public Graph graph() {
		return graph;
	}

	public HDTGraph getHDTGraph() {
		return graph;
	}

	@Override
	public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <V> VertexProperty<V> property(String key, V value) {
		throw new UnsupportedOperationException();
	}

	public Iterable<Vertex> getVertices(Direction direction, String... labels) {
		return this.query().direction(direction).labels(labels).vertices();
	}

	@Override
	public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
		return this.query().direction(direction).labels(edgeLabels).edges().iterator();
	}

	@Override
	public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
		return this.query().direction(direction).labels(edgeLabels).vertices().iterator();
	}

	@Override
	public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
		return null;
	}
	
	/**
	 * Get the properties associated to this vertex (The RDF literals associated to the subject)
	 *
	 * @param key The property to get.
	 * @return an Iterator<String> that provides the values associated to this vertex under that key. 
	 */
	public String getProperty(String key) {

		// Vertex must be subject
		if(role!=TripleComponentRole.SUBJECT) {
			return null;
		}

		// Get id of property
		int predid = graph.getBaseGraph().getDictionary().stringToId(key, TripleComponentRole.PREDICATE);
		if(predid==-1) {
			return null;
		}

		// Search SP?
		Iterator<TripleID> it = this.graph.getBaseGraph().getTriples().search(new TripleID(id, predid, 0));

		// Filter only literals
		it = Iter.filter(it, new Filter<TripleID>() {
			@Override
			public boolean accept(TripleID triple) {
				return graph.isLiteral(triple.getObject());
			}
		});

		// Convert to edges.
		Iterator<String> itFin = Iter.map(it, new TripleIDtoLiteral(graph));
		String result=null;
		if(itFin.hasNext()){
			result = itFin.next();
		}
		return result;
	}
	
    public <V> VertexProperty<V> property(final String key) {
        final Iterator<VertexProperty<V>> iterator = this.properties(key);
        if (iterator.hasNext()) {
            final VertexProperty<V> property = iterator.next();
            if (iterator.hasNext())
                throw Vertex.Exceptions.multiplePropertiesExistForProvidedKey(key);
            else
                return property;
        } else {
            return VertexProperty.<V>empty();
        }
    }

	@Override
	public Set<String> keys() {
		if(role==TripleComponentRole.OBJECT && id>graph.getNShared()) {
			return Collections.emptySet();
		}
		return new VertexPropertySet(this);
	}

	@Override
	public <V> VertexProperty<V> property(Cardinality cardinality, String key, V value, Object... keyValues) {
		throw Element.Exceptions.propertyAdditionNotSupported();
	}
}
