import java.util.*;
import java.awt.*;
import javax.swing.*;        

/**
 *  Implements a graphical canvas that displays a list of points.
 *
 *  @author  Emma Jordan
 *  @version 12/15/16
 */
class GraphCanvas extends JComponent {
    
    /** The graph to be drawn on this canvas */
    Graph<DisplayNodeData<String>, Double> graph;

    /** Coordinates of mouse for line animation */
    Point mousePoint;

    /** Node to be used for line animation */
    Graph<DisplayNodeData<String>, Double>.Node startNode;

    /** Boolean field to determine if animated line should be drawn */
    boolean drawLine;

    /** Boolean field to determine if point should be highlighted */
    boolean highlight;

    /** Boolean field to determine if point should be semi-permanently highlighted */
    boolean longHighlight;

    /** Node to be highlighted */
    Graph<DisplayNodeData<String>, Double>.Node highlightNode;

    /** Node to be semi-permanently highlighted */
    Graph<DisplayNodeData<String>, Double>.Node longHighlightNode;

    /** Dijkstra nodes to be highlighted */
    HashSet<Graph<DisplayNodeData<String>, Double>.Node> dijkstraNodes;

    /** Dijkstra edges to be highlighted */
    HashSet<Graph<DisplayNodeData<String>, Double>.Edge> dijkstraEdges;

    /** Boolean to determine whether to highlight Dijkstra path */
    boolean highlightPath;

    /** Constructor */
    public GraphCanvas() {
	graph = new Graph<DisplayNodeData<String>, Double>();
	mousePoint = null;
	startNode = null;
	drawLine = false;
	highlight = false;
	longHighlight = false;
	highlightNode = null;
	longHighlightNode = null;
	dijkstraNodes = new HashSet<Graph<DisplayNodeData<String>, Double>.Node>();
	dijkstraEdges = new HashSet<Graph<DisplayNodeData<String>, Double>.Edge>();
    }

    /** Recursive */

    /**
     *  Paints a red circle ten pixels in diameter at each node.
     *  Paints a line between points for each edge.
     *
     *  @param g The graphics object to draw with
     */
    public void paintComponent(Graphics g) {
	// Draw the nodes and node data
	for (int i = 0; i < graph.numNodes(); i++) {
	    Graph<DisplayNodeData<String>, Double>.Node node = graph.getNode(i);
	    DisplayNodeData<String> dispData = (DisplayNodeData<String>)node.getData(); 
	    int x = (int)dispData.getPoint().getX()-10;
	    int y = (int)dispData.getPoint().getY()-10;
	    // Highlight node if necessary
	    if ((highlight)&&(highlightNode.equals(node))) {
		g.setColor(Color.BLUE);
		g.fillOval((x-2),(y-2),24,24);
	    }
	    // Semi permanently highlight node if necessary
	    if ((longHighlight)&&(longHighlightNode.equals(node))) {
                g.setColor(Color.BLUE);
                g.fillOval((x-2),(y-2),24,24);
            }
	    if ((highlightPath)&&(dijkstraNodes.contains(node))) {
		g.setColor(Color.CYAN);
	    } else {
		g.setColor(Color.RED);
	    }
	    g.fillOval(x,y,20,20);
	    // Draw node's data(label)
	    g.setColor(Color.BLACK);
	    g.drawString(node.getData().getData(), x, y);
	}
	// Draw the edges
	for (int i = 0; i < graph.numEdges(); i++) {
	    Graph<DisplayNodeData<String>, Double>.Edge edge = graph.getEdge(i);
	    DisplayNodeData<String> headData = (DisplayNodeData<String>)edge.getHead().getData();
	    DisplayNodeData<String> tailData = (DisplayNodeData<String>)edge.getTail().getData();
	    int x1 = (int)headData.getPoint().getX();
	    int y1 = (int)headData.getPoint().getY();
	    int x2 = (int)tailData.getPoint().getX();
	    int y2 = (int)tailData.getPoint().getY();
	    if ((highlightPath)&&(dijkstraEdges.contains(edge))) {
		g.setColor(Color.CYAN);
            } else {
		g.setColor(Color.BLACK);
	    }
	    g.drawLine(x1, y1, x2, y2);
	    // Draw edge's data(distance)
	    g.setColor(Color.BLACK);
	    g.drawString(edge.getData().toString(), ((x1+x2)/2), ((y1+y2)/2));
	}
	// Draw line if drawLine is true
	if (drawLine) {
	    int x = (int)startNode.getData().getPoint().getX();
	    int y = (int)startNode.getData().getPoint().getY();
	    g.drawLine(x, y, (int)mousePoint.getX(), (int)mousePoint.getY());
	}
        super.paintComponent(g);
    }

    /**
     *  The component will look bad if it is sized smaller than this
     *
     *  @returns The minimum dimension
     */
    public Dimension getMinimumSize() {
        return new Dimension(500,3000);
    }

    /**
     *  The component will look best at this size
     *
     *  @returns The preferred dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(500,300);
    }
}