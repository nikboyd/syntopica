package com.educery.tags;

import java.util.*;

import com.educery.graphics.Point;
import static com.educery.utils.Utils.*;

/**
 * A model element (rectangle) edge. Each edge contains three anchors.
 *
 * <h4>Edge Responsibilities:</h4>
 * <ul>
 * <li>knows its three anchors and their locations</li>
 * <li>assigns the best available anchor to each connection</li>
 * </ul>
 *
 * @see Anchor
 * @see ModelElement
 */
public class Edge {

    private static final int Center = 1;
    private static final int Anchors = 3;
    static final int OptimalY = 2 * ModelElement.ModelHeight;

    /**
     * Indicates a direction (2-dimensional).
     */
    public static enum Index { Left, Right, Top, Bottom }

    /**
     * Indicates either a vertical or horizontal orientation.
     */
    public static enum Orientation { Horizontal, Vertical }

    /**
     * Indicates the orientation of the available directions.
     */
    public static final Orientation[] Directions = {
        Orientation.Vertical,
        Orientation.Vertical,
        Orientation.Horizontal,
        Orientation.Horizontal,
    };

    private final Index index;
    public Edge(int index) { this.index = Index.values()[index]; }
    public Index getIndex() { return this.index; }

    private final Anchor[] anchors = { new Anchor(), new Anchor(), new Anchor() };
    public int count() { int result = 0; for (Anchor a : this.anchors) result += a.count(); return result; }
    private Anchor getAnchor(int index) { return this.anchors[index]; }
    public boolean isEmpty() { return count() == 0; }
    public List<Connector> getConnectors() {
        ArrayList<Connector> results = emptyList();
        for (Anchor a : this.anchors) {
            if (a.count() > 0) results.addAll(wrap(a.getConnectors()));
        }
        return results;
    }

    private final HashMap<String, Anchor> anchorMap = new HashMap();
    public Anchor getAnchor(Point p) { return this.anchorMap.get(p.format()); }
    public void setCenter(Point p, int length) {
        Orientation o = Directions[getIndex().ordinal()];
        Point delta = (o == Orientation.Horizontal
                ? Point.at(length / Anchors, 0)
                : Point.at(0, length / Anchors));

        for (int index = 0; index < Anchors; index++) {
            Point location = p.plus(delta.times(index - Center));
            getAnchor(index).setLocation(location);
            this.anchorMap.put(location.format(), getAnchor(index));
        }
    }

    // assigns the best anchor for a connector
    public Point assignBest(Point tip, Point end) { return nullOr((r) -> r.getLocation(), getBestAnchor(tip, end)); }
    public boolean accepts(Point tip, Point end) { return hasSome(getBestAnchor(tip, end)); }
    private Anchor getBestAnchor(Point tip, Point end) {
        Anchor result = getAnchor(Center);
        if (result.isEmpty()) return result;

        Point pole = end.minus(tip).signs();
        if (getIndex().ordinal() < Index.Top.ordinal()) {
            result = getAnchor(Center + pole.getY());
            if (result.isEmpty()) return result;
        } else {
            result = getAnchor(Center + pole.getX());
            if (result.isEmpty()) return result;
        }

        return null;
    }

} // Edge
