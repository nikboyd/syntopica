package com.educery.tags;

import com.educery.utils.Site;
import com.educery.graphics.Point;

/**
 * A model element. These represent a named rectangle in a model diagram.
 *
 * <h4>ModelElement Responsibilities:</h4>
 * <ul>
 * <li>knows a name, color, and location in a diagram</li>
 * <li>knows the appearance of such in a diagram</li>
 * <li>draws a model element in a diagram using SVG</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>supply a name, color, and location during construction</li>
 * </ul>
 */
public class ModelElement extends TextElement implements Tag.Factory {

    // force proper construction via factory
    protected ModelElement(String name) { super(name); }
    public static ModelElement named(String name) { return new ModelElement(name); }
    public ModelElement withColor(String color) { this.color = color; return this; }

    static final String Grey = "#bfbfbf";
    public ModelElement withGrey() { return this.withColor(Grey); }

    static final String Cyan = "#add8e6";
    public ModelElement withCyan() { return this.withColor(Cyan); }

    static final String Bluish = "#d8e5e5";
    public ModelElement withBlue() { return this.withColor(Bluish); }

    public ModelElement at(int x, int y) { setLocation(Point.at(x, y)); return this; }
    public ModelElement at(Point p) { setLocation(p); return this; }

    @Override public int getWidth() { return ModelWidth; }
    @Override public int getHeight() { return super.normalHeight(); }
    @Override public int nameWidth() { return Math.max(super.nameWidth(), ModelWidth); }

    static final int[] Offsets = { ModelWidth / 2, ModelHeight / 2 + 5 };
    @Override public int getOffsetX() { return getX() + Offsets[0]; }
    @Override public int getOffsetY() { return getY() + Offsets[1]; }
    @Override public Point getOffsets() { return new Point(getOffsetX(), getOffsetY()); }

    @Override public Tag drawElement() { return drawAnchor().with(drawTextRectangle()); }
    private Tag drawTextRectangle() { return Tag.graphic().with(componentTags()); }
    private Tag drawAnchor() { return Tag.xlinkWith(getPageName() + pageType()).withBase(pageBase()); }
    private String pageBase() { return Site.getSite().linkBase(); }
    private String pageType() { return Site.getSite().pageType(); }

    private String getPageName() { return getName().toLowerCase().replaceAll(Blank, Period); }
    private Tag[] componentTags() {
        Tag[] tags = { drawFilledRectangle(), drawUnfilledRectangle(), drawTextElement() }; return tags; }

} // ModelElement
