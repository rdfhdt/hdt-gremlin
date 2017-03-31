package org.rdfhdt.hdt.gremlin.iterators;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.gremlin.element.HDTEdge;
import org.rdfhdt.hdt.iterator.utils.Transform;
import org.rdfhdt.hdt.triples.TripleID;

public class TripleIDtoEdge implements Transform<TripleID, Edge>{

	private HDTGraph graph;

	public TripleIDtoEdge(HDTGraph graph) {
		super();
		this.graph = graph;
	}
	
	@Override
	public Edge convert(TripleID triple) {
		return new HDTEdge(graph, triple);
	}

}
