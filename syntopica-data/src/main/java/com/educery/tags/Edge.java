package com.educery.tags;

import java.util.*;

import com.educery.graphics.Point;
import com.educery.tags.Anchor;

/**
 * A model element (rectangle) edge. Each edge contains three anchors.
 * 
 * <h4>Edge Responsibilities:</h4>
 * <ul>
 * <li>knows its three anchors and their locations</li>
 * <li>assigns the best available anchor to each connection</li>
 * </ul>
 * 
 * @see Anchor
 * @see ModelElement
 */
public class Edge {

	private static final int Center = 1;
	private static final int Anchors = 3;
	static final int OptimalY = 2 * ModelElement.Height;
	
	/**
	 * Indicates a direction (2-dimensional).
	 */
	public static enum Index {
		Left, Right, Top, Bottom
	}

	/**
	 * Indicates either a vertical or horizontal orientation.
	 */
	public static enum Orientation {
		Horizontal,
		Vertical
	}
	
	/**
	 * Indicates the orientation of the available directions.
	 */
	public static final Orientation[] Directions = {
		Orientation.Vertical, 
		Orientation.Vertical, 
		Orientation.Horizontal, 
		Orientation.Horizontal, 
	};

	private Index index;
	private Anchor[] anchors = { new Anchor(),  new Anchor(),  new Anchor() };
	private HashMap<String, Anchor> anchorMap = new HashMap<String, Anchor>();
	
	/**
	 * Constructs a new Edge.
	 * @param index indicates which edge is this
	 */
	public Edge(int index) {
		this.index = Index.values()[index];
	}
	
	/**
	 * The edge index.
	 * @return an index
	 */
	public Index getIndex() {
		return this.index;
	}
	
	/**
	 * Returns the anchor at a point.
	 * @param p a point
	 * @return an Anchor
	 */
	public Anchor getAnchor(Point p) {
		return this.anchorMap.get(p.format());
	}
	
	/**
	 * Sets the locations of the anchors for this edge.
	 * @param p the center anchor location
	 * @param length the length of this edge
	 */
	public void setCenter(Point p, int length) {
		Orientation o = Directions[getIndex().ordinal()];
		Point delta = (o == Orientation.Horizontal ? 
						Point.at(length / Anchors, 0) : 
						Point.at(0, length / Anchors));
		
		for (int index = 0; index < Anchors; index++) {
			Point location = p.plus(delta.times(index - Center));
			this.anchors[index].setLocation(location);
			this.anchorMap.put(location.format(), this.anchors[index]);
		}
	}
	
	/**
	 * Assigns the best available anchor to a connector tip.
	 * @param tip a connector tip
	 * @param end a connector end
	 * @return an Anchor, or null
	 */
	public Point assign(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result == null ? null : result.getLocation());
	}
	
	/**
	 * Indicates whether a best available anchor exists.
	 * @param tip a connector tip
	 * @param end a connector end
	 * @return whether a best available anchor exists
	 */
	public boolean accepts(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result != null);
	}

	/**
	 * Returns the allocated connectors.
	 * @return a Connector list
	 */
	public List<Connector> getConnectors() {
		ArrayList<Connector> results = new ArrayList<Connector>();
		for (Anchor a : this.anchors) {
			if (a.count() > 0) {
				results.addAll(Arrays.asList(a.getConnectors()));
			}
		}
		return results;
	}

	/**
	 * Counts the allocated connectors.
	 * @return a connector count
	 */
	public int count() {
		int result = 0;
		for (Anchor a : this.anchors) {
			result += a.count();
		}
		return result;
	}
	
	/**
	 * Indicates whether any connectors have been acquired by this edge.
	 * @return whether this edge has been connected yet
	 */
	public boolean isEmpty() {
		return count() == 0;
	}

	/**
	 * Returns the best available anchor given a connection.
	 * @param tip a connection tip
	 * @param end a connection end
	 * @return an Anchor, or null
	 */
	private Anchor getBestAnchor(Point tip, Point end) {
		Anchor result = getAnchor(Center);
		if (result.isEmpty()) return result;

		Point pole = end.minus(tip).signs();
		if (getIndex().ordinal() < Index.Top.ordinal()) {
			result = getAnchor(Center + pole.getY());
			if (result.isEmpty()) return result;
		}
		else {
			result = getAnchor(Center + pole.getX());
			if (result.isEmpty()) return result;
		}

		return null;
	}
	
	private Anchor getAnchor(int index) {
		return this.anchors[index];
	}

} // Edge