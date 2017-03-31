package org.rdfhdt.hdt.gremlin;

import org.apache.tinkerpop.gremlin.structure.Graph.Features;

public class HDTGraphFeatures implements Features.GraphFeatures {

	@Override
	public boolean supportsTransactions() {
		return false;
	}
	
	@Override
	public boolean supportsThreadedTransactions() {
		return false;
	}
	
	@Override
	public boolean supportsComputer() {
		// TODO: Implement, sounds interesting.
		return false;
	}
}
