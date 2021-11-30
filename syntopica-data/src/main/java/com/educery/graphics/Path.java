package com.educery.graphics;

import java.util.*;
import static com.educery.utils.Utils.*;
import static com.educery.utils.LineBuilder.*;
import com.educery.utils.Logging;

/**
 * A path of connected points.
 *
 * <h4>Path Responsibilities:</h4>
 * <ul>
 * <li>knows the points that form a connected path</li>
 * <li>provides copies of the head and tail segments</li>
 * </ul>
 */
public class Path implements Logging {

    private final ArrayList<Point> points = new ArrayList();
    public List<Point> points() { return this.points; }
    public int length() { return points().size(); }
    public int end() { return length() - 1; }

    private Path() { }
    private Path(Point... points) { this(wrap(points)); }
    private Path(List<Point> points) { this(); this.points.addAll(points); }
    public static Path from(Point... points) { return from(wrap(points)); }
    public static Path from(List<Point> points) { return (points.size() < 2) ? null : new Path(points); }

    public Path reverse() {
        ArrayList<Point> copy = new ArrayList(points());
        Collections.reverse(copy); return Path.from(copy); }

    // replace head, keep all remaining points
    public Path withHead(Point head) { Point[] copy = getPoints(); copy[0] = head; return Path.from(copy); }
    public Path with(Point... points) { points().addAll(wrap(points)); return this; }

    public Point getTip() { return points().get(0); }
    public Point getEnd() { return points().get(end()); }

    int[] HeadSpots = { 0, 1 };
    public Point[] getHead() { return getSpots(HeadSpots); }
    public Direction getDirection() { return Direction.of(getHead()); }

    int[] tailSpots() { int[] spots = { end(), end() - 1 }; return spots; }
    public Point[] getTail() { return getSpots(tailSpots()); }

    private Point[] getSpots(int[] spots) { Point[] ps = { points().get(spots[0]), points().get(spots[1]) }; return ps; }

    static Point[] NoPoints = { };
    public Point[] getPoints() { return unwrap(points(), NoPoints); }

    public String format() { return build(b -> { points().forEach(p -> { b.tieAfterSome(Blank); b.tie(p.format()); }); }); }

} // Path
