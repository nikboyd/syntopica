package com.educery.xml.tags;

/**
 * Represents an orientation for an arrow head at the end of a connector.
 * 
 * <h4>Orientation Responsibilities:</h4>
 * <ul>
 * <li>computes the points for an arrow head given the final segment of a connector</li>
 * </ul>
 */
public class Orientation {

	private static final int CornerLeg = 10;
	private static final int CornerBar = 15;
	
	private static final Point HeadOffset = Point.at(CornerBar, 0);
	private static final Point HeadCorner = Point.at(CornerBar, CornerLeg);
	private static final Point TailCorner = Point.at(CornerLeg, CornerBar);
	
	private static final Orientation UpWard = new Upward();
	private static final Orientation DownWard = new Downward();
	private static final Orientation LeftWard = new Leftward();
	private static final Orientation RightWard = new Rightward();
	
	/**
	 * Returns an orientation appropriate to a path.
	 * @param path a path
	 * @return an Orientation
	 */
	public static Orientation of(Path path) {
		Point[] points = path.getHead();
		if (points[0].getX() > points[1].getX()) {
			return RightWard;
		}
		else 
		if (points[0].getX() < points[1].getX()) {
			return LeftWard;
		}
		else // x[0] == x[1]
		if (points[0].getY() > points[1].getY()) {
			return DownWard;
		}
		else 
		if (points[0].getY() < points[1].getY()) {
			return UpWard;
		}
		else {
			return DownWard;
		}
	}
	
	/**
	 * Indicates an upward oriented arrow head.
	 */
	public static class Upward extends Orientation {
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).flip();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, TailCorner, TailCorner.invertX());
		}
		
	}
	
	/**
	 * Indicates an downward oriented arrow head.
	 */
	public static class Downward extends Orientation {
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).flip().invert();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, TailCorner.invertY(), TailCorner.invert());
		}
		
	}
	
	/**
	 * Indicates an leftward oriented arrow head.
	 */
	public static class Leftward extends Orientation {
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount);
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, HeadCorner, HeadCorner.invertY());
		}
		
	}
	
	/**
	 * Indicates an rightward oriented arrow head.
	 */
	public static class Rightward extends Orientation {
		
		@Override
		public Point getTipOffset(int headCount) {
			return HeadOffset.times(headCount).invert();
		}
		
		@Override
		public Path buildArrow(Point[] points, int index) {
			Point tip = points[0].plus(getTipOffset(index));
			return buildArrow(tip, HeadCorner.invertX(), HeadCorner.invert());
		}
				
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

} // Orientation