import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 *  Implements a GUI for working with a graph
 *
 *  @author  Emma Jordan
 *  @version 12/15/16
 */
public class GraphGUI {

    /** The graph to be displayed */
    private GraphCanvas canvas;

    /** JFrame */
    private JFrame frame;

    /** Label for the input mode instructions */
    private JLabel instr;

    /** The input mode */
    InputMode mode = InputMode.ADD_NODES;

    /** Remembers node where last mousedown event occurred */
    Graph<DisplayNodeData<String>, Double>.Node nodeUnderMouse;

    /** Remembers node where last mouse click occuurred */
    Graph<DisplayNodeData<String>, Double>.Node nodeClicked;

    /** Remembers current open graph file */
    String openFile;

    /**
     *  Schedules a job for the event-dispatching thread
     *  creating and showing this application's GUI.
     */
    public static void main(String[] args) {
        final GraphGUI GUI = new GraphGUI();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUI.createAndShowGUI();
                }
            });
    }

    /** Sets up the GUI window */
    public void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        frame = new JFrame("Graph GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components
        createComponents(frame);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /** Puts content in the GUI window */
    public void createComponents(JFrame frame) {
        // graph display
        Container pane = frame.getContentPane();
        pane.setLayout(new FlowLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        canvas = new GraphCanvas();
        PointMouseListener pml = new PointMouseListener();
        canvas.addMouseListener(pml);
        canvas.addMouseMotionListener(pml);
        panel1.add(canvas);

        instr = new JLabel("Click to add new nodes; drag to move.");
        panel1.add(instr,BorderLayout.NORTH);
        pane.add(panel1);

        // controls
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(4,2));
        JButton addNodeButton = new JButton("Add/Move Nodes");
        panel2.add(addNodeButton);
        addNodeButton.addActionListener(new AddNodeListener());
        JButton rmvNodeButton = new JButton("Remove Nodes");
        panel2.add(rmvNodeButton);
        rmvNodeButton.addActionListener(new RmvNodeListener());
	// ALSO ADD BUTTONS FOR OTHER MODES
	JButton addEdgeButton = new JButton("Add Edges");
        panel2.add(addEdgeButton);
        addEdgeButton.addActionListener(new AddEdgeListener());
	JButton rmvEdgeButton = new JButton("Remove Edges");
        panel2.add(rmvEdgeButton);
        rmvEdgeButton.addActionListener(new RmvEdgeListener());
        // Add button for distance
	JButton getDistButton = new JButton("Get directions");
	panel2.add(getDistButton);
	getDistButton.addActionListener(new GetDistListener());
	// Add button to read in saved graph
	JButton readGraphButton = new JButton("Open saved graph");
	panel2.add(readGraphButton);
	readGraphButton.addActionListener(new ReadGraphListener());
	// Add button to save graph to a file
	JButton saveGraphButton = new JButton("Save graph");
	panel2.add(saveGraphButton);
	saveGraphButton.addActionListener(new SaveGraphListener());

	pane.add(panel2);
    }

    /** Method to read in predetermined file to graph */
    public void readGraph (String filename) {
	BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
	    for (String line = br.readLine(); line != null; line = br.readLine()) {
		String[] lineData = line.split(" ");
		// if first char is 'n', add new node to graph with given string name and point
		if (lineData[0].equals("n")) {
		    Point p = new Point (Integer.parseInt(lineData[3]), Integer.parseInt(lineData[4]));
		    canvas.graph.addNode(new DisplayNodeData<String>(lineData[2], p));
		}
		// if first char is 'e', add new edge to graph with given head, tail, and data 
		else if (lineData[0].equals("e")) {
		    Graph<DisplayNodeData<String>, Double>.Node head = canvas.graph.getNode(Integer.parseInt(lineData[1])); 
		    Graph<DisplayNodeData<String>, Double>.Node tail = canvas.graph.getNode(Integer.parseInt(lineData[2]));
		    double data = Double.parseDouble(lineData[3]);
		    canvas.graph.addEdge(data, head, tail);
		}
	    }
	    canvas.repaint();
        } catch (IOException e) {
            System.err.println("Problem reading file "+filename);
            System.exit(-1);
        }
    }

    /** Method to print out graph to file */
    public void saveGraph () {	
	// Create new printwriter
	PrintWriter out = null;
	try {
	    // if a graph has been opened, save to same file 
	    if (openFile != null) {
		out = new PrintWriter(new FileWriter(openFile));	    
	    } else {
		try {
		    String s = (String)JOptionPane.showInputDialog(frame, "Type in a file name to save graph as", "SAVE FILE", JOptionPane.PLAIN_MESSAGE);
		    if (!s.equals(null)) {
			out = new PrintWriter(new FileWriter(s));
		    }
		} catch (NullPointerException npe) {}
	    }
	    if (out != null) {
		// Print current graph to file
		for (int i = 0; i < canvas.graph.numNodes(); i++) {
		    Graph<DisplayNodeData<String>, Double>.Node n = canvas.graph.getNode(i);
		    out.println("n "+(i+1)+" "+n.getData().getData()+" "+(int)n.getData().getPoint().getX()+" "+(int)n.getData().getPoint().getY());
		}
		for (int j = 0; j < canvas.graph.numEdges(); j++) {
		    Graph<DisplayNodeData<String>, Double>.Edge e = canvas.graph.getEdge(j);
		    out.println("e "+canvas.graph.getNodeIndex(e.getHead())+" "+canvas.graph.getNodeIndex(e.getTail())+" "+e.getData());
		}
	    }
	    //out.close();
	} catch (IOException e) {
	    System.err.println("Problem reading file ");
	    System.exit(-1);
	}
    }

    /** Method to clear current graph */
    public void clearGraph() {
	while(canvas.graph.numNodes() != 0) {
	    canvas.graph.removeNode(canvas.graph.getNode(0));
	}
	while(canvas.graph.numEdges() != 0) {
	    canvas.graph.removeEdge(canvas.graph.getEdge(0));
	}
	canvas.repaint();
    }

    /** 
     * Returns a node found within the drawing radius of the given location, 
     * or null if none
     *
     *  @param x  the x coordinate of the location
     *  @param y  the y coordinate of the location
     *  @return  a node from the canvas if there is one covering this location, 
     *  or a null reference if not
     */
    public Graph<DisplayNodeData<String>, Double>.Node findNearbyNode(int x, int y) {
	Graph<DisplayNodeData<String>, Double>.Node nearby = null;
	for (int i = 0; i < canvas.graph.numNodes(); i++) {
	    Graph<DisplayNodeData<String>, Double>.Node n = canvas.graph.getNode(i);
	    if (n.getData().getPoint().distance(x,y) < 20) {
                nearby = n;
            }
	}
	return nearby;
    }


    /** Constants for recording the input mode */
    enum InputMode {
        ADD_NODES, RMV_NODES, ADD_EDGES, RMV_EDGES, GET_DIST
	    }

    /** Listener for AddNode button */
    private class AddNodeListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_NODES;
            instr.setText("Click to add new nodes or change their location.");
	    if (nodeClicked != null) {
		nodeClicked = null;
		canvas.longHighlightNode = null;
		canvas.longHighlight = false;
	    }
        }
    }

    /** Listener for RmvNode button */
    private class RmvNodeListener implements ActionListener {
        /** Event handler for RmvNode button */
        public void actionPerformed(ActionEvent e) {
	    mode = InputMode.RMV_NODES;
	    instr.setText("Click to remove nodes.");
	    if (nodeClicked != null) {
                nodeClicked = null;
                canvas.longHighlightNode = null;
                canvas.longHighlight = false;
            }
	}
    }

    /** Listener for AddEdge button */
    private class AddEdgeListener implements ActionListener {
        /** Event handler for AddEdge button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_EDGES;
            instr.setText("Click and drag from one node to another to add an edge.");
	    if (nodeClicked != null) {
                nodeClicked = null;
                canvas.longHighlightNode = null;
                canvas.longHighlight = false;
            }
        }
    }

    /** Listener for RmvEdge button */
    private class RmvEdgeListener implements ActionListener {
        /** Event handler for RmvEdge button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.RMV_EDGES;
            instr.setText("Click to remove edges.");
	    if (nodeClicked != null) {
                nodeClicked = null;
                canvas.longHighlightNode = null;
                canvas.longHighlight = false;
            }
	}
    }

    /** Listener for shortest distance button */
    private class GetDistListener implements ActionListener {
	/** Event handler for distance button*/
	public void actionPerformed(ActionEvent e) {
	    mode = InputMode.GET_DIST;
	    instr.setText("Click a start and end node to find the shortest path between them.");
	}
    }
    
    /** Listener for graph reading button */
    private class ReadGraphListener implements ActionListener {
	/** Event handler for graph reading button */
	public void actionPerformed(ActionEvent e) {
	    // Ask if user wants to save current graph
	    String s = null;
	    try {
                s = (String)JOptionPane.showInputDialog(frame, "Save current graph? (y/n)", "SAVE CHECK", JOptionPane.PLAIN_MESSAGE);
		// if yes, save graph
		if (s.equals("y")) {
		    saveGraph();
		}
            } catch (NullPointerException npe) {}
	    // clear graph before opening new one
	    if (s != null) {
		clearGraph();
		openFile = null;
		try {
		    String filename = (String)JOptionPane.showInputDialog(frame, "Type in a file name to open saved graph", "OPEN FILE", JOptionPane.PLAIN_MESSAGE);
		    openFile = filename;
		    readGraph(filename);
		} catch (NullPointerException npe) {}
	    }
	}
    }
    
    /** Listener for graph saving button */
    private class SaveGraphListener implements ActionListener {
	/** Event handler for graph saving button */
	public void actionPerformed(ActionEvent e) {
	    // Call saveGraph
	    saveGraph();
	}
    }

    /** Mouse listener for GraphCanvas element */
    private class PointMouseListener extends MouseAdapter
        implements MouseMotionListener {

        /** Responds to click event depending on mode */
        public void mouseClicked(MouseEvent e) {
            switch (mode) {
            case ADD_NODES:
		if (findNearbyNode(e.getX(), e.getY()) == null) {
		    Point p = new Point (e.getX(), e.getY());
		    // Pop up dialog box to name node
		    try {
			String s = (String)JOptionPane.showInputDialog(frame, "Please label this node:", "LABEL", JOptionPane.PLAIN_MESSAGE);
			// Add new node with the input data
			if (!s.equals(null)) {
			    canvas.graph.addNode(new DisplayNodeData<String>(s, p));
			}
		    } catch (NullPointerException npe) {}
		} else {
		    // Otherwise, emit a beep
		    Toolkit.getDefaultToolkit().beep();
		}
		break;
            case RMV_NODES:
		if (findNearbyNode(e.getX(), e.getY()) != null) {
		    // Remove from canvas
		    canvas.graph.removeNode(findNearbyNode(e.getX(), e.getY()));
		} else {
		    // Otherwise, emit a beep
		    Toolkit.getDefaultToolkit().beep();
		}
		break;
	    case ADD_EDGES:
		Toolkit.getDefaultToolkit().beep();
		break;
	    case RMV_EDGES:
		Toolkit.getDefaultToolkit().beep();
		break;
	    case GET_DIST:
		// If there's a node nearby and no node recorded, record it and highlight it
		if ((findNearbyNode(e.getX(), e.getY()) != null)&&(nodeClicked == null)) {
		    nodeClicked = findNearbyNode(e.getX(), e.getY());
		    canvas.longHighlightNode = nodeClicked;
		    canvas.longHighlight = true;
		}
		// If there's a node nearby and a node recorded, find shortest path between recorded node and current node
		else if ((findNearbyNode(e.getX(), e.getY()) != null)&&(nodeClicked != null)) {
		    DijkstraResults<DisplayNodeData<String>, Double> distInfo = canvas.graph.distance(findNearbyNode(e.getX(), e.getY()));
		    // Recursive method in DijkstraResults returns ordered nodes list
		    ArrayList<Graph<DisplayNodeData<String>, Double>.Node> orderedNodes = distInfo.order(nodeClicked);

		    // Make new string array
		    ArrayList<String> nodeNames = new ArrayList<String>();

		    // Loop over ordered array and add each node and edge to canvas
		    for (int i = 0; i <orderedNodes.size(); i++) {
			canvas.dijkstraNodes.add(orderedNodes.get(i));
			nodeNames.add(orderedNodes.get(i).getData().getData());
			if (i < orderedNodes.size()-1) {
			    canvas.dijkstraEdges.add(orderedNodes.get(i).edgeTo(orderedNodes.get(i+1)));
			}
		    }
		    canvas.highlightPath = true;
		   
		    // Build directions message
		    String directions = "Travel from "+nodeClicked.getData().getData();
		    for (int i=1; i < nodeNames.size(); i++) {
			String nodeName = nodeNames.get(i);
			directions = directions+" to "+nodeName;
		    }
		    
		    // Pop up box to tell you the shortest distance and directions
		    JOptionPane.showMessageDialog(frame, "The shortest distance between these points is "+distInfo.getCosts().get(nodeClicked)+". "+directions, "DIRECTIONS", JOptionPane.PLAIN_MESSAGE);

		    // Reset all path-highlighting related variables
		    nodeClicked = null;
		    canvas.longHighlightNode = null;
		    canvas.longHighlight = false;
		    canvas.highlightPath = false;
		    canvas.dijkstraNodes.clear();
		    canvas.dijkstraEdges.clear();
		}
                break;
            }
            canvas.repaint();
        }

        /** Records point under mousedown event in anticipation of possible drag */
        public void mousePressed(MouseEvent e) {
            // Record node under mouse, if any
	    nodeUnderMouse = findNearbyNode(e.getX(), e.getY());
        }

        /** Responds to mouseup event */
        public void mouseReleased(MouseEvent e) {
	    switch(mode) {
	    case ADD_NODES:
		// Clear record of node under mouse, if any
		if (nodeUnderMouse != null) {
		    nodeUnderMouse = null;
		}
		break;
	    case ADD_EDGES:
		// If canvas is currently drawing a line, stop drawing that line
		if (canvas.drawLine == true) {
		    canvas.drawLine = false;
		}
		// If node under mouse and node at release point, add edge
		if ((nodeUnderMouse != null)&&(findNearbyNode(e.getX(), e.getY()) != null)&&(!nodeUnderMouse.equals(findNearbyNode(e.getX(), e.getY()))&&(!nodeUnderMouse.isNeighbor(findNearbyNode(e.getX(), e.getY()))))) {
		    // Pop up dialog box to name node
		    try {
			Double d = Double.parseDouble(JOptionPane.showInputDialog(frame, "Please set distance for this edge:", "DISTANCE", JOptionPane.PLAIN_MESSAGE));
			    canvas.graph.addEdge(d, nodeUnderMouse, findNearbyNode(e.getX(), e.getY()));
		    } catch(NumberFormatException nfe) {
			Toolkit.getDefaultToolkit().beep();
		    } catch(NullPointerException npe ) {}
		}
		break;
	    case RMV_EDGES:
		// If node under mouse, and node at release point, and they share an edge, remove that edge
		if ((nodeUnderMouse != null)&&(findNearbyNode(e.getX(), e.getY()) != null)&&(nodeUnderMouse.isNeighbor(findNearbyNode(e.getX(), e.getY())))) {
		    canvas.graph.removeEdge(nodeUnderMouse, findNearbyNode(e.getX(), e.getY()));
		}
	    }
	    canvas.repaint();
        }

        /** Responds to mouse drag event */
        public void mouseDragged(MouseEvent e) {
	    switch (mode) {
	    case ADD_NODES:
		// if there is a node under the mouse, change coordinates to current mouse coordinates and update display
		if (nodeUnderMouse != null) {
		    nodeUnderMouse.getData().setPoint(new Point(e.getX(), e.getY()));
		}
		break;
		case ADD_EDGES:
		// if there is a node under the mouse, create a line from the node to the mouse
		if (nodeUnderMouse != null) {
		    canvas.startNode = nodeUnderMouse;
		    canvas.mousePoint = new Point(e.getX(), e.getY());
		    canvas.drawLine = true;
		}
	    }
	    canvas.repaint();
        }

	/** Responds to mouse move event */
        public void mouseMoved(MouseEvent e) {
	    // if there is a nearby node, highlight it
	    if (findNearbyNode(e.getX(), e.getY()) != null) {
		canvas.highlightNode = findNearbyNode(e.getX(), e.getY());
		canvas.highlight = true;
	    } else if ((findNearbyNode(e.getX(), e.getY()) == null)&&(canvas.highlight == true)) {
		canvas.highlight = false;
	    }
	    canvas.repaint();
	}
    }
}

