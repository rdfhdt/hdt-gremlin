package org.rdfhdt.hdt.gremlin.iterators;

import java.util.HashSet;
import java.util.Set;

import org.rdfhdt.hdt.iterator.utils.Filter;
import org.rdfhdt.hdt.triples.TripleID;

public class FilterPredicate implements Filter<TripleID> {

	private Set<Integer> values;
	
	public FilterPredicate(int [] values) {
		this.values = new HashSet<Integer>();
		for(Integer i : values) {
			this.values.add(i);
		}
	}
	
	@Override
	public boolean accept(TripleID item) {
		return values.contains(item.getPredicate());
	}

}
