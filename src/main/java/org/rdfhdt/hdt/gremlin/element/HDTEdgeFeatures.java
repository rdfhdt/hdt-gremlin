package org.rdfhdt.hdt.gremlin.element;

import org.apache.tinkerpop.gremlin.structure.Graph.Features.EdgeFeatures;

public class HDTEdgeFeatures implements EdgeFeatures {
	@Override
	public boolean supportsAddProperty() {
		return false;
	}
	
	@Override
	public boolean supportsRemoveProperty() {
		return false;
	}
	
	@Override
	public boolean supportsUserSuppliedIds() {
		return false;
	}
	
	@Override
	public boolean supportsAddEdges() {
		return false;
	}
	
	@Override
	public boolean supportsRemoveEdges() {
		return false;
	};
}
