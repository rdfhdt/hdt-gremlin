package org.rdfhdt.hdt.gremlin.convert;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.DictionarySectionRole;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;
import org.rdfhdt.hdt.util.StopWatch;


/**
 * 
 * Import an HDT file into a Gremlin Graph
 * 
 * @author mario.arias
 *
 */
public class HDTtoGremlin {
	
	private static void loadSection(long [] map, Iterator<? extends CharSequence> it, Graph graph, DictionarySectionRole role) {

		int count=0;
		while(it.hasNext()) {
			String str = it.next().toString();

			Vertex v = graph.addVertex(str);
//			v.property("node", str);
			map[count] = (long) v.id();

			if((count%1000000)==0) {
				System.out.println(role+" Vertex: "+(count/1000000)+"M");
			}
			count++;
		}
	}
	
	private static long getIDShared(long[]mapShared, long []mapNotShared, int val) {
		if(val<=mapShared.length) {
			return mapShared[val-1];
		} else {
			return mapNotShared[val-mapShared.length-1];
		}
	}
	
	
	private static void importGraph(HDT hdt, Graph graph) {
		
		Dictionary d = hdt.getDictionary();
		
		long [] sharedMap = new long[(int)d.getNshared()];
		long [] subjectMap = new long[(int)(d.getNsubjects()-d.getNshared())];
//		long [] predicateMap = new long[(int)d.getNpredicates()];
		long [] objectMap = new long[(int)(d.getNobjects()-d.getNshared())];
		
//		load(predicateMap, d.getPredicates().getSortedEntries(), to);
		loadSection(sharedMap, d.getShared().getSortedEntries(), graph, DictionarySectionRole.SHARED);
		loadSection(subjectMap, d.getSubjects().getSortedEntries(), graph, DictionarySectionRole.SUBJECT);
		loadSection(objectMap, d.getObjects().getSortedEntries(), graph, DictionarySectionRole.OBJECT);
		
		IteratorTripleID it = hdt.getTriples().searchAll();
		
		int count=0;
		while(it.hasNext()) {
			TripleID triple = it.next();
			long s = getIDShared(sharedMap, subjectMap, triple.getSubject());
//			long p = predicateMap[triple.getPredicate()-1];
			long o = getIDShared(sharedMap, objectMap, triple.getObject());
			
			Vertex subj = graph.vertices(s).next();
			Vertex obj = graph.vertices(o).next();
			
			subj.addEdge(d.idToString(triple.getPredicate(), TripleComponentRole.PREDICATE).toString(), obj);
			
			if((count%10000)==0) {
				System.out.println("Edges: "+(count/1000)+"K");
			}
			count++;
		}
	}
	
	public static void smallTest(Graph graph) {
		Iterator<Vertex> it = graph.vertices();
		int i=0;
		while(i<100 && it.hasNext()) {
			Vertex v = it.next();
			System.out.println(v.id()+"/"+v.property("node"));
			i++;

		}
		
		// Iterate over Edges
		Iterator<Edge> it2 = graph.edges();
		i=0;
		while(i<100 && it2.hasNext()) {
			Edge e = it2.next();
			System.out.println(e);
			i++;
		}
	}
	
	public static void main(String[] args) throws Exception {		
		if(args.length!=2) {
			System.out.println("Usage: hdt2gremlin <file.hdt> <Gremlin Graph Config File>");
			System.out.println(" The config follows the syntax of gremlins factory Graph.open().");
			System.exit(-1);
		}
		
		// Create Graph
		Configuration p = new PropertiesConfiguration(args[1]);
		try(Graph gremlinGraph = GraphFactory.open(p)){
			
			// Open HDT
			try(HDT hdt = HDTManager.mapHDT("args[0]")){

				// Import HDT into Graph
				StopWatch st = new StopWatch();
				importGraph(hdt, gremlinGraph);
				System.out.println("Took "+st.stopAndShow());
			}
				
//			smallTest(gremlinGraph);
		}
		
		System.exit(0);
	}
}
