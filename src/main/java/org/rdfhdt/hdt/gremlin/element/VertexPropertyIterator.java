package org.rdfhdt.hdt.gremlin.element;

import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.iterator.utils.PrefetchIterator;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;

public class VertexPropertyIterator extends PrefetchIterator<String> {
	private final Dictionary dict;
	private final IteratorTripleID it;
	private int lastPredicate=-1;
	private final HDTVertex vertex;
	
	public VertexPropertyIterator(HDTVertex vertex) {
		this.vertex = vertex;
		dict = vertex.getHDTGraph().getBaseGraph().getDictionary();
		it = vertex.getHDTGraph().getBaseGraph().getTriples().search(new TripleID(vertex.id, 0, 0));
	}

	@Override
	protected String prefetch() {
		while(it.hasNext()) {
			TripleID t = it.next();

			if(t.getPredicate()!=lastPredicate) {
				if(vertex.graph.isLiteral(t.getObject())) {
					lastPredicate = t.getPredicate();
					return dict.idToString(t.getPredicate(), TripleComponentRole.PREDICATE).toString();
				} else {
					// Non-literal found, no more elements.
					return null;
				}
			}
		}
		return null;
	}
};