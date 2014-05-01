package com.educery.xml.tags;

/**
 * A 2-dimensional point.
 * 
 * <h4>Point Responsibilities:</h4>
 * <ul>
 * <li>knows X and Y locations</li>
 * <li>operates on points in combinations</li>
 * </ul>
 */
public class Point {
	
	private int[] positions = { 0, 0 };
	
	/**
	 * Returns a new Point.
	 * @param x a location
	 * @param y a location
	 * @return a new Point
	 */
	public static Point at(int x, int y) {
		return new Point(x, y);
	}
	
	/**
	 * Constructs a new Point.
	 */
	public Point() {
		this.positions[0] = 0;
		this.positions[1] = 0;
	}
	
	/**
	 * Constructs a new Point.
	 * @param x a location
	 * @param y a location
	 */
	public Point(int x, int y) {
		this.positions[0] = x;
		this.positions[1] = y;
	}
	
	/**
	 * An X position
	 * @return X
	 */
	public int getX() {
		return this.positions[0];
	}

	/**
	 * An Y position
	 * @return Y
	 */
	public int getY() {
		return this.positions[1];
	}

	/**
	 * Computes a product.
	 * @param factor a factor
	 * @return a new Point
	 */
	public Point times(int factor) {
		return Point.at(getX() * factor, getY() * factor);
	}

	/**
	 * Transposes X and Y.
	 * @return a new Point
	 */
	public Point flip() {
		return Point.at(getY(), getX());
	}
	
	/**
	 * Inverts the X and Y values.
	 * @return a new Point
	 */
	public Point invert() {
		return Point.at(-getX(), -getY());
	}
	
	/**
	 * Inverts the X value.
	 * @return a new Point
	 */
	public Point invertX() {
		return Point.at(-getX(), getY());
	}
	
	/**
	 * Inverts the Y value.
	 * @return a new Point
	 */
	public Point invertY() {
		return Point.at(getX(), -getY());
	}
	
	/**
	 * Subtracts a point from this.
	 * @param p a point
	 * @return a new Point
	 */
	public Point minus(Point p) {
		return Point.at(getX() - p.getX(), getY() - p.getY());
	}
	
	/**
	 * Adds a point to this.
	 * @param p a point
	 * @return a new Point
	 */
	public Point plus(Point p) {
		return Point.at(getX() + p.getX(), getY() + p.getY());
	}
	
	/**
	 * Formats this point.
	 * @return a formatted point
	 */
	public String format() {
		return getX() + "," + getY();
	}

} // Point