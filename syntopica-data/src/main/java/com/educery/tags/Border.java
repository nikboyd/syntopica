package com.educery.tags;

import com.educery.graphics.Point;
import com.educery.tags.Anchor;
import com.educery.tags.Edge.Index;

/**
 * A border surrounding a model element. Each border contains four edges.
 *
 * <h4>Border Responsibilities:</h4>
 * <ul>
 * <li>knows its four edges and their locations</li>
 * <li>assigns the best available anchor to each connection</li>
 * </ul>
 *
 * @see Edge
 * @see Anchor
 * @see ModelElement
 */
public class Border {

    private static final int Anchors = 3;
    static final int OptimalY = 2 * ModelElement.Height;

    private static final Index[] BestIndex = {
        Index.Top, Index.Top, Index.Top,
        Index.Left, Index.Top, Index.Right,
        Index.Bottom, Index.Bottom, Index.Bottom
    };

    private static final Index[] NextIndex = {
        Index.Left, Index.Top, Index.Right,
        Index.Left, Index.Top, Index.Right,
        Index.Left, Index.Bottom, Index.Right
    };

    private Edge[] edges = {
        new Edge(Index.Left.ordinal()),
        new Edge(Index.Right.ordinal()),
        new Edge(Index.Top.ordinal()),
        new Edge(Index.Bottom.ordinal())
    };

    /**
     * Returns the edge located at a point.
     *
     * @param p a connection point
     * @return an Edge
     */
    public Edge getEdge(Point p) {
        for (Edge edge : this.edges) {
            Anchor post = edge.getAnchor(p);
            if (post != null && post.getLocation().equals(p)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Returns the anchor located at a point.
     *
     * @param p a connection point
     * @return an Anchor
     */
    public Anchor getAnchor(Point p) {
        for (Edge edge : this.edges) {
            Anchor post = edge.getAnchor(p);
            if (post != null && post.getLocation().equals(p)) {
                return post;
            }
        }
        return null;
    }

    /**
     * Returns the indicated edge.
     *
     * @param index an edge index
     * @return an Edge
     */
    public Edge getEdge(Index index) {
        return getEdge(index.ordinal());
    }

    /**
     * Locates the edges of this border.
     *
     * @param origin the origin of this border rectangle
     * @param area the area of this border rectangle
     */
    public void locate(Point origin, Point area) {
        int x = origin.getX();
        int y = origin.getY();
        int width = area.getX();
        int height = area.getY();

        this.edges[Index.Top.ordinal()].setCenter(Point.at(x + (width / 2), y), width);
        this.edges[Index.Bottom.ordinal()].setCenter(Point.at(x + (width / 2), y + height), width);
        this.edges[Index.Left.ordinal()].setCenter(Point.at(x, y + (height / 2)), height);
        this.edges[Index.Right.ordinal()].setCenter(Point.at(x + width, y + (height / 2)), height);
    }

    /**
     * Adds the heads of the supplied connectors to this border.
     *
     * @param heads the connectors
     */
    public void addHeads(Connector... heads) {
        Anchor a = getAnchor(heads[0].getTip());
        if (a != null) {
            a.add(heads);
        }
    }

    /**
     * Adds the tails of the supplied connectors to this border.
     *
     * @param tails the connectors
     */
    public void addTails(Connector... tails) {
        Anchor a = getAnchor(tails[0].getEnd());
        if (a != null) {
            a.add(tails);
        }
    }

    /**
     * Assigns the best available anchor point to a connector head.
     *
     * @param tip a connector tip location
     * @param end a connector end location
     * @return an anchor Point
     */
    public Point assignHead(Point tip, Point end) {
        Point delta = end.minus(tip);
        int deltaY = delta.getY();
        if (deltaY < 0) {
            deltaY = 0 - deltaY;
        }

        if (deltaY < OptimalY) {
            Edge edge = getBestEdge(tip, end);
            if (edge.accepts(tip, end)) {
                return edge.assign(tip, end);
            }
        }

        Edge edge = getNextEdge(tip, end);
        if (edge.accepts(tip, end)) {
            return edge.assign(tip, end);
        }

        return null;
    }

    /**
     * Assigns the best available anchor point to a connector tail.
     *
     * @param tip a connector tip location
     * @param end a connector end location
     * @return an anchor Point
     */
    public Point assignTail(Point tip, Point end) {
        Point delta = end.minus(tip);
        int deltaY = delta.getY();
        if (deltaY < 0) {
            deltaY = 0 - deltaY;
        }

        if (deltaY < OptimalY) {
            Edge edge = getNextEdge(end, tip);
            if (edge.accepts(end, tip)) {
                return edge.assign(end, tip);
            }
        }

        Edge edge = getBestEdge(end, tip);
        if (edge.accepts(end, tip)) {
            return edge.assign(end, tip);
        }

        return null;
    }

    /**
     * Returns the best available edge for a connection.
     *
     * @param tip a connection tip
     * @param end a connection end
     * @return an Edge, or null
     */
    private Edge getBestEdge(Point tip, Point end) {
        Point delta = end.minus(tip);
        Point pole = delta.signs();
        Point offset = pole.plus(Point.unity());
        int index = offset.combine(Anchors);
        return getEdge(BestIndex[index]);
    }

    /**
     * Returns the next available edge for a connection.
     *
     * @param tip a connection tip
     * @param end a connection end
     * @return an Edge, or null
     */
    private Edge getNextEdge(Point tip, Point end) {
        Point delta = end.minus(tip);
        Point pole = delta.signs();
        Point offset = pole.plus(Point.unity());
        int index = offset.combine(Anchors);
        return getEdge(NextIndex[index]);
    }

    private Edge getEdge(int index) {
        return this.edges[index];
    }

} // Border

