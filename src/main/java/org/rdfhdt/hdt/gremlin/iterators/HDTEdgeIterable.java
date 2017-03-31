package org.rdfhdt.hdt.gremlin.iterators;

import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.iterator.utils.Filter;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.triples.TripleID;

public class HDTEdgeIterable implements Iterable<Edge> {
	private final HDTGraph graph;
	private TripleID pattern;
	private Filter<TripleID> filter=null;
	private int limit=0;
	
	public HDTEdgeIterable(HDTGraph graph) {
		super();
		this.graph = graph;
		this.pattern = new TripleID();
	}
	
	public HDTEdgeIterable(HDTGraph graph, TripleID pattern, Filter<TripleID> filter, int limit) {
		super();
		this.graph = graph;
		this.pattern = pattern;
		this.filter = filter;
		this.limit = limit;
	}

	@Override
	public Iterator<Edge> iterator() {
		Iterator<TripleID> it = this.graph.getBaseGraph().getTriples().search(pattern);
		
		// Filter literals
		if(pattern.getObject()==0) {
			it = Iter.filter(it, new Filter<TripleID>() {
				@Override
				public boolean accept(TripleID triple) {
					return !graph.isLiteral(triple.getObject());
				}
			});
		}

		// Filter properties
		if(filter!=null) {
			it = Iter.filter(it, filter);
		}

		// Limit number of results
		if(limit!=Integer.MAX_VALUE && limit>0) {
			it = Iter.limit(it, limit);
		}
		
		// Convert to edges.
		return Iter.map(it, new TripleIDtoEdge(graph));
	}

}
