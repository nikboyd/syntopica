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

    /**
     * Returns the direction of a given line segment.
     *
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
     * Returns the opposite of this direction.
     *
     * @return a Direction
     */
    public Direction flip() {
        return null; // override this
    }

    /**
     * Returns the index of this direction.
     *
     * @return a direction index
     */
    public int index() {
        return 0;
    }

    /**
     * Returns a tip offset amount.
     *
     * @param headCount a head count
     * @return a Point
     */
    public Point getTipOffset(int headCount) {
        return Point.at(0, 0);
    }

    /**
     * Builds the path of an arrow head.
     *
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
     *
     * @param tip the arrow tip vertex
     * @param offsets the offsets of the arrow corners
     * @return a Path
     */
    protected Path buildArrow(Point tip, Point... offsets) {
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
        public Direction flip() {
            return DownWard;
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
        public Direction flip() {
            return UpWard;
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
        public Point getTipOffset(int headCount) {
            return HeadOffset.times(headCount);
        }

        @Override
        public Path buildArrow(Point[] points, int index) {
            Point tip = points[0].plus(getTipOffset(index));
            return buildArrow(tip, HeadCorner, HeadCorner.invertY());
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
        public Point getTipOffset(int headCount) {
            return HeadOffset.times(headCount).invert();
        }

        @Override
        public Path buildArrow(Point[] points, int index) {
            Point tip = points[0].plus(getTipOffset(index));
            return buildArrow(tip, HeadCorner.invertX(), HeadCorner.invert());
        }

    } // Rightward

} // Orientation
