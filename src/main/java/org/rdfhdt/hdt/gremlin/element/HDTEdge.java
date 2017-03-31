package org.rdfhdt.hdt.gremlin.element;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rdfhdt.hdt.dictionary.DictionaryUtil;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.iterator.utils.Iter;
import org.rdfhdt.hdt.triples.TripleID;

public class HDTEdge implements Edge {
	private HDTGraph graph;
	private TripleID triple;
	
	public HDTEdge(HDTGraph graph, TripleID triple) {
		super();
		this.graph = graph;
		this.triple = triple;
	}

	public TripleID getTripleID() {
		return triple;
	}

	@Override
	public String toString() {
		return DictionaryUtil.tripleIDtoTripleString(graph.getBaseGraph().getDictionary(),triple).toString();
	}
	
	@Override
	public Object id() {
		return new Long(((long)triple.getSubject())<<32 | triple.getObject());
	}

	@Override
	public String label() {
		// FIXME: Cache?
		return graph.getBaseGraph().getDictionary().idToString(triple.getPredicate(), TripleComponentRole.PREDICATE).toString();
	}

	@Override
	public Graph graph() {
		return graph;
	}

	@Override
	public Vertex inVertex() {
		return new HDTVertex(graph, TripleComponentRole.OBJECT, triple.getObject());
	}
	
	@Override
	public Vertex outVertex() {
		return new HDTVertex(graph, TripleComponentRole.SUBJECT, triple.getSubject());
	}
	
	@Override
	public Iterator<Vertex> bothVertices() {
		return Arrays.asList(inVertex(),outVertex()).iterator();
	}
	
	@Override
	public Iterator<Vertex> vertices(Direction direction) {
		if(direction==Direction.OUT) {
			return Iter.<Vertex>single(outVertex());
		} else if(direction==Direction.IN) { 
			return Iter.<Vertex>single(inVertex());
		} else if(direction==Direction.BOTH) {
			return bothVertices();
		}
		return null;
	}

	@Override
	public <V> Iterator<Property<V>> properties(String... propertyKeys) {
		// Edges have no properties in RDF
		return Collections.emptyIterator();
	}
	
	@Override
	public <V> Property<V> property(String key, V value) {
		// Edges have no properties in RDF
		return null;
	}

	@Override
	public void remove() {
	}
}
