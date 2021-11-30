package com.educery.tags;

import java.util.*;
import com.educery.graphics.Point;
import com.educery.tags.Edge.Index;
import com.educery.utils.Registry;
import static com.educery.utils.Utils.*;

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
    @Override public Point getLocation() { return this.location; }
    public void setLocation(Point origin) {
        this.location.setX(origin.getX());
        this.location.setY(origin.getY());
        this.border.locate(origin, Point.at(getWidth(), getHeight()));
    }

    protected Border border = new Border();
    public Border getBorder() { return this.border; }
    public Edge getEdge(Index index) { return getBorder().getEdge(index); }

    public void addHeads(Connector... heads)    { getBorder().addHeads(heads); }
    public void addHeads(List<Connector> heads) { getBorder().addHeads(heads); }
    public void addTails(Connector... tails)    { getBorder().addTails(tails); }
    public void addTails(List<Connector> tails) { getBorder().addTails(tails); }

    public Point assignHead(Point end) { return getBorder().assignHead(getPole(), end); }
    public Point assignTail(Point tip) { return getBorder().assignTail(tip, getPole()); }
    public Point getPole() { return Point.at(getCenter(), getMiddle()); }

    public int getX() { return getLocation().getX(); }
    public int getY() { return getLocation().getY(); }

    public int getOffsetX() { return getX(); }
    public int getOffsetY() { return getY(); }

    static final int LetterWidth = 10;
    static final int ModelWidth = 200;
    static final int ModelHeight = 44;
    public int nameWidth() { return getName().length() * LetterWidth; }

    public int normalHeight() {
        int result = ModelHeight;
        List<String> spanNames = spanNames();
        if (spanNames.size() == 1) return result;
        result += 8 * (spanNames.size() + 1);
//        report(result+"");
        return result; }

    public int normalWidth() {
        int maxWidth = ModelWidth;
        List<String> spanNames = spanNames();
        for (String name : spanNames) {
            maxWidth = Math.max(name.length() * LetterWidth, maxWidth);
        }
        return maxWidth; }

    public int getWidth() { return normalWidth(); }
    public int getHeight() { return normalHeight(); }
    @Override public Point getExtent() { return new Point(getWidth(), getHeight()); }

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

    @Override public Tag drawElement() { return Tag.graphic().with(drawFilledRectangle()).with(drawTextElement()); }
    public Tag drawConnectedBox() { return Tag.graphic().with(drawConnectedRectangle()).with(drawTextElement()); }
    protected Tag drawTextElement() { return copyTextStyle().with(drawTextSpans()).withOrigin(getOffsets()); }
    protected List<Tag> drawTextSpans() {
        int[] index = { 0 };
        List<String> spanNames = spanNames();
//        report(spanNames.toString());
        return map(spanNames, p -> {
            Tag tag = Tag.textSpan().withContent(p);
            tag.withX(getOffsetX());
            tag.withY(getOffsetY() + (20 * index[0]));
            index[0]++;
            return tag; });
    }

    protected List<String> spanNames() {
        String[] parts = getName().split(Blank);
        List<String> results = new ArrayList();
        for (int index = 0; index < parts.length; index++) {
            if (index % 2 == 0) {
                results.add(parts[index]);
            }
            else {
                String tail = results.get(results.size() - 1);
                results.set(results.size() - 1, tail + Blank + parts[index]);
            }
        }
        return results;
    }

    protected Tag drawConnectedRectangle() { return drawFillStyle().copyOrigin(this).copyExtent(this); }
    protected Tag drawUnfilledRectangle() { return copyBorderStyle().copyOrigin(this).copyExtent(this); }
    protected Tag drawFilledRectangle() { return drawFillStyle().copyOrigin(this).copyExtent(this); }
    protected Tag drawFillStyle() { return Tag.rectangle().withStyle(Fill, getColor()); }

    // styling for a SVG text box
    protected Tag copyTextStyle() { return Tag.textBox().withValues(getTextStyle()); }
    protected Tag getTextStyle() { return TextElementStyle; }
    static final Tag TextElementStyle =
        Tag.named(TextStyle)
            .withStyle(Fill, Black)
            .withStyle(TextAnchor, Middle)
            .withStyle(FontFamily, SansSerif)
            .withStyle(FontStyle, Normal)
            .withStyle(FontWeight, 700)
            .with(FontSize, 17);

    // styling for a SVG text border rectangle
    protected Tag copyBorderStyle() { return Tag.rectangle().withValues(getBorderStyle()); }
    protected Tag getBorderStyle() { return RectangleBase; }
    static final Tag RectangleBase =
        Tag.named("rect-style")
            .withStyle(Fill, None)
            .withStyle(FillOpacity, 0)
            .withStyle(StrokeWidth, 2)
            .withStyle(Stroke, Black)
            ;

} // TextElement
