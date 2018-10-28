import java.util.*;
import java.io.*;

/**
 * Tester class for Graph data structure
 *
 * @author Emma Jordan
 * @version 12/7/16
 *
 */
public class TestGraph {

    /** Main method creates a graph, addes nodes and edges to it, and tests its methods */
    public static void main (String [] args) {
	// make a graph
	Graph<String, Double> testgraph = new Graph<String, Double>();

	// add some nodes
	testgraph.addNode("A");
	testgraph.addNode("B");
	testgraph.addNode("C");

	// add some edges
	testgraph.addEdge(5.6, testgraph.getNode(0), testgraph.getNode(1));
	testgraph.addEdge(8.0, testgraph.getNode(1), testgraph.getNode(2));
	testgraph.addEdge(1.2, testgraph.getNode(0), testgraph.getNode(2));

	// print number of nodes and edges
	System.out.println("Graph contains "+testgraph.numNodes()+" nodes and "+testgraph.numEdges()+" edges"); 

	// check graph for consistency
	testgraph.check();
	
	// The following 5 tests require node and edge classes to be public

	// recheck graph with the following inconsistencies:
	// (uncomment each)
	//testgraph.getEdge(0).setHead(null);
	//testgraph.getNode(0).removeEdge(testgraph.getEdge(2));
	testgraph.check();

	// print neighbors of second node
	System.out.println("Neighbors of node '"+testgraph.getNode(1).getData()+"':");
	for (Graph.Node n : testgraph.getNode(1).getNeighbors()) {
	    System.out.println("Node '"+n.getData()+"'");
	}

	// find the edge from the first to the second node
	System.out.println("Edge from first to second node = node '"+testgraph.getNode(0).edgeTo(testgraph.getNode(1)).getData()+"'");

	// test if two edges are equal
	testgraph.addEdge(12.2, testgraph.getNode(0), testgraph.getNode(2));
	if (testgraph.getEdge(2).equals(testgraph.getEdge(3))) {
	    System.out.println("Edge '"+testgraph.getEdge(2).getData()+"' and edge '"+testgraph.getEdge(3).getData()+"' are equal");
	} else {
	    System.out.println("Edge '"+testgraph.getEdge(2).getData()+"' and edge '"+testgraph.getEdge(3).getData()+"' are not equal");
	}

	if (testgraph.getEdge(2).equals(testgraph.getEdge(0))) {
            System.out.println("Edge '"+testgraph.getEdge(2).getData()+"' and edge '"+testgraph.getEdge(0).getData()+"' are equal");
        } else {
            System.out.println("Edge '"+testgraph.getEdge(2).getData()+"' and edge '"+testgraph.getEdge(0).getData()+"' are not equal");
        }

	// get hash codes of some edges
	System.out.println("Hashcode of edge '"+testgraph.getEdge(0).getData()+"' is "+testgraph.getEdge(0).hashCode());
	System.out.println("Hashcode of edge '"+testgraph.getEdge(1).getData()+"' is "+testgraph.getEdge(1).hashCode());
	System.out.println("Hashcode of edge '"+testgraph.getEdge(2).getData()+"' is "+testgraph.getEdge(2).hashCode());
	System.out.println("Hashcode of edge '"+testgraph.getEdge(3).getData()+"' is "+testgraph.getEdge(3).hashCode());

	// print out graph
	System.out.println();
	testgraph.print();

	// BFT
	System.out.println("Breadth first traversal from A:");
	testgraph.BFT(testgraph.getNode(0));

	// DFT
	System.out.println("Depth first traversal from A:");
        testgraph.DFT(testgraph.getNode(0));

	// find distances from nodes to A
	/*System.out.println();
	System.out.println("Minimum distances to node A:");
	Hashtable<Graph<String, Double>.Node, Double> distances = testgraph.distance(testgraph.getNode(0));
	for (Map.Entry<Graph<String, Double>.Node, Double> entry : distances.entrySet()) {
	    System.out.println("Edge '"+entry.getKey().getData()+"' is minimum distance of "+entry.getValue() +" away");	 
	    }*/

	// remove a node
	testgraph.removeNode(testgraph.getNode(0));

	// print out
	System.out.println();
	testgraph.print();

	// remove an edge
	testgraph.removeEdge(testgraph.getEdge(1));

	// remove an edge by head and tail
	testgraph.removeEdge(testgraph.getNode(0), testgraph.getNode(1));

	// print out
	System.out.println();
	testgraph.print();
    }

}//end of TestGraph class 