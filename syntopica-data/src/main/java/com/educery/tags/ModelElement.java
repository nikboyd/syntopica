package com.educery.tags;

import com.educery.graphics.Point;
import com.educery.utils.Tag;

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

    public static final int Width = 140;
    public static final int Height = 44;
    private static final int[] Offsets = {Width / 2, Height / 2 + 5};

    private static final String Cyan = "#add8e6";
    private static final String Grey = "#bfbfbf";
    private static final String Bluish = "#d8e5e5";
    private static final String PageType = ".html";

    // styling for a SVG text rectangle
    private static Tag RectangleBase
            = Tag.named("rect-style")
                    .withStyle(Fill, None)
                    .withStyle(FillOpacity, 0)
                    .withStyle(StrokeWidth, 2)
                    .withStyle(Stroke, Black)
                    .withWidth(Width)
                    .withHeight(Height);

    /**
     * Constructs a new ModelElement.
     */
    private ModelElement() {
        super();
    }

    /**
     * Returns a new ModelElement.
     *
     * @param name a model element name
     * @return a new ModelElement
     */
    public static ModelElement named(String name) {
        ModelElement result = new ModelElement();
        result.name = name;
        return result;
    }

    /**
     * Colors this element grey.
     *
     * @return this ModelElement
     */
    public ModelElement withGrey() {
        return this.withColor(Grey);
    }

    /**
     * Colors this element cyan.
     *
     * @return this ModelElement
     */
    public ModelElement withCyan() {
        return this.withColor(Cyan);
    }

    /**
     * Colors this element blue.
     *
     * @return this ModelElement
     */
    public ModelElement withBlue() {
        return this.withColor(Bluish);
    }

    /**
     * Configures this model element with a color.
     *
     * @param color a color
     * @return this ModelElement
     */
    public ModelElement withColor(String color) {
        this.color = color;
        return this;
    }

    /**
     * Configures this model element with a location.
     *
     * @param x an x position
     * @param y an y position
     * @return this ModelElement
     */
    public ModelElement at(int x, int y) {
        setLocation(Point.at(x, y));
        return this;
    }

    /**
     * Locates this element at a point.
     *
     * @param p a point
     * @return this ModelElement
     */
    public ModelElement at(Point p) {
        setLocation(p);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return Width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return Height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffsetX() {
        return getX() + Offsets[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffsetY() {
        return getY() + Offsets[1];
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
        return drawAnchor().with(drawTextRectangle());
    }

    private Tag drawAnchor() {
        return Tag.xlinkWith(getPageName() + PageType);
    }

    private Tag drawTextRectangle() {
        return Tag.graphic()
                .with(drawFilledRectangle())
                .with(drawUnfilledRectangle())
                .with(drawTextElement());
    }

    private Tag drawUnfilledRectangle() {
        return Tag.rectangle().withValues(RectangleBase)
                .withX(getX()).withY(getY());
    }

    private String getPageName() {
        return getName().toLowerCase().replace(Blank, Period);
    }

} // ModelElement
