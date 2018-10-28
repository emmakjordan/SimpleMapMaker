import java.util.*;
import java.io.*;

/**
 * Implementation of a Graph data structure
 *
 * @author Emma Jordan
 * @version 12/15/16
 *
 */
public class Graph<V, E> extends Object {
    
    /** List of edges */
    private ArrayList<Edge> edges;

    /** List of nodes */
    private ArrayList<Node> nodes;

    /** Constructs a new graph with empty nodes and edges */
    Graph () {
	this.edges = new ArrayList<Edge>();
	this.nodes = new ArrayList<Node>();
    }

    /** Returns the edge at the given index */
    public Edge getEdge(int i) {
	return this.edges.get(i);
    }

    /** Returns the node at the given index */
    public Node getNode(int i) {
	return this.nodes.get(i);
    }

    /** Returns index of a node in the master list */
    public int getNodeIndex(Node node) {
	return this.nodes.indexOf(node);
    }

    /** Returns an edge specified by its node endpoints */
    public Edge getEdgeRef(Node head, Node tail) {
	Edge foundEdge = null;
	Edge refEdge = new Edge(null, head, tail);
	if (this.edges.contains(refEdge)) {
	    int index = this.edges.indexOf(refEdge);
	    foundEdge = this.edges.get(index);
	}
	return foundEdge;
    }

    /** Returns number of edges in edge list */
    public int numEdges() {
	return edges.size();
    }

    /** Returns number of nodes in node list */
    public int numNodes() {
	return nodes.size();
    }

    /** Adds an edge with given data, head, and tail */
    public void addEdge(E data, Node head, Node tail) {
	// Only add edge if the head and tail aren't the same
	// and aren't already connected
	if ((!head.equals(tail))&&(!head.isNeighbor(tail))) {
	    Edge e = new Edge(data, head, tail);
	    // Add to master edge list
	    this.edges.add(e);
	    // Add to head's edge list
	    head.addEdge(e);
	    // Add to tail's edge list
	    tail.addEdge(e);
	}
    }

    /** Adds a node with given data */
    public void addNode(V data) {
	// Only needs to update master node list to begin with
	this.nodes.add(new Node(data));
    }

    /** Removes a specific edge */
    public void removeEdge(Edge edge) {
	// Should remove 3 links to the edge
	this.edges.remove(edge);
	edge.getHead().removeEdge(edge);
	edge.getTail().removeEdge(edge);
    }

    /** Removes an edge specified by head and tail */
    public void removeEdge(Node head, Node tail) {
	this.removeEdge(this.getEdgeRef(head, tail));
    }

    /** Removes a specific node */
    public void removeNode(Node node) {
	// Should remove master list link and all edges to the node
	while (node.getEdgeList().size() != 0) {
	    this.removeEdge(node.getEdgeList().get(0));
	}
	this.nodes.remove(node);
    }

    /** Breadth-first traversal of the graph */
    public HashSet BFT (Node start) {
	// Queue for traversal
	LinkedList<Node> queue = new LinkedList<Node>();
	// HashSet for seen nodes
	HashSet<Node> seen = new HashSet<Node>();
	// Traversal
	seen.add(start);
	queue.add(start);
	while (queue.peek() != null) {
	    Node currentNode = queue.remove();
	    currentNode.visit();
	    for (Node n : currentNode.getNeighbors()) {
		if (!seen.contains(n)) {
		    seen.add(n);
		    queue.add(n);
		}
	    }
	}
	return seen;
    }

    /** Wrapper method for DFT */
    public HashSet<Node> DFT (Node start) {
	HashSet<Node> seen = new HashSet<Node>();
	HashSet<Node> nodes = DFTRecurse(start, seen);
	return nodes;
    }

    /** Depth-first traversal of the graph */
    public HashSet<Node> DFTRecurse (Node start, HashSet<Node> seen) {
	if (!seen.contains(start)) {
	    start.visit();
	    seen.add(start);
	    for (Node n : start.getNeighbors()) {
		DFTRecurse(n, seen);
	    }
	}
	return seen;
    }

    /** Finds shortest path from one node to any other node */
    public DijkstraResults<V, E> distance (Node start) {
	// Hashtable for cost
	Hashtable<Node, Double> cost = new Hashtable<Node, Double>();
	// Hashtable for signpost
	Hashtable<Node, Node> signpost = new Hashtable<Node, Node>();
	// Hashset for visited
	HashSet<Node> visited = new HashSet<Node>(); 
	// Hashtable for unvisited
	Hashtable<Node, Double> unvisited = new Hashtable<Node, Double>();

	// Add all nodes to cost and unvisited hashtables with infinite cost 
	// (except start - cost of 0)
	// Add nodes to signpost hashtable with themselves as signpost
	for (Node n : this.nodes) {
	    if (n.equals(start)) {
		cost.put(n, 0.0);
		unvisited.put(n, 0.0);
	    } else {
		cost.put(n, Double.MAX_VALUE);
		unvisited.put(n, Double.MAX_VALUE);
	    }
	    signpost.put(n, n);
	}
	
	// For each unvisited node, take the one with lowest cost
	while (!unvisited.isEmpty()) {
	    Node currentNode = null;
	    double lowValue = Collections.max(unvisited.values());
	    for (Map.Entry<Node, Double> entry : unvisited.entrySet()) {
		if (entry.getValue() == lowValue) {
		    currentNode =  entry.getKey();
		}
	    }
	    unvisited.remove(currentNode);
	    visited.add(currentNode);
	    // Check if the node's neighbors benefit from going through this node
	    for (Node node : currentNode.getNeighbors()) {
		Edge e = currentNode.edgeTo(node);
		double possCost;
		if (e.getData() instanceof Number) {
		    Number n  = (Number)e.getData();
		    double data = n.doubleValue();
		    possCost = data+cost.get(currentNode);
		} else {
		    possCost = 1.0+cost.get(currentNode);
		}
		// If the neighbor does benefit, update its cost and signpost
		if (possCost < cost.get(node)) {
		    cost.put(node, possCost);
		    unvisited.put(node, possCost);
		    signpost.put(node, currentNode);
		}
	    }
	}
	DijkstraResults<V,E> results = new DijkstraResults<V,E> (cost, signpost);
	return results;
    }

    /** Prints graph: each node and its edges, then each edge and its nodes */
    public void print() {
	for (Node node : this.nodes) {
	    System.out.println("Node '"+node.getData()+"'");
	    for (Edge e : node.getEdgeList()) {
		System.out.println("    Edge '"+e.getData()+"'");
	    }
	}
	for (Edge edge : this.edges) {
	    System.out.println("Edge '"+edge.getData()+"'");
	    System.out.println("    Head: Node '"+edge.getHead().getData()+"'");
	    System.out.println("    Tail: Node '"+edge.getTail().getData()+"'");
	}
    }

    /** Performs consistency checks in the graph */
    public boolean check () {
	boolean consistent = true;
	// Loop over central edge list
	for (int i = 0; i < this.edges.size(); i++) {
	    Edge edge = this.getEdge(i);
	    // check if head and tail links are null
	    if ((edge.getHead() == null)||(edge.getTail() == null)) {
		consistent = false;
		System.out.println("Graph contains edge with null endpoint");
	    }
	    // check if head and tail link back to the edge 
	    else if ((!edge.getHead().getEdgeList().contains(edge))||(!edge.getTail().getEdgeList().contains(edge))) {
		consistent = false;
		System.out.println("Graph contains edge with an endpoint that does not link back to it");
	    }
	    // check if central node list contains head and tail 
	    else if ((!this.nodes.contains(edge.getHead()))||(!this.nodes.contains(edge.getTail()))) {
		consistent = false;
		System.out.println("Graph contains edge whose endpoint is not in central node list");
	    }
	} 
	//Loop over central node list
	for (int j = 0; j < this.nodes.size(); j++) {
	    Node node = this.getNode(j);
	    for (Edge e : node.getEdgeList()) {
		// check if edge links are null
		if (e == null) {
		    consistent = false;
		    System.out.println("Graph contains node with null edge link");
		}
		// check if edge has the node as either head or tail
		else if ((!e.getHead().equals(node))&&(!e.getTail().equals(node))) {
		    consistent = false;
		    System.out.println("Graph contains node with an edge that does not link back to it");
		}
		// check if edge is in the central edge list
		else if (!this.edges.contains(e)) {
		    consistent = false;
		    System.out.println("Graph contains node whose edge is not in the central edge list");
		}
	    }
	}
	if (consistent) {
	    System.out.println("Graph is consistent");
	}
	return consistent;
    }


    /**
     * Class for graph nodes
     */
    public class Node extends Object {
	/** Data of the node */
	private V data;

	/** List of the node's edges */
	private ArrayList<Edge> edgeList;

	/** Constructs a new node with data and an empty edge list */
	Node (V data) {
	    this.data = data;
	    this.edgeList = new ArrayList<Edge>(); 
	}

	/** Accessor for data */
	public V getData () {
	    return this.data;
	}

	/** Manipulator for data */
	public void setData (V data) {
	    this.data = data;
	}

	/** Accessor for edge list */
	public ArrayList<Edge> getEdgeList() {
	    return this.edgeList;
	}

	/** Returns list of all this node's neighbors */
	public ArrayList<Node> getNeighbors () {
	    ArrayList<Node> neighbors = new ArrayList<Node>();
	    // Go through edge list and get the node on the other side of each edge
	    for (Edge e : edgeList) {
		if ((e.getHead().equals(this))&&(!neighbors.contains(e.getTail()))) {
		    neighbors.add(e.getTail());
		} else if ((e.getTail().equals(this))&&(!neighbors.contains(e.getHead()))) {
		    neighbors.add(e.getHead());
		}
	    }
	    return neighbors;
	}

	/** Adds an edge to the edge list */
	public void addEdge (Edge edge) {
	    this.edgeList.add(edge);
	}

	/** Removes an edge from the edge list */
	public void removeEdge (Edge edge) {
	    this.edgeList.remove(edge);
	}

	/** Returns true if the given node is this node's neighbor */
	public boolean isNeighbor (Node node) {
	    boolean isneighbor = false;
	    for (Edge edge : this.edgeList) {
		if ((edge.getHead().equals(node))||(edge.getTail().equals(node))) {
		    isneighbor = true;
		}
	    }
	    return isneighbor;
	}

	/** Returns the edge from this node to the given one, or null if none */
	public Edge edgeTo (Node node) {
	    Edge e = null;
	    for (Edge edge : this.edgeList) {
		if ((edge.getHead().equals(node))||(edge.getTail().equals(node))) {
                    e = edge;
                }
	    }
	    return e;
	}

	/** "Visits" the node during traversal */
	public void visit() {
	    System.out.println(this.getData());
	}
    }

    /** 
     * Class for graph edges 
     */
    public class Edge extends Object {
	/** Data of the edge */
	private E data;

	/** Edge's head */
	private Node head;

	/** Edge's tail*/
	private Node tail;

	/** Constructs new edge with data, head, and tail */
	Edge (E data, Node head, Node tail) {
	    this.data = data;
	    this.head = head;
	    this.tail = tail;
	}

	/** Accessor for edge data */
	public E getData() {
	    return this.data;
	}

	/** Accessor for head */
	public Node getHead() {
	    return this.head;
	}

	/** Accessor for tail */
	public Node getTail() {
	    return this.tail;
	}

	/** Manipulator for edge data */
	public void setData(E data) {
	    this.data = data;
	}

	/** Manipulator for head */
	public void setHead(Node head) {
	    this.head = head;
	}

	/** Manipulator for tail */
	public void setTail(Node tail) {
	    this.tail = tail;
	}

	/** Returns true if two edges are equal (same endpoints) */
	public boolean equals(Object o) {
	    boolean equal = false;
	    if (getClass() == o.getClass()) {
                @SuppressWarnings("unchecked")
                    Edge e = (Edge)o;
		if (((this.head.equals(e.getHead()))&&(this.tail.equals(e.getTail())))||((this.head.equals(e.getTail()))&&(this.tail.equals(e.getHead())))) {
		    equal = true;
		}
            }
	    return equal;
	}

	/** Updated hashcode method to match updated equals */
	public int hashCode () {
	    return(this.head.hashCode()*this.tail.hashCode());
	}

    }

}//end of Graph class