package com.educery.tags;

import com.educery.graphics.Point;
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

    // force proper construction via factory
    protected TextElement() { super(); }
    protected TextElement(String name) { this(); this.name = name; }

    protected String name = Empty;
    public String getName() { return this.name; }
    @Override public String getKey() { return this.name; }

    protected String color = "none";
    public String getColor() { return this.color; }

    protected Point location = Point.at(0, 0);
    public Point getLocation() { return this.location; }
    public void setLocation(Point origin) {
        this.location.setX(origin.getX());
        this.location.setY(origin.getY());
        this.border.locate(origin, Point.at(getWidth(), getHeight()));
    }

    protected Border border = new Border();
    public Border getBorder() { return this.border; }
    public Edge getEdge(Index index) { return getBorder().getEdge(index); }

    public void addHeads(Connector... heads) { getBorder().addHeads(heads); }
    public void addTails(Connector... tails) { getBorder().addTails(tails); }

    public Point assignHead(Point end) { return getBorder().assignHead(getPole(), end); }
    public Point assignTail(Point tip) { return getBorder().assignTail(tip, getPole()); }
    public Point getPole() { return Point.at(getCenter(), getMiddle()); }

    public int getX() { return getLocation().getX(); }
    public int getY() { return getLocation().getY(); }

    public int getOffsetX() { return getX(); }
    public int getOffsetY() { return getY(); }

    public int getWidth() { return 20; }
    public int getHeight() { return 20; }

    public int getLeft() { return getX(); }
    public int getRight() { return getX() + getWidth(); }
    public int getCenter() { return getX() + (getWidth() / 2); }

    public int getTop() { return getY(); }
    public int getBottom() { return getY() + getHeight(); }
    public int getMiddle() { return getY() + (getHeight() / 2); }

    static final int MarginOffset = 10;
    public int getMarginLeft() { return getLeft() - MarginOffset;}
    public int getMarginRight() { return getRight() + MarginOffset; }

    public int getMarginTop() { return getTop() - MarginOffset; }
    public int getMarginBottom() { return getBottom() + MarginOffset; }

    // styling for a SVG text box
    protected Tag getTextStyle() { return TextElementStyle; }
    static final Tag TextElementStyle =
        Tag.named(TextStyle)
            .withStyle(Fill, Black)
            .withStyle(TextAnchor, Middle)
            .withStyle(FontFamily, SansSerif)
            .withStyle(FontStyle, Normal)
            .withStyle(FontWeight, 700)
            .with(FontSize, 17);

    @Override public Tag drawElement() {
        return Tag.graphic().with(drawFilledRectangle()).with(drawTextElement()); }

    protected Tag drawFilledRectangle() {
        return Tag.rectangle().withStyle(Fill, getColor())
                .withWidth(getWidth()).withHeight(getHeight()).withX(getX()).withY(getY()); }

    protected Tag drawTextElement() {
        return Tag.textBox().withValues(getTextStyle()).with(drawTextSpan()).withX(getOffsetX()).withY(getOffsetY()); }

    protected Tag drawTextSpan() {
        return Tag.textSpan().withContent(getName()).withX(getOffsetX()).withY(getOffsetY()); }

} // TextElement
