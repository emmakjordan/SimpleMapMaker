import java.util.*;
import java.io.*;

/**
 * Class to hold Dijkstra shortest path information
 *
 * @author Emma Jordan
 * @version 12/15/16
 *
 */
public class DijkstraResults<V,E> {
    /** Hashtable of nodes to costs */
    private Hashtable<Graph<V,E>.Node, Double> cost;

    /** Hashtable of nodes to signposts */
    private Hashtable<Graph<V,E>.Node, Graph<V,E>.Node> signpost;

    /** Constructor */
    DijkstraResults(Hashtable<Graph<V,E>.Node, Double> cost, Hashtable<Graph<V,E>.Node, Graph<V,E>.Node> signpost) {
	this.cost = cost;
	this.signpost = signpost;
    }

    /** Accessor for cost hashtable */
    public Hashtable<Graph<V,E>.Node, Double> getCosts() {
	return this.cost;
    }

    /** Accessor for signpost hashtable */
    public Hashtable<Graph<V,E>.Node, Graph<V,E>.Node> getSignposts() {
	return this.signpost;
    }

    /** Wrapper for order method */
    public ArrayList<Graph<V,E>.Node> order (Graph<V,E>.Node node) {
	ArrayList<Graph<V,E>.Node> orderedNodes = new ArrayList<Graph<V,E>.Node>();
	ArrayList<Graph<V,E>.Node> nodes = orderRecurse(node, orderedNodes);
	return nodes;
    }

    /** Recursive method to put nodes in order */
    public ArrayList<Graph<V,E>.Node> orderRecurse (Graph<V,E>.Node node, ArrayList<Graph<V,E>.Node> orderedNodes) {
	// Stop condition: when node cost == 0 (because that's the end node)
	if (!signpost.get(node).equals(node)){
	    orderedNodes.add(node);
	    orderRecurse(signpost.get(node), orderedNodes);
	} else {
	    orderedNodes.add(node);
	}
	return orderedNodes;
    }
}