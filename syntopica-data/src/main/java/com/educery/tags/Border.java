package com.educery.tags;

import java.util.*;
import com.educery.graphics.Point;
import com.educery.tags.Edge.Index;
import static com.educery.utils.Utils.*;

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

    public Edge getEdge(Index index) { return getEdge(index.ordinal()); }
    private Edge getEdge(int index) { return this.edges[index]; }
    private final Edge[] edges = {
        new Edge(Index.Left.ordinal()),
        new Edge(Index.Right.ordinal()),
        new Edge(Index.Top.ordinal()),
        new Edge(Index.Bottom.ordinal())
    };

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

    public Edge getEdge(Point p) {
        for (Edge edge : this.edges) {
            Anchor post = edge.getAnchor(p);
            if (hasSome(post) && post.locates(p)) return edge;
        }
        return null;
    }

    public Anchor getAnchor(Point p) {
        for (Edge edge : this.edges) {
            Anchor post = edge.getAnchor(p);
            if (hasSome(post) && post.locates(p)) return post;
        }
        return null;
    }

    public void addHeads(Connector... heads) { addHeads(wrap(heads)); }
    public void addHeads(List<Connector> heads) {
        Anchor a = getAnchor(heads.get(0).getTip()); if (hasSome(a)) a.addAll(heads); }

    public void addTails(Connector... tails) { addTails(wrap(tails)); }
    public void addTails(List<Connector> tails) {
        Anchor a = getAnchor(tails.get(0).getEnd()); if (hasSome(a)) a.addAll(tails); }

    static final int OptimalY = 2 * ModelElement.ModelHeight;
    public Point assignHead(Point tip, Point end) {
        // assigns the best available anchor point to a connector head
        Point delta = end.minus(tip);
        int deltaY = delta.getY();
        if (deltaY < 0) deltaY = 0 - deltaY;
        if (deltaY < OptimalY) {
            Edge edge = getBestEdge(tip, end);
            if (edge.accepts(tip, end)) return edge.assignBest(tip, end);
        }

        Edge edge = getNextEdge(tip, end);
        return edge.accepts(tip, end) ? edge.assignBest(tip, end) : null; }

    public Point assignTail(Point tip, Point end) {
        // assigns the best available anchor point to a connector tail
        Point delta = end.minus(tip);
        int deltaY = delta.getY();
        if (deltaY < 0) deltaY = 0 - deltaY;
        if (deltaY < OptimalY) {
            Edge edge = getNextEdge(end, tip);
            if (edge.accepts(end, tip)) return edge.assignBest(end, tip);
        }

        Edge edge = getBestEdge(end, tip);
        return edge.accepts(end, tip) ? edge.assignBest(end, tip) : null; }

    static final int Anchors = 3;
    static final Index[] BestIndex = {
        Index.Top, Index.Top, Index.Top,
        Index.Left, Index.Top, Index.Right,
        Index.Bottom, Index.Bottom, Index.Bottom
    };
    private Edge getBestEdge(Point tip, Point end) {
        Point delta = end.minus(tip);
        Point pole = delta.signs();
        Point offset = pole.plus(Point.unity());
        int index = offset.combine(Anchors);
        return getEdge(BestIndex[index]); }

    static final Index[] NextIndex = {
        Index.Left, Index.Top, Index.Right,
        Index.Left, Index.Top, Index.Right,
        Index.Left, Index.Bottom, Index.Right
    };
    private Edge getNextEdge(Point tip, Point end) {
        Point delta = end.minus(tip);
        Point pole = delta.signs();
        Point offset = pole.plus(Point.unity());
        int index = offset.combine(Anchors);
        return getEdge(NextIndex[index]); }

} // Border
