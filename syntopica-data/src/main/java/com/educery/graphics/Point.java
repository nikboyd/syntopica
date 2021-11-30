package com.educery.graphics;

import java.util.*;
import static com.educery.utils.Utils.*;

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

    private final int[] positions = { 0, 0 };

    public Point() { }
    public Point(int x, int y) {
        this.positions[0] = x;
        this.positions[1] = y;
    }

    public static Point zero() { return new Point(); }
    public static Point unity() { return Point.at(1, 1); }
    public static Point at(int x, int y) { return new Point(x, y); }

    public int combine(int factor) { return (getY() * factor) + getX(); }

    public int getX() { return this.positions[0]; }
    public void setX(int x) { this.positions[0] = x; }

    public int getY() { return this.positions[1]; }
    public void setY(int y) { this.positions[1] = y; }

    public Point invert() { return this.times(-1); }
    public Point times(int factor) { return Point.at(getX() * factor, getY() * factor); }
    public Point times(Point p) { return Point.at(getX() * p.getX(), getY() * p.getY()); }
    public Point reduced(int factor) { return (factor == 0) ? new Point() : Point.at(getX() / factor, getY() / factor); }

    public Point flip() { return Point.at(getY(), getX()); }
    public Point signs() { return Point.at((int) Math.signum(getX()), (int) Math.signum(getY())); }

    public Point invertX() { return Point.at(-getX(), getY()); }
    public Point invertY() { return Point.at(getX(), -getY()); }

    public Point minus(Point p) { return Point.at(getX() - p.getX(), getY() - p.getY()); }
    public Point plus(Point p) { return Point.at(getX() + p.getX(), getY() + p.getY()); }

    public String format() { return getX() + "," + getY(); }

    public boolean resembles(Point p) { return p.getX() == getX() && p.getY() == getY(); }
    @Override public boolean equals(Object candidate) {
        return hasSome(candidate) && getClass().isInstance(candidate) && resembles((Point) candidate); }

    @Override public int hashCode() { int hash = 3; hash = 19 * hash + Arrays.hashCode(this.positions); return hash; }

} // Point
