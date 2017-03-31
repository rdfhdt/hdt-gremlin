package org.rdfhdt.hdt.gremlin;

import org.apache.tinkerpop.gremlin.jsr223.AbstractGremlinPlugin;
import org.apache.tinkerpop.gremlin.jsr223.DefaultImportCustomizer;
import org.apache.tinkerpop.gremlin.jsr223.ImportCustomizer;
import org.rdfhdt.hdt.gremlin.element.HDTEdge;
import org.rdfhdt.hdt.gremlin.element.HDTVertex;
import org.rdfhdt.hdt.gremlin.element.HDTVertexFeatures;

public class HDTGremlinPlugin extends AbstractGremlinPlugin {
	private static final String NAME = "org.rdfht.hdt";
	
    private static final ImportCustomizer imports = DefaultImportCustomizer.build()
            .addClassImports(HDTGraph.class,
                    HDTVertex.class,
                    HDTEdge.class,
                    HDTVertexFeatures.class
            		).create();
	
	public HDTGremlinPlugin() {
		super(NAME, imports);
	}
		
}
