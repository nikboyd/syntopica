package com.educery.tags;

import java.util.ArrayList;
import java.util.Arrays;

import com.educery.graphics.Point;

/**
 * A connector anchor. Each anchor "staples" a connector end point to the edge of a model element.
 * 
 * <h4>Anchor Responsibilities:</h4>
 * <ul>
 * <li>knows a point of connection</li>
 * <li>knows the attached connectors</li>
 * </ul>
 * 
 * @see Connector
 * @see Edge
 * @see ModelElement
 */
public class Anchor {
	
	private Point location = new Point();
	private ArrayList<Connector> connectors = new ArrayList<Connector>();

	/**
	 * The connection point.
	 * @param p a connection point
	 */
	public void setLocation(Point p) {
		this.location.setX(p.getX());
		this.location.setY(p.getY());
	}
	
	/**
	 * The connection point.
	 */
	public Point getLocation() {
		return this.location;
	}

	/**
	 * Indicates whether this anchor has any connectors.
	 * @return whether this anchor has any connectors
	 */
	public boolean isEmpty() {
		return this.connectors.isEmpty();
	}

	/**
	 * A connector count.
	 * @return a count
	 */
	public int count() {
		return this.connectors.size();
	}

	/**
	 * Adds connectors to this anchor.
	 * @param c the connectors
	 */
	public void add(Connector ... c) {
		this.connectors.addAll(Arrays.asList(c));
	}
	
	/**
	 * The connectors attached to this anchor.
	 * @return the Connectors, or empty
	 */
	public Connector[] getConnectors() {
		return this.connectors.stream().toArray(Connector[]::new);
	}
	
} // Anchor
