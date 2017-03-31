package org.rdfhdt.hdt.gremlin.element;

import java.util.AbstractSet;
import java.util.Iterator;

import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;

public class VertexPropertySet extends AbstractSet<String> {

	final HDTVertex vertex;
	
	public VertexPropertySet(HDTVertex vertex) {
		super();
		this.vertex = vertex;
	}

	@Override
	public Iterator<String> iterator() {
		return new VertexPropertyIterator(vertex);
	}

	@Override
	public int size() {
		IteratorTripleID it = vertex.getHDTGraph().getBaseGraph().getTriples().search(new TripleID(vertex.id, 0, 0));
		int lastPredicate=-1;
		int count = 0;

		while(it.hasNext()) {
			TripleID t = it.next();

			if(t.getPredicate()!=lastPredicate && vertex.graph.isLiteral(t.getObject())) {
				lastPredicate = t.getPredicate();
				count++;
			}
		}

		return count;
	}

}
