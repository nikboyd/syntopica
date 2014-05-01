package com.educery.xml.tags;

import com.educery.utils.Registry;

/**
 * Describes an element that contains text in a box (rectangle).
 * 
 * <h4>TextElement Responsibilities:</h4>
 * <ul>
 * <li>knows some text, a color, and a location</li>
 * </ul>
 */
public class TextElement implements Registry.KeySource, Tag.Factory {
	
	// styling for a SVG text box
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle(Fill, Black)
		.withStyle(TextAnchor, Middle)
		.withStyle(FontFamily, SansSerif)
		.withStyle(FontStyle, Normal)
		.withStyle(FontWeight, 700)
		.with(FontSize, 17)
		;

	protected String name = Empty;
	protected String color = "none";
	protected int[] location = { 0, 0 };
	
	/**
	 * The model element name.
	 * @return an element name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * The model element fill color.
	 * @return a fill color
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * An x position.
	 * @return a position
	 */
	public int getX() {
		return this.location[0];
	}
	
	/**
	 * An y position
	 * @return a position
	 */
	public int getY() {
		return this.location[1];
	}
	
	/**
	 * An offset x position.
	 * @return a position
	 */
	public int getOffsetX() {
		return getX();
	}
	
	/**
	 * An offset y position.
	 * @return a position
	 */
	public int getOffsetY() {
		return getY();
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public Tag buildElement() {
		return Tag.graphic()
				.with(buildFilledRectangle())
				.with(buildTextElement());
	}
	
	/**
	 * Builds a filled rectangle surrounding this text element.
	 * @return a Tag
	 */
	protected Tag buildFilledRectangle() {
		return Tag.rectangle().withStyle(Fill, getColor())
				.withWidth(getWidth()).withHeight(getHeight())
				.withX(getX()).withY(getY());
	}
	
	/**
	 * Builds a text box for this element.
	 * @return a Tag
	 */
	protected Tag buildTextElement() {
		return Tag.textBox()
				.withValues(getTextStyle()).with(buildTextSpan())
				.withX(getOffsetX()).withY(getOffsetY());
	}
	
	/**
	 * Builds a text span for this element.
	 * @return a Tag
	 */
	protected Tag buildTextSpan() {
		return Tag.textSpan().withContent(getName())
				.withX(getOffsetX()).withY(getOffsetY());
	}
	
	protected int getWidth() {
		return 20;
	}

	protected int getHeight() {
		return 20;
	}
	
	protected Tag getTextStyle() {
		return TextStyle;
	}

} // TextElement