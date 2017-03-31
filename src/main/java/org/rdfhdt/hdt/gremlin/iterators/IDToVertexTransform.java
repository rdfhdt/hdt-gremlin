package org.rdfhdt.hdt.gremlin.iterators;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rdfhdt.hdt.enums.DictionarySectionRole;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.iterator.utils.Transform;

public class IDToVertexTransform implements Transform<Integer, Vertex>{
	private HDTGraph graph;
	private DictionarySectionRole role;
	
	public IDToVertexTransform(HDTGraph graph, DictionarySectionRole role) {
		super();
		this.graph = graph;
		this.role = role;
	}
	
	@Override
	public Vertex convert(Integer item) {
		return graph.getVertexID(role, item);
	}

}
