package com.educery.graphics;

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

    private int[] positions = {0, 0};

    /**
     * Returns the zero point.
     *
     * @return a Point
     */
    public static Point zero() {
        return new Point();
    }

    /**
     * Returns the unity point.
     *
     * @return a Point
     */
    public static Point unity() {
        return Point.at(1, 1);
    }

    /**
     * Returns a new Point.
     *
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
    }

    /**
     * Constructs a new Point.
     *
     * @param x a location
     * @param y a location
     */
    public Point(int x, int y) {
        this.positions[0] = x;
        this.positions[1] = y;
    }

    /**
     * Combines the X and Y elements with a factor.
     *
     * @param factor a factor
     * @return a combined result
     */
    public int combine(int factor) {
        return (getY() * factor) + getX();
    }

    /**
     * An X position
     *
     * @return X
     */
    public int getX() {
        return this.positions[0];
    }

    public void setX(int x) {
        this.positions[0] = x;
    }

    /**
     * An Y position
     *
     * @return Y
     */
    public int getY() {
        return this.positions[1];
    }

    public void setY(int y) {
        this.positions[1] = y;
    }

    /**
     * Computes a division.
     *
     * @param factor a factor
     * @return a new Point
     */
    public Point reduced(int factor) {
        if (factor == 0) {
            return new Point();
        }
        return Point.at(getX() / factor, getY() / factor);
    }

    /**
     * Computes a product.
     *
     * @param factor a factor
     * @return a new Point
     */
    public Point times(int factor) {
        return Point.at(getX() * factor, getY() * factor);
    }

    /**
     * Computes the product of this point and another.
     *
     * @param p a point
     * @return a new Point
     */
    public Point times(Point p) {
        return Point.at(getX() * p.getX(), getY() * p.getY());
    }

    /**
     * Transposes X and Y.
     *
     * @return a new Point
     */
    public Point flip() {
        return Point.at(getY(), getX());
    }

    /**
     * The signs of X and Y.
     *
     * @return a new Point
     */
    public Point signs() {
        return Point.at((int) Math.signum(getX()), (int) Math.signum(getY()));
    }

    /**
     * Inverts the X and Y values.
     *
     * @return a new Point
     */
    public Point invert() {
        return this.times(-1);
    }

    /**
     * Inverts the X value.
     *
     * @return a new Point
     */
    public Point invertX() {
        return Point.at(-getX(), getY());
    }

    /**
     * Inverts the Y value.
     *
     * @return a new Point
     */
    public Point invertY() {
        return Point.at(getX(), -getY());
    }

    /**
     * Subtracts a point from this.
     *
     * @param p a point
     * @return a new Point
     */
    public Point minus(Point p) {
        return Point.at(getX() - p.getX(), getY() - p.getY());
    }

    /**
     * Adds a point to this.
     *
     * @param p a point
     * @return a new Point
     */
    public Point plus(Point p) {
        return Point.at(getX() + p.getX(), getY() + p.getY());
    }

    /**
     * Formats this point.
     *
     * @return a formatted point
     */
    public String format() {
        return getX() + "," + getY();
    }

    @Override
    public boolean equals(Object candidate) {
        if (null == candidate) {
            return false;
        }
        if (!(candidate instanceof Point)) {
            return false;
        }
        Point p = (Point) candidate;
        return p.getX() == getX() && p.getY() == getY();
    }

} // Point
