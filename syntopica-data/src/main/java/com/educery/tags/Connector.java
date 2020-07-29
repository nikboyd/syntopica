package com.educery.tags;

import com.educery.graphics.*;

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
public class Connector implements Tag.Factory {

//	private static final Log Logger = LogFactory.getLog(Connector.class);
    private static final String Points = "points";

    // styling for a SVG connector line
    private static Tag LineStyle
            = Tag.named("line-style")
                    .withStyle(Fill, None)
                    .withStyle(FillOpacity, 0)
                    .withStyle(StrokeWidth, 2)
                    .withStyle(Stroke, Black);

    private static Tag FillStyle
            = Tag.named("fill-style")
                    .withStyle(Fill, Black);

    private Path path;
    private String label = "";
    private int headCount = 1;
    private boolean filledHeads = true;

    /**
     * Returns a new Connector.
     *
     * @param label a label
     * @return a new Connector
     */
    public static Connector named(String label) {
        Connector result = new Connector();
        result.label = label;
        return result;
    }

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

    /**
     * Returns a new Connector.
     *
     * @param points the points that define a path for this connector
     * @return a new Connector
     */
    public static Connector with(Point... points) {
        return Connector.with(Path.from(points));
    }

    /**
     * Returns a new Connector.
     *
     * @param path a path
     * @return a new Connector
     */
    public static Connector with(Path path) {
        Connector result = new Connector();
        result.path = path;
        return result;
    }

    /**
     * Configures the label of this connector.
     *
     * @param label a label
     * @return this Connector
     */
    public Connector withLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * Configures the arrow head count of this connector.
     *
     * @param headCount a head count
     * @return this Connector
     */
    public Connector withHeads(int headCount) {
        this.headCount = headCount;
        return this;
    }

    /**
     * Makes this connector have filled arrow heads,
     *
     * @return this Connector
     */
    public Connector fillHeads() {
        fillHeads(true);
        return this;
    }

    /**
     * Makes this connector have empty arrow heads.
     *
     * @return this Connector
     */
    public Connector emptyHeads() {
        fillHeads(false);
        return this;
    }

    /**
     * Configures whether this connector has filled arrow heads.
     *
     * @param filledHeads indicates whether to fill the heads
     * @return this Connector
     */
    public Connector fillHeads(boolean filledHeads) {
        this.filledHeads = filledHeads;
        return this;
    }

    /**
     * Returns the head of this connector.
     *
     * @return a path segment
     */
    public Point[] getHead() {
        return getPath().getHead();
    }

    /**
     * Returns the tail of this connector.
     *
     * @return a path segment
     */
    public Point[] getTail() {
        return getPath().getTail();
    }

    /**
     * Returns the tip of this connector.
     *
     * @return a Point
     */
    public Point getTip() {
        return getPath().getTip();
    }

    /**
     * Returns the end of this connector.
     *
     * @return a Point
     */
    public Point getEnd() {
        return getPath().getEnd();
    }

    /**
     * The path of this connector.
     *
     * @return a Path
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag drawElement() {
        Tag result = Tag.graphic().with(drawSegmentedLine());
        for (int index = 0; index < getHeadCount(); index++) {
            result.with(drawArrow(index));
        }
        return (this.label.isEmpty() ? result
                : result.with(drawTextBox()));
    }

    private Tag drawTextBox() {
        Point[] segment = getTextSegment();
        TextBox box = TextBox.named(this.label);
        int bx = (segment[0].getX() + segment[1].getX() - box.getWidth()) / 2;
        int by = (segment[0].getY() + segment[1].getY() - box.getHeight()) / 2;
        Point boxOrigin = Point.at(bx, by);
        if (getHeadCount() > 1) {
            Point offset = Direction.of(segment).getTipOffset(getHeadCount());
            boxOrigin = boxOrigin.plus(offset.reduced(3));
        }
        box = box.withColor(White).at(boxOrigin);
        return box.drawElement();
    }

    private Point[] getTextSegment() {
        return (this.path.length() < 4 ? getHead() : getTail());
    }

    private Tag drawSegmentedLine() {
        return Tag.polyline().withValues(LineStyle).with(Points, formatPath());
    }

    private Tag drawArrow(int index) {
        return Tag.polygon().with(Points, drawArrowPath(index).format()).withValues(getArrowStyle());
    }

    private String formatPath() {
        Point offset = headAdjustment();
        return getPath().withHead(getTip().plus(offset)).format();
    }

    private Tag getArrowStyle() {
        return (this.filledHeads ? FillStyle : LineStyle);
    }

    private Path drawArrowPath(int index) {
        return getDirection().buildArrow(getHead(), index);
    }

    private Point headAdjustment() {
        if (filledHeads) {
            return Point.at(0, 0);
        }
        return getDirection().getTipOffset(getHeadCount());
    }

    private int getHeadCount() {
        return this.headCount;
    }

    private Direction getDirection() {
        return getPath().getDirection();
    }

} // Connector
