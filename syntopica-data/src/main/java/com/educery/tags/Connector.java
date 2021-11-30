package com.educery.tags;

import com.educery.graphics.*;
import com.educery.utils.Logging;

/**
 * A connector between a pair of model elements.
 *
 * <h4>Connector Responsibilities:</h4>
 * <ul>
 * <li>knows a path between two model elements</li>
 * <li>draws a connector between anchors on model elements using SVG</li>
 * </ul>
 *
 * @see Path
 * @see Anchor
 */
public class Connector implements Tag.Factory, Logging {

    private String label = Empty;
    public String label() { return this.label; }
    private Connector(String label) { this.label = label; }
    public static Connector named(String label) { return new Connector(label); }
    public Connector withLabel(String label) { this.label = label; return this; }

    private Path path;
    public Path getPath() { return this.path; }
    private Connector(Path path) { this(Empty); this.path = path; }
    public static Connector with(Path path) { return new Connector(path); }
    public static Connector with(Point... points) { return Connector.with(Path.from(points)); }
    public Connector copy() { return with(getPath().getPoints()).withLabel(label()); }

    public Point[] getHead() { return getPath().getHead(); }
    public Point[] getTail() { return getPath().getTail(); }
    private Point[] getTextSegment() { return (this.path.length() < 4 ? getHead() : getTail()); }

    public Point getTip() { return getPath().getTip(); }
    public Point getEnd() { return getPath().getEnd(); }

    private int headCount = 1;
    private int getHeadCount() { return this.headCount; }
    public Connector withHeads(int headCount) { this.headCount = headCount; return this; }

    private boolean filledHeads = true;
    public boolean filledHeads() { return this.filledHeads; }
    public Connector emptyHeads() { fillHeads(false); return this; }
    public Connector fillHeads() { fillHeads(true); return this; }
    public Connector fillHeads(boolean filledHeads) { this.filledHeads = filledHeads; return this; }

    /**
     * Connects to a pair of model elements.
     *
     * @param elements the connected elements
     * @return this Connector
     */
    public Connector between(ModelElement... elements) {
        Point tip = elements[0].assignHead(elements[1].getPole());
        Point end = elements[1].assignTail(elements[0].getPole());

        Point delta = end.minus(tip);
        Point norm = delta.signs().times(delta);
        if (norm.getY() == 0 || norm.getX() == 0) {
            this.path = Path.from(tip, end);
            return this;
        }

        Edge head = elements[0].getBorder().getEdge(tip);
        Edge tail = elements[1].getBorder().getEdge(end);
        boolean headHoriz = head.getIndex().ordinal() < Edge.Index.Top.ordinal();
        boolean tailHoriz = tail.getIndex().ordinal() < Edge.Index.Top.ordinal();
        boolean opposites = headHoriz ^ tailHoriz;

        if (opposites) {
            if (tailHoriz) {
                Point mid = Point.at(tip.getX(), end.getY());
                this.path = Path.from(tip, mid, end);
            } else {
                Point mid = Point.at(end.getX(), tip.getY());
                this.path = Path.from(tip, mid, end);
            }
        } else {
            int changeY = delta.getY() / 3;
            int midY = tip.getY() + changeY;
            Point p = Point.at(tip.getX(), midY);
            Point q = Point.at(end.getX(), midY);
            this.path = Path.from(tip, p, q, end);
        }

        return this;
    }

    private String formatPath() { return getPath().withHead(getTip().plus(headAdjustment())).format(); }
    private Tag drawSegmentedLine() { return Tag.polyline().withValues(LineStyle).with(Points, formatPath()); }
    @Override public Tag drawElement() {
        Tag result = Tag.graphic().with(drawSegmentedLine());
        for (int index = 0; index < getHeadCount(); index++) result.with(drawArrow(index));
        return (label().isEmpty() ? result : result.with(drawTextBox()));
    }

    private Tag drawTextBox() {
        Point[] segment = getTextSegment();
        TextBox box = TextBox.named(label());
        int bx = (segment[0].getX() + segment[1].getX() - box.getWidth()) / 2;
        int by = (segment[0].getY() + segment[1].getY() - box.getHeight()) / 2;
        Point boxOrigin = Point.at(bx, by);
        if (getHeadCount() > 1) boxOrigin = boxOrigin.plus(textOffset().reduced(3));
        return box.withColor(White).at(boxOrigin).drawConnectedBox();
    }

    private Path drawArrowPath(int index) { return getDirection().buildArrow(getHead(), index); }
    private Direction getDirection() { return getPath().getDirection(); }

    private Point headAdjustment() { return filledHeads() ? Point.zero() : headOffset(); }
    private Point headOffset() { return getDirection().getTipOffset(getHeadCount()); }
    private Point textOffset() { return Direction.of(getTextSegment()).getTipOffset(getHeadCount()); }


    static final String Points = "points";
    private Tag drawArrow(int index) {
        return Tag.polygon().with(Points, drawArrowPath(index).format()).withValues(getArrowStyle()); }

    private Tag getArrowStyle() { return filledHeads() ? FillStyle : LineStyle; }

    // styling for a SVG connector line
    static final Tag LineStyle =
        Tag.named("line-style")
            .withStyle(Fill, None)
            .withStyle(FillOpacity, 0)
            .withStyle(StrokeWidth, 2)
            .withStyle(Stroke, Black);

    static final Tag FillStyle =
        Tag.named("fill-style")
            .withStyle(Fill, Black);

} // Connector
