package org.rdfhdt.hdt.gremlin.iterators;

import java.util.Iterator;

import org.rdfhdt.hdt.iterator.utils.IteratorConcat;

public class NumberIterator implements Iterator<Integer> {
	int pos;
	int max;
	
	public NumberIterator(int pos, int max) {
		super();
		this.pos = pos;
		this.max = max;
	}

	@Override
	public boolean hasNext() {
		return pos<=max;
	}

	@Override
	public Integer next() {
		return pos++;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	
	public static void main(String[] args) {
		NumberIterator it1 = new NumberIterator(1, 10);
		
		NumberIterator it2 = new NumberIterator(17, 20);
		Iterator<Integer> it = IteratorConcat.concat(it1, it2);
		while(it.hasNext()) {
			int a = it.next();
			System.out.println(a);
		}
	}
}
