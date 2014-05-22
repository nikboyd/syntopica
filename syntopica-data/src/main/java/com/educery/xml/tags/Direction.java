package com.educery.xml.tags;

import com.educery.xml.tags.Edge.Index;

/**
 * Represents a direction.
 * 
 * <h4>Direction Responsibilities:</h4>
 * <ul>
 * <li>computes the points for an arrow head given the final segment of a connector</li>
 * <li>computes the path for a connector given the locations of its model elements</li>
 * </ul>
 */
public class Direction {

	private static final int CornerLeg = 10;
	private static final int CornerBar = 15;
	
	private static final Point HeadOffset = Point.at(CornerBar, 0);
	private static final Point HeadCorner = Point.at(CornerBar, CornerLeg);
	private static final Point TailCorner = Point.at(CornerLeg, CornerBar);
	
	public static final Upward UpWard = new Upward();
	public static final Downward DownWard = new Downward();
	public static final Leftward LeftWard = new Leftward();
	public static final Rightward RightWard = new Rightward();
	public static final Direction[] Available = { UpWard, DownWard, LeftWard, RightWard };
	
	public static final OneSegmentPath OneSegment = new OneSegmentPath();
	public static final TwoSegmentPath TwoSegment = new TwoSegmentPath();
	public static final ThreeSegmentPath ThreeSegment = new ThreeSegmentPath();
	
	/**
	 * Returns a direction appropriate to a path head.
	 * @param path a path
	 * @return a Direction
	 */
	public static Direction ofHead(Path path) {
		return Direction.of(path.getHead());
	}
	
	/**
	 * Returns a direction appropriate to a path tail.
	 * @param path a path
	 * @return a Direction
	 */
	public static Direction ofTail(Path path) {
		return Direction.of(path.getTail());
	}
	
	/**
	 * Returns the direction of a given line segment.
	 * @param points a line segment
	 * @return a Direction
	 */
	public static Direction of(Point[] points) {
		if (points[0].getX() > points[1].getX()) {
			return RightWard;
		}

		if (points[0].getX() < points[1].getX()) {
			return LeftWard;
		}

		// x[0] == x[1]
		if (points[0].getY() > points[1].getY()) {
			return DownWard;
		}

		if (points[0].getY() < points[1].getY()) {
			return UpWard;
		}

		return DownWard;
	}
	
	/**
	 * Returns a direction appropriate to the placement of a pair of model elements.
	 * @param elements the model elements
	 * @return a Direction
	 */
	public static Direction between(ModelElement ... elements) {
		Point tip = Point.at(elements[0].getCenter(), elements[0].getMiddle());
		Point end = Point.at(elements[1].getCenter(), elements[1].getMiddle());
		Point signed = end.minus(tip);

		if (tip.getY() < end.getY()) {
			Direction result = UpWard;
			Edge edge = elements[0].getEdge(result.flip().edgeIndex());
			if (edge.accepts(tip, end)) {
				tip = edge.assign(tip, end);
			}
			else {
				result = result.alternate(tip, end);
				edge = elements[0].getEdge(result.edgeIndex());
				tip = edge.assign(tip, end);
			}
			return (edge.accepts(tip, end) ? result : result.alternate(tip, end));
		}
		else
		if (tip.getY() > end.getY()) {
			Direction result = DownWard;
			Edge edge = elements[0].getEdge(UpWard.edgeIndex());
			return (edge.accepts(tip, end) ? result : result.alternate(tip, end));
		}
		else // tipY == endY
		if (tip.getX() < end.getX()) {
			Direction result = LeftWard;
			Edge edge = elements[0].getEdge(RightWard.edgeIndex());
			return (edge.accepts(tip, end) ? result : result.alternate(tip, end));
		}
		else {
			Direction result = RightWard;
			Edge edge = elements[0].getEdge(LeftWard.edgeIndex());
			return (edge.accepts(tip, end) ? result : result.alternate(tip, end));
		}
	}
	
	/**
	 * Returns the opposite of this direction.
	 * @return a Direction
	 */
	public Direction flip() {
		return null; // override this
	}
	
	/**
	 * Returns the index of this direction.
	 * @return a direction index
	 */
	public int index() {
		return 0;
	}
	
	public Index edgeIndex() {
		return Index.Top;
	}
	
	public Direction alternate(Point tip, Point end) {
		return DownWard;
	}
	
	/**
	 * Builds a path between a pair of model elements.
	 * @param elements the model elements
	 * @return a Path
	 */
	public Path pathBetween(ModelElement ...  elements) {
		// this default should be overridden
		return Path.from(Point.at(0, 0), Point.at(0, 0));
	}
	
	/**
	 * Returns a tip offset amount.
	 * @param headCount a head count
	 * @return a Point
	 */
	public Point getTipOffset(int headCount) {
		return Point.at(0, 0);
	}
	
	/**
	 * Builds the path of an arrow head.
	 * @param points the final segment points
	 * @param index an arrow index
	 * @return a Path
	 */
	public Path buildArrow(Point[] points, int index) {
		// this default should be overridden
		return Path.from(Point.at(0, 0), Point.at(0, 0));
	}
	
	/**
	 * Forms the path of an arrow head.
	 * @param tip the arrow tip vertex
	 * @param offsets the offsets of the arrow corners
	 * @return a Path
	 */
	protected Path buildArrow(Point tip, Point ... offsets) {
		// this default should be overridden
		return Path.from(tip, tip.plus(offsets[0]), tip.plus(offsets[1]));
	}

	
	/**
	 * Indicates an upward oriented direction.
	 */
	public static class Upward extends Direction {
		
		@Override
		public int index() {
			return 0;
		}
		
		@Override
		public Index edgeIndex() {
			return Index.Top;
		}
		
		@Override
		public Direction flip() {
			return DownWard;
		}
		
		@Override
		public Direction alternate(Point tip, Point end) {
			return (tip.getX() < end.getX() ? LeftWard : RightWard);
		}
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).flip();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, TailCorner, TailCorner.invertX());
		}
		
		@Override
		public Path pathBetween(ModelElement ... elements) {
			elements[0].getEdge(edgeIndex()).isEmpty();
			int testX = elements[0].getCenter();
			if (testX > elements[1].getCenter()) {
				
				return (testX > elements[1].getRight() + CornerBar) ?
						RightWard.pathUpBetween(elements) :
						pathOffCenterBetween(elements);
			}

			if (testX < elements[1].getCenter()) {
				return (testX < elements[1].getLeft() - CornerBar) ?
						LeftWard.pathUpBetween(elements) :
						pathOffCenterBetween(elements);
			}

			return OneSegment.pathUpBetween(elements);
		}
		
		public Path pathCenterBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getCenter(), elements[1].getTop());
			return Path.from(tip, end);
		}
		
		public Path pathOffCenterBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getCenter(), elements[1].getTop());
			int midY = end.getY() - ((end.getY() - tip.getY()) / 3);
			Point p = Point.at(tip.getX(), midY);
			Point q = Point.at(end.getX(), midY);
			return Path.from(tip, p, q, end);
		}
		
	} // Upward
	
	/**
	 * Indicates a downward oriented direction.
	 */
	public static class Downward extends Direction {
		
		@Override
		public int index() {
			return 1;
		}
		
		@Override
		public Index edgeIndex() {
			return Index.Bottom;
		}
		
		@Override
		public Direction flip() {
			return UpWard;
		}
		
		@Override
		public Direction alternate(Point tip, Point end) {
			return (tip.getX() < end.getX() ? LeftWard : RightWard);
		}
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).flip().invert();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, TailCorner.invertY(), TailCorner.invert());
		}
		
		@Override
		public Path pathBetween(ModelElement ...  elements) {
			int testX = elements[0].getCenter();
			if (testX > elements[1].getCenter()) {
				return (testX > elements[1].getRight() + CornerBar) ?
						RightWard.pathDownBetween(elements) :
						pathOffCenterBetween(elements);
			}

			if (testX < elements[1].getCenter()) {
				return (testX < elements[1].getLeft() - CornerBar) ?
						LeftWard.pathDownBetween(elements) :
						pathOffCenterBetween(elements);
			}

			return pathCenterBetween(elements);
		}
		
		public Path pathCenterBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getCenter(), elements[1].getBottom());
			return Path.from(tip, end);
		}
		
		public Path pathOffCenterBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getCenter(), elements[1].getBottom());
			int midY = ((tip.getY() - end.getY()) / 3) + end.getY();
			Point p = Point.at(tip.getX(), midY);
			Point q = Point.at(end.getX(), midY);
			return Path.from(tip, p, q, end);
		}
		
	} // Downward
	
	/**
	 * Indicates a leftward oriented direction.
	 */
	public static class Leftward extends Direction {
		
		@Override
		public int index() {
			return 2;
		}
		
		@Override
		public Direction flip() {
			return RightWard;
		}
		
		@Override
		public Direction alternate(Point tip, Point end) {
			return (tip.getY() < end.getY() ? UpWard : DownWard);
		}
		
		@Override
		public Index edgeIndex() {
			return Index.Left;
		}
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount);
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, HeadCorner, HeadCorner.invertY());
		}
		
		@Override
		public Path pathBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getRight(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			return Path.from(tip, end);
		}
		
		public Path pathUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}
		
		public Path pathDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}

	} // Leftward
	
	/**
	 * Indicates a rightward oriented direction.
	 */
	public static class Rightward extends Direction {
		
		@Override
		public int index() {
			return 3;
		}
		
		@Override
		public Direction flip() {
			return LeftWard;
		}
		
		@Override
		public Direction alternate(Point tip, Point end) {
			return (tip.getY() < end.getY() ? UpWard : DownWard);
		}
		
		@Override
		public Index edgeIndex() {
			return Index.Right;
		}
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).invert();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, HeadCorner.invertX(), HeadCorner.invert());
		}
		
		@Override
		public Path pathBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getLeft(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			return Path.from(tip, end);
		}
		
		public Path pathUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}

		public Path pathDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}
				
	} // Rightward
	
	public static class OneSegmentPath {
		
		public Path pathLeftBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getRight(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			return Path.from(tip, end);
		}
		
		public Path pathRightBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getLeft(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			return Path.from(tip, end);
		}
		
		public Path pathUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getCenter(), elements[1].getTop());
			return Path.from(tip, end);
		}
		
		public Path pathDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getCenter(), elements[1].getBottom());
			return Path.from(tip, end);
		}
		
	} // OneSegmentPath
	
	public static class TwoSegmentPath {
		
		public Path pathRightUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}

		public Path pathRightDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}
		
		public Path pathLeftUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}
		
		public Path pathLeftDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			Point mid = Point.at(tip.getX(), end.getY());
			return Path.from(tip, mid, end);
		}
		
	} // TwoSegmentPath
	
	public static class ThreeSegmentPath {
		
		public Path pathCenterUpBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getBottom());
			Point end = Point.at(elements[1].getCenter(), elements[1].getTop());
			int midY = end.getY() - ((end.getY() - tip.getY()) / 3);
			Point p = Point.at(tip.getX(), midY);
			Point q = Point.at(end.getX(), midY);
			return Path.from(tip, p, q, end);
		}

		public Path pathCenterDownBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
			Point end = Point.at(elements[1].getCenter(), elements[1].getBottom());
			int midY = end.getY() + ((tip.getY() - end.getY()) / 3);
			Point p = Point.at(tip.getX(), midY);
			Point q = Point.at(end.getX(), midY);
			return Path.from(tip, p, q, end);
		}
		
		public Path pathMiddleLeftBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getRight(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getLeft(), elements[1].getMiddle());
			int midX = end.getX() - ((end.getX() - tip.getX()) / 3);
			Point p = Point.at(midX, tip.getY());
			Point q = Point.at(midX, end.getY());
			return Path.from(tip, p, q, end);
		}

		public Path pathMiddleRightBetween(ModelElement ... elements) {
			Point tip = Point.at(elements[0].getLeft(), elements[0].getMiddle());
			Point end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			int midX = end.getX() + ((tip.getX() - end.getX()) / 3);
			Point p = Point.at(midX, tip.getY());
			Point q = Point.at(midX, end.getY());
			return Path.from(tip, p, q, end);
		}
		
	} // ThreeSegmentPath

} // Orientation