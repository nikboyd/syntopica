package com.educery.xml.tags;

/**
 * A text box. These label and describe the connectors that appear in model diagrams.
 * 
 * <h4>TextBox Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 */
public class TextBox extends TextElement implements Tag.Factory {

	private static int Width = 70;
	private static int Height = 20;
	private static int[] Offsets = { Width / 2, Height };

	// styling for a SVG text box
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle("fill", "#000000")
		.withStyle("text-anchor", "middle")
		.withStyle("font-family", "sans-serif")
		.withStyle("font-style", "italic")
		.withStyle("font-weight", "normal")
		.with("font-size", 17)
		;
	
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
		this.location[0] = x;
		this.location[1] = y;
		return this;
	}
	
	/**
	 * An offset x position.
	 * @return a position
	 */
	public int getOffsetX() {
		return getX() + Offsets[0];
	}
	
	/**
	 * An offset y position.
	 * @return a position
	 */
	public int getOffsetY() {
		return getY() + Offsets[1];
	}

	/** {@inheritDoc} */
	@Override
	public Tag buildElement() {
		return Tag.named("g")
				.with(buildFilledRectangle())
				.with(buildTextElement());
	}
	
	private Tag buildFilledRectangle() {
		return Tag.named("rect").withStyle("fill", getColor())
				.withWidth(Width).withHeight(Height)
				.withX(getX()).withY(getY());
	}
	
	private Tag buildTextElement() {
		return Tag.named("text")
				.withValues(TextStyle).with(buildTextSpan())
				.withX(getOffsetX()).withY(getOffsetY());
	}
	
	private Tag buildTextSpan() {
		return Tag.named("tspan").withContent(getName())
				.withX(getOffsetX()).withY(getOffsetY());
	}

} // TextBox