package org.rdfhdt.hdt.gremlin.iterators;

import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.TripleComponentRole;

public class LiteralChecker {

	final Dictionary dict;
	long min, max, sh;
	
	public LiteralChecker(Dictionary dictionary) {
		this.dict = dictionary;
		this.sh = dictionary.getNshared();
		this.min = Long.MAX_VALUE;
		this.max = 0;
	}

	public boolean isLiteral(int object) {
		if(object<=sh) {
			return false;
		}
		if(object>=min && object<=max) {
			return true;
		}
		CharSequence str = dict.idToString(object, TripleComponentRole.OBJECT);
		if(str.charAt(0)=='"') {
			min = Math.min(min, object);
			max = Math.max(max, object);
//			System.out.println(min+"/"+object+"/"+max);
			return true;
		} else {
			return false;
		}
	}

}
