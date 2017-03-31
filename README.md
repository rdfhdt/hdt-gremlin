# HDT Gremlin

This project provides a wrapper to execute [Apache Tinkerpop Gremlin](http://tinkerpop.apache.org) queries on top of an RDF/HDT file.

# Example Usage

(Note: Full code available under `org.rdfhdt.hdt.gremlin.example.HDTGremlinExample`)

```Java
// Download Semantic Web Dog Food dataset about papers and create HDT
String url = "http://gaia.infor.uva.es/hdt/swdf-2012-11-28.hdt.gz";
InputStream in = new BufferedInputStream(new GZIPInputStream(new URL(url).openStream()));
try(HDT hdt = HDTManager.loadIndexedHDT(in)){
	in.close();

	// Create a Gremlin Graph
	try(HDTGraph hdtgraph = new HDTGraph(hdt)){

		// Find Mario's coauthors in SWDF dataset
		hdtgraph.traversal().V("http://data.semanticweb.org/person/mario-arias-gallego")
		.out("http://xmlns.com/foaf/0.1/made")
		.in("http://xmlns.com/foaf/0.1/made")
		.sideEffect( e-> System.out.println(e) )
		.iterate();
	}
}
```

# Dependencies

This project requires hdt-java-core version 2.1-SNAPSHOT which is available in https://github.com/rdfhdt/hdt-java


# License
This project is distributed under Apache License. Please see LICENSE file for full terms. Please note that dependent libraries may have different licenses (E.g. HDT has LGPL license).