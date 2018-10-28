import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * Data class for displaying nodes
 *
 * @author Emma Jordan
 * @version 12/15/16
 *
 */
public class DisplayNodeData<V> {
    
    /** Data */
    private V data;

    /** Coordinates */
    private Point point;

    /** Constructor */
    DisplayNodeData (V data, Point point) {
	this.data = data;
	this.point = point;
    }

    /** Accessor for data */
    public V getData() {
	return this.data;
    }

    /** Accessor for point */
    public Point getPoint() {
	return this.point;
    }

    /** Manipulator for data */
    public void setData(V data) {
	this.data = data;
    }

    /** Manipulator for point */
    public void setPoint(Point point) {
	this.point = point;
    }
}