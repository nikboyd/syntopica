package com.educery.graphics;

/**
 * Represents a direction.
 *
 * <h4>Direction Responsibilities:</h4>
 * <ul>
 * <li>computes the points for an arrow head given the final segment of a connector</li>
 * </ul>
 */
public class Direction {

    private static final int CornerLeg = 10;
    public static final int CornerBar = 15;

    private static final Point HeadOffset = Point.at(CornerBar, 0);
    private static final Point HeadCorner = Point.at(CornerBar, CornerLeg);
    private static final Point TailCorner = Point.at(CornerLeg, CornerBar);

    public static final Upward UpWard = new Upward();
    public static final Downward DownWard = new Downward();
    public static final Leftward LeftWard = new Leftward();
    public static final Rightward RightWard = new Rightward();
    public static final Direction[] Available = {UpWard, DownWard, LeftWard, RightWard};

    public static Direction of(Point[] points) {
        if (points[0].getX() > points[1].getX()) return RightWard;
        if (points[0].getX() < points[1].getX()) return LeftWard;

        if (points[0].getY() > points[1].getY()) return DownWard;
        if (points[0].getY() < points[1].getY()) return UpWard;

        return DownWard; // x[0] == x[1] and y[0] == y[1]
    }

    // parts overridden
    public int index() { return 0; }
    public Direction flip() { return null; }
    public Point getTipOffset(int headCount) { return Point.zero(); }
    public Point getTip(Point[] points, int index) { return points[0].plus(getTipOffset(index)); }
    public Path buildArrow(Point tip, Point... pts) { return Path.from(tip, tip.plus(pts[0]), tip.plus(pts[1])); }

    // this default should be overridden, see below
    public Path buildArrow(Point[] points, int index) { return Path.from(Point.zero(), Point.zero()); }

    /**
     * Indicates an upward oriented direction.
     */
    public static class Upward extends Direction {

        @Override public int index() { return 0; }
        @Override public Direction flip() { return DownWard; }
        @Override public Point getTipOffset(int headCount) { return HeadOffset.times(headCount).flip(); }
        @Override public Path buildArrow(Point[] points, int index) {
            return buildArrow(getTip(points, index), TailCorner, TailCorner.invertX());
        }

    } // Upward

    /**
     * Indicates a downward oriented direction.
     */
    public static class Downward extends Direction {

        @Override public int index() { return 1; }
        @Override public Direction flip() { return UpWard; }
        @Override public Point getTipOffset(int headCount) { return HeadOffset.times(headCount).flip().invert(); }
        @Override public Path buildArrow(Point[] points, int index) {
            return buildArrow(getTip(points, index), TailCorner.invertY(), TailCorner.invert());
        }

    } // Downward

    /**
     * Indicates a leftward oriented direction.
     */
    public static class Leftward extends Direction {

        @Override public int index() { return 2; }
        @Override public Direction flip() { return RightWard; }
        @Override public Point getTipOffset(int headCount) { return HeadOffset.times(headCount); }
        @Override public Path buildArrow(Point[] points, int index) {
            return buildArrow(getTip(points, index), HeadCorner, HeadCorner.invertY());
        }

    } // Leftward

    /**
     * Indicates a rightward oriented direction.
     */
    public static class Rightward extends Direction {

        @Override public int index() { return 3; }
        @Override public Direction flip() { return LeftWard; }
        @Override public Point getTipOffset(int headCount) { return HeadOffset.times(headCount).invert(); }
        @Override public Path buildArrow(Point[] points, int index) {
            return buildArrow(getTip(points, index), HeadCorner.invertX(), HeadCorner.invert());
        }

    } // Rightward

} // Orientation
