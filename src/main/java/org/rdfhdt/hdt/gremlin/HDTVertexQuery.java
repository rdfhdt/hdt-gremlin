package org.rdfhdt.hdt.gremlin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rdfhdt.hdt.enums.ResultEstimationType;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.gremlin.element.HDTVertex;
import org.rdfhdt.hdt.gremlin.iterators.EdgeToVertex;
import org.rdfhdt.hdt.gremlin.iterators.FilterPredicate;
import org.rdfhdt.hdt.gremlin.iterators.HDTEdgeIterable;
import org.rdfhdt.hdt.iterator.utils.Filter;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.iterator.utils.IteratorConcat;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;

public class HDTVertexQuery {
	private HDTVertex vertex;
	Direction direction;
	String [] labels;
	int limit;
	
	public HDTVertexQuery(HDTVertex vertex) {
		this.vertex = vertex;
	}

	public HDTVertexQuery direction(Direction direction) {
		this.direction = direction;
		 
		return this;
	}
	
	public HDTVertexQuery labels(String [] labels) {
		this.labels = labels;
		 
		return this;
	}
	
	public Iterable<Edge> edges() {
		Iterable<Edge> in = null;
		Iterable<Edge> out = null;

		if(direction==Direction.IN || direction==Direction.BOTH) {
			if(vertex.getRole()==TripleComponentRole.OBJECT || vertex.getInternalId()<=vertex.getHDTGraph().getBaseGraph().getDictionary().getNshared()) {
				in = getEdges(new TripleID(0,0,vertex.getInternalId()));
			}
		}
		
		if(direction==Direction.OUT || direction==Direction.BOTH) {
			if(vertex.getRole()==TripleComponentRole.SUBJECT || vertex.getInternalId()<=vertex.getHDTGraph().getBaseGraph().getDictionary().getNshared()) {
				out = getEdges(new TripleID(vertex.getInternalId(),0,0));
			}
		}

		if(in!=null && out!=null) {
			return IteratorConcat.concat(in, out);
		} else if(in!=null) {
			return in;
		} else if(out!=null){
			return out;
		}
		return Collections.emptySet();
	}
	
	private Iterable<Edge> getEdges( TripleID triple) {
		// FIXME: Check shared

		triple.setPredicate(getPredicateID());

		Filter<TripleID> filter = null;
		if(labels.length>1) {
			int preds[]  = new int[labels.length];
			for(int i=0;i<labels.length;i++) {
				preds[i] = vertex.getHDTGraph().getBaseGraph().getDictionary().stringToId(labels[i], TripleComponentRole.PREDICATE);
			}
			filter = new FilterPredicate(preds);
		}

		return new HDTEdgeIterable(vertex.getHDTGraph(), triple, filter, limit);
	}
	
	private int getPredicateID() {
		int pred = 0;
		if(labels.length==1) {
			pred = vertex.getHDTGraph().getBaseGraph().getDictionary().stringToId(labels[0], TripleComponentRole.PREDICATE);
		}
		return pred;
	}

	public Iterable<Vertex> vertices() {
		Iterable<Vertex> in = null;
		Iterable<Vertex> out = null;

		if(direction==Direction.IN || direction==Direction.BOTH) {
			if(vertex.getRole()==TripleComponentRole.OBJECT || vertex.getInternalId()<=vertex.getHDTGraph().getBaseGraph().getDictionary().getNshared()) {
				in = Iter.mapIterable(getEdges(new TripleID(0,0,vertex.getInternalId())), new EdgeToVertex(Direction.OUT));
			}
		}

		if(direction==Direction.OUT || direction==Direction.BOTH) {
			if(vertex.getRole()==TripleComponentRole.SUBJECT || vertex.getInternalId()<=vertex.getHDTGraph().getBaseGraph().getDictionary().getNshared()) {
				out = Iter.mapIterable(getEdges(new TripleID(vertex.getInternalId(),0,0)), new EdgeToVertex(Direction.IN));
			}
		}

		if(in!=null && out!=null) {
			return IteratorConcat.concat(in, out);
		} else if(in!=null) {
			return in;
		} else if(out!=null){
			return out;
		}
		return Collections.emptySet();
	}

	public long count() {
		long count = 0;
		
		// More than one label, HDT cannot estimate results, just count
		if(labels.length>1) {
			for (@SuppressWarnings("unused") final Edge edge : this.edges()) {
				count++;
			}
			return count;
		}
		
		// Zero or one label, do triple pattern.
		int pred = getPredicateID();

		if(direction==Direction.OUT || direction==Direction.BOTH) {
			count+=getSingleCount(new TripleID(vertex.getInternalId(), pred, 0));
		}
		
		if(direction==Direction.IN || direction==Direction.BOTH) {
			count+=getSingleCount(new TripleID(0, pred, vertex.getInternalId()));
		}	
		return count;
	}
	
	private long getSingleCount(TripleID tid) {
		IteratorTripleID it = vertex.getHDTGraph().getBaseGraph().getTriples().search(tid);
		if(it.numResultEstimation()==ResultEstimationType.EXACT) {
			return it.estimatedNumResults();
		} else {
			long count=0;
			while(it.hasNext()) {
				it.next();
				count++;
			}
			return count;
		}
	}

	public Object vertexIds() {
		final List<Object> list = new ArrayList<Object>();
		for (final Vertex vertex : this.vertices()) {
			list.add(vertex.id());
		}
		return list;
	}
}
