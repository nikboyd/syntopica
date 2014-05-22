package com.educery.xml.tags;

import java.util.*;

/**
 * A path of connected points.
 * 
 * <h4>Path Responsibilities:</h4>
 * <ul>
 * <li>knows the points that form a connected path</li>
 * <li>provides copies of the head and tail segments</li>
 * </ul>
 */
public class Path {
	
	private ArrayList<Point> points = new ArrayList<Point>();
	
	/**
	 * Returns a new Path.
	 * @param points the points that form a path
	 * @return a new Path
	 */
	public static Path from(List<Point> points) {
		if (points.size() < 2) return null;
		Path result = new Path();
		result.points.addAll(points);
		return result;
	}
	
	/**
	 * Returns a new Path.
	 * @param points the points that form a path
	 * @return a new Path
	 */
	public static Path from(Point ... points) {
		if (points.length < 2) return null;
		return Path.from(Arrays.asList(points));
	}
	
	private Path() { }
	
	/**
	 * The length of this path.
	 * @return a point count
	 */
	public int length() {
		return this.points.size();
	}
	
	/**
	 * Returns a new Path which reverses this one.
	 * @return a new Path
	 */
	public Path reverse() {
		ArrayList<Point> results = new ArrayList<Point>(this.points);
		Collections.reverse(results);
		return Path.from(results);
	}
	
	/**
	 * Returns the overall direction of this path.
	 * @return a Direction
	 */
	public Direction getDirection() {
		return Direction.ofHead(this);
	}
	
	/**
	 * Returns a new Path derived from this one.
	 * @param head a new end point to replace the head in a copy of this path
	 * @return a new Path
	 */
	public Path withHead(Point head) {
		Point[] copy = this.points.stream().toArray(Point[]::new);
		copy[0] = head;
		return Path.from(copy);
	}
	
	/**
	 * Adds points to this path.
	 * @param points the additional points
	 * @return this Path
	 */
	public Path with(Point ... points) {
		this.points.addAll(Arrays.asList(points));
		return this;
	}

	/**
	 * Returns the tip of this path.
	 * @return a Point
	 */
	public Point getTip() {
		return this.points.get(0);
	}
	
	public Point getEnd() {
		return this.points.get(this.points.size() - 1);
	}
	
	/**
	 * Returns the initial (head) segment of this path.
	 * @return a (initial) pair of points
	 */
	public Point[] getHead() {
		int[] spots = { 0, 1 };
		return getSpots(spots);
	}
	
	/**
	 * Returns the final (tail) segment of this path.
	 * @return a (final) pair of points
	 */
	public Point[] getTail() {
		int last = this.points.size() - 1;
		int[] spots = { last, last - 1 };
		return getSpots(spots);
	}
	
	public Point[] getPoints() {
		return this.points.stream().toArray(Point[]::new);
	}
	
	/**
	 * Formats this path as a sequence of points.
	 * @return a formatted sequence of points
	 */
	public String format() {
		StringBuilder builder = new StringBuilder();
		for (Point p : this.points) {
			if (builder.length() > 0) builder.append(" ");
			builder.append(p.format());
		}
		return builder.toString();
	}
	
	private Point[] getSpots(int[] spots) {
		Point[] results = { this.points.get(spots[0]), this.points.get(spots[1]) };
		return results;
	}

} // Path
