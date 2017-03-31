package org.rdfhdt.hdt.gremlin.example;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.rdfhdt.hdt.gremlin.HDTGraph;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

public class HDTGremlinExample {
	public static void main(String[] args) throws Throwable {

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
		
		System.exit(0);
	}
}
