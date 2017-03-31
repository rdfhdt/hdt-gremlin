package org.rdfhdt.hdt.gremlin.iterators;

import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rdfhdt.hdt.iterator.utils.Transform;

public class EdgeToVertex implements Transform<Edge, Vertex> {

	private Direction direction;
	
	public EdgeToVertex(Direction dir) {
		super();
		this.direction = dir;
	}

	@Override
	public Vertex convert(Edge item) {
		Iterator<Vertex> vertices = item.vertices(direction);
		if(vertices.hasNext()) {
			return vertices.next();
		}
		return null;
	}

}
