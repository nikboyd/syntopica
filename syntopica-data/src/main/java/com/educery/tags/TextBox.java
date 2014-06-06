package com.educery.tags;

import com.educery.graphics.Point;
import com.educery.utils.Tag;

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

	private static int FontHeight = 18;
	private static int LetterWidth = 10;
//	private static int Width = 70;
	private static int Height = FontHeight + 8;

	// styling for a SVG text box
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle(Fill, Black)
		.withStyle(TextAnchor, Middle)
		.withStyle(FontFamily, SansSerif)
		.withStyle(FontStyle, Italic)
		.withStyle(FontWeight, Normal)
		.with(FontSize, FontHeight)
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
	 * @param p a point
	 * @return this TextBox
	 */
	public TextBox at(Point p) {
		setLocation(p);
		return this;
	}

	/**
	 * Configures this text box with a location.
	 * @param x an x position
	 * @param y an y position
	 * @return this TextBox
	 */
	public TextBox at(int x, int y) {
		setLocation(Point.at(x, y));
		return this;
	}
	
	/**
	 * An offset x position.
	 * @return a position
	 */
	@Override
	public int getOffsetX() {
		return getX() + (getWidth() / 2);
	}
	
	/**
	 * An offset y position.
	 * @return a position
	 */
	@Override
	public int getOffsetY() {
		return getY() + (getHeight() * 2 / 3);
	}
	
	@Override
	public int getWidth() {
		return this.name.length() * LetterWidth;
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