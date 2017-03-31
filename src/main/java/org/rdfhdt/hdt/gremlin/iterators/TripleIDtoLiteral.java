package org.rdfhdt.hdt.gremlin.iterators;

import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.iterator.utils.Transform;
import org.rdfhdt.hdt.triples.TripleID;

public class TripleIDtoLiteral implements Transform<TripleID, String>{

	HDTGraph graph;
	Dictionary dict;

	public TripleIDtoLiteral(HDTGraph graph) {
		this.graph = graph;
		this.dict = graph.getBaseGraph().getDictionary();
	}

	@Override
	public String convert(TripleID item) {
		return dict.idToString(item.getObject(), TripleComponentRole.OBJECT).toString();
	}

}
