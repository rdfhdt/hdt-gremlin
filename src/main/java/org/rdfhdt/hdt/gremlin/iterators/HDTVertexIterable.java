package org.rdfhdt.hdt.gremlin.iterators;

import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.DictionarySectionRole;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.iterator.utils.Filter;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.iterator.utils.IteratorConcat;

public class HDTVertexIterable implements Iterable<Vertex> {
	private final HDTGraph graph;
	private Dictionary d;
	
	public HDTVertexIterable(HDTGraph graph) {
		super();
		this.graph = graph;
		this.d=graph.getBaseGraph().getDictionary();
	}

	@Override
	public Iterator<Vertex> iterator() {
		
		Iterator<Vertex> it1=Iter.map(new NumberIterator(1, (int)d.getNshared()), new IDToVertexTransform(graph, DictionarySectionRole.SHARED));
		Iterator<Vertex> it2=Iter.map(new NumberIterator((int)d.getNshared()+1, (int)d.getNsubjects()), new IDToVertexTransform(graph, DictionarySectionRole.SUBJECT));
		
		Iterator<Vertex> it3=Iter.map(
				Iter.filter(
						new NumberIterator((int)d.getNshared()+1, (int)d.getNobjects()),
						new Filter<Integer>() {
							@Override
							public boolean accept(Integer item) {
								return !graph.isLiteral(item);
							}
						}
				),
				new IDToVertexTransform(graph, DictionarySectionRole.OBJECT)
		);

		return IteratorConcat.concat(IteratorConcat.concat(it1, it2),it3);
	}

}
