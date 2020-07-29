package com.educery.tags;

import com.educery.graphics.Point;
import com.educery.tags.Border;
import com.educery.tags.Edge.Index;
import com.educery.utils.Registry;

/**
 * A boxed (rectangular) element that contains some text.
 *
 * <h4>TextElement Responsibilities:</h4>
 * <ul>
 * <li>knows some text, a color, a location, and a border</li>
 * </ul>
 *
 * @see Border
 * @see TextBox
 * @see ModelElement
 */
public class TextElement implements Registry.KeySource, Tag.Factory {

    private static final int MarginOffset = 10;

    // styling for a SVG text box
    private static Tag TextStyle
            = Tag.named("text-style")
                    .withStyle(Fill, Black)
                    .withStyle(TextAnchor, Middle)
                    .withStyle(FontFamily, SansSerif)
                    .withStyle(FontStyle, Normal)
                    .withStyle(FontWeight, 700)
                    .with(FontSize, 17);

    protected String name = Empty;
    protected String color = "none";
    protected Point location = Point.at(0, 0);
    protected Border border = new Border();

    /**
     * Constructs a new TextElement.
     */
    protected TextElement() {
        super();
    }

    /**
     * Sets the origin of this element.
     *
     * @param origin a Point
     */
    public void setLocation(Point origin) {
        this.location.setX(origin.getX());
        this.location.setY(origin.getY());
        this.border.locate(origin, Point.at(getWidth(), getHeight()));
    }

    /**
     * Returns the indicated edge.
     *
     * @param index an edge index
     * @return an Edge
     */
    public Edge getEdge(Index index) {
        return getBorder().getEdge(index);
    }

    /**
     * Returns the border of this element.
     *
     * @return a Border
     */
    public Border getBorder() {
        return this.border;
    }

    /**
     * The model element name.
     *
     * @return an element name
     */
    public String getName() {
        return this.name;
    }

    /**
     * The model element fill color.
     *
     * @return a fill color
     */
    public String getColor() {
        return this.color;
    }

    /**
     * The origin point of this element.
     *
     * @return a Point
     */
    public Point getLocation() {
        return this.location;
    }

    /**
     * Adds the heads of the supplied connectors to the border of this element.
     *
     * @param heads connectors
     */
    public void addHeads(Connector... heads) {
        getBorder().addHeads(heads);
    }

    /**
     * Adds the tails of the supplied connectors to the border of this element.
     *
     * @param tails connectors
     */
    public void addTails(Connector... tails) {
        getBorder().addTails(tails);
    }

    /**
     * Assigns a connector head to this element.
     *
     * @param end the connector end location
     * @return the assigned Point
     */
    public Point assignHead(Point end) {
        return getBorder().assignHead(getPole(), end);
    }

    /**
     * Assigns a connector tail to this element.
     *
     * @param tip the connector tip location
     * @return the assigned Point
     */
    public Point assignTail(Point tip) {
        return getBorder().assignTail(tip, getPole());
    }

    /**
     * Returns the center point of this element.
     *
     * @return a Point
     */
    public Point getPole() {
        return Point.at(getCenter(), getMiddle());
    }

    /**
     * An x position.
     *
     * @return a position
     */
    public int getX() {
        return getLocation().getX();
    }

    /**
     * An y position
     *
     * @return a position
     */
    public int getY() {
        return getLocation().getY();
    }

    /**
     * An offset x position.
     *
     * @return a position
     */
    public int getOffsetX() {
        return getX();
    }

    /**
     * An offset y position.
     *
     * @return a position
     */
    public int getOffsetY() {
        return getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag drawElement() {
        return Tag.graphic()
                .with(drawFilledRectangle())
                .with(drawTextElement());
    }

    /**
     * Builds a filled rectangle surrounding this text element.
     *
     * @return a Tag
     */
    protected Tag drawFilledRectangle() {
        return Tag.rectangle().withStyle(Fill, getColor())
                .withWidth(getWidth()).withHeight(getHeight())
                .withX(getX()).withY(getY());
    }

    /**
     * Builds a text box for this element.
     *
     * @return a Tag
     */
    protected Tag drawTextElement() {
        return Tag.textBox()
                .withValues(getTextStyle()).with(drawTextSpan())
                .withX(getOffsetX()).withY(getOffsetY());
    }

    /**
     * Builds a text span for this element.
     *
     * @return a Tag
     */
    protected Tag drawTextSpan() {
        return Tag.textSpan().withContent(getName())
                .withX(getOffsetX()).withY(getOffsetY());
    }

    /**
     * The width of this element.
     *
     * @return a width
     */
    public int getWidth() {
        return 20;
    }

    /**
     * The height of this element.
     *
     * @return a height
     */
    public int getHeight() {
        return 20;
    }

    /**
     * The left edge of this element.
     *
     * @return a position
     */
    public int getLeft() {
        return getX();
    }

    /**
     * Returns the margin left offset of this element.
     *
     * @return a margin left
     */
    public int getMarginLeft() {
        return getLeft() - MarginOffset;
    }

    /**
     * The center of this element.
     *
     * @return a position
     */
    public int getCenter() {
        return getX() + (getWidth() / 2);
    }

    /**
     * The right edge of this element.
     *
     * @return a position
     */
    public int getRight() {
        return getX() + getWidth();
    }

    /**
     * Returns the margin right offset of this element.
     *
     * @return a margin right
     */
    public int getMarginRight() {
        return getRight() + MarginOffset;
    }

    /**
     * The top of this element.
     *
     * @return a position
     */
    public int getTop() {
        return getY();
    }

    /**
     * Returns the margin top offset of this element.
     *
     * @return a margin top
     */
    public int getMarginTop() {
        return getTop() - MarginOffset;
    }

    /**
     * The middle of the element.
     *
     * @return a position
     */
    public int getMiddle() {
        return getY() + (getHeight() / 2);
    }

    /**
     * The bottom of this element.
     *
     * @return a position
     */
    public int getBottom() {
        return getY() + getHeight();
    }

    /**
     * Returns the margin offset of the bottom of this element.
     *
     * @return a margin offset
     */
    public int getMarginBottom() {
        return getBottom() + MarginOffset;
    }

    /**
     * The text style of this element.
     *
     * @return a Tag
     */
    protected Tag getTextStyle() {
        return TextStyle;
    }

} // TextElement
