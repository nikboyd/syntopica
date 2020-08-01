package com.educery.tags;

import com.educery.graphics.Point;

/**
 * A text box. A text box labels and describes a connector (arrow) that appears in a model diagram.
 *
 * <h4>TextBox Responsibilities:</h4>
 * <ul>
 * <li>knows the style used for drawing connector text boxes</li>
 * <li>knows the text, color, and location of such a text box</li>
 * <li>draws a connector text box in a diagram using SVG</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>supply a name, color, and location during construction</li>
 * </ul>
 */
public class TextBox extends TextElement implements Tag.Factory {

    // force proper construction via factory
    protected TextBox(String name) { super(name); }
    public static TextBox named(String name) { return new TextBox(name); }
    public TextBox withColor(String color) { this.color = color; return this; }

    public TextBox at(Point p) { setLocation(p); return this; }
    public TextBox at(int x, int y) { setLocation(Point.at(x, y)); return this; }

    @Override public int getOffsetX() { return getX() + (getWidth() / 2); }
    @Override public int getOffsetY() { return getY() + (getHeight() * 2 / 3); }

    @Override public int getWidth() { return getName().length() * LetterWidth; }
    @Override public int getHeight() { return Height; }

    // styling for a SVG text box
    static final int LetterWidth = 10;
    static final int BoxFontHeight = 18;
    static final int Height = BoxFontHeight + 8;
    @Override protected Tag getTextStyle() { return TextBoxStyle; }
    static final Tag TextBoxStyle =
        Tag.named(TextStyle)
            .withStyle(Fill, Black)
            .withStyle(TextAnchor, Middle)
            .withStyle(FontFamily, SansSerif)
            .withStyle(FontStyle, Italic)
            .withStyle(FontWeight, Normal)
            .with(FontSize, BoxFontHeight);

} // TextBox
