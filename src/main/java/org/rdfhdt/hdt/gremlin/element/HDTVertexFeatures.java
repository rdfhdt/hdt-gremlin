package org.rdfhdt.hdt.gremlin.element;

import org.apache.tinkerpop.gremlin.structure.Graph.Features.VertexFeatures;

public class HDTVertexFeatures implements VertexFeatures {
	@Override
	public boolean supportsAddProperty() {
		return false;
	}
	
	@Override
	public boolean supportsRemoveProperty() {
		return false;
	}
	
	@Override
	public boolean supportsAddVertices() {
		return false;
	}
	
	@Override
	public boolean supportsRemoveVertices() {
		return false;
	}
	
}
