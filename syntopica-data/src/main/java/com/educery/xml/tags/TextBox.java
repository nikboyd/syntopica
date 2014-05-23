package com.educery.xml.tags;

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

	private static int Width = 70;
	private static int Height = 24;
	private static int[] Offsets = { Width / 2, (Height * 2) / 3 };

	// styling for a SVG text box
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle(Fill, Black)
		.withStyle(TextAnchor, Middle)
		.withStyle(FontFamily, SansSerif)
		.withStyle(FontStyle, Italic)
		.withStyle(FontWeight, Normal)
		.with(FontSize, 17)
		;
	
	/**
	 * Constructs a new TextBox.
	 */
	private TextBox() {
		super();
	}
	
	/**
	 * Returns a new TextBox.
	 * @param name a text box name
	 * @return a new TextBox
	 */
	public static TextBox named(String name) {
		TextBox result = new TextBox();
		result.name = name;
		return result;
	}

	/**
	 * Configures this text box with a color.
	 * @param color a color
	 * @return this TextBox
	 */
	public TextBox withColor(String color) {
		this.color = color;
		return this;
	}

	/**
	 * Configures this text box with a location.
	 * @param x an x position
	 * @param y an y position
	 * @return this TextBox
	 */
	public TextBox at(int x, int y) {
		this.location = Point.at(x, y);
		return this;
	}
	
	/**
	 * An offset x position.
	 * @return a position
	 */
	@Override
	public int getOffsetX() {
		return getX() + Offsets[0];
	}
	
	/**
	 * An offset y position.
	 * @return a position
	 */
	@Override
	public int getOffsetY() {
		return getY() + Offsets[1];
	}
	
	@Override
	public int getWidth() {
		return Width;
	}

	@Override
	public int getHeight() {
		return Height;
	}
	
	@Override
	protected Tag getTextStyle() {
		return TextStyle;
	}

} // TextBox