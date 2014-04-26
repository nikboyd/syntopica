package com.educery.xml.tags;

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
	
	private static int Width = 140;
	private static int Height = 44;
	private static int[] Offsets = { Width / 2, Height / 2 + 5 };
	
	// styling for a SVG text rectangle
	private static Tag RectangleBase =
		Tag.named("rect-style")
		.withStyle("fill", "none")
		.withStyle("fill-opacity", 0)
		.withStyle("stroke-width", 2)
		.withStyle("stroke", "#000000")
		.withWidth(Width)
		.withHeight(Height)
		;
	
	// styling for a SVG text box
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle("fill", "#000000")
		.withStyle("text-anchor", "middle")
		.withStyle("font-family", "sans-serif")
		.withStyle("font-style", "normal")
		.withStyle("font-weight", 700)
		.with("font-size", 17)
		;
	
	/**
	 * Returns a new ModelElement.
	 * @param name a model element name
	 * @return a new ModelElement
	 */
	public static ModelElement named(String name) {
		ModelElement result = new ModelElement();
		result.name = name;
		return result;
	}

	/**
	 * Configures this model element with a color.
	 * @param color a color
	 * @return this ModelElement
	 */
	public ModelElement withColor(String color) {
		this.color = color;
		return this;
	}

	/**
	 * Configures this model element with a location.
	 * @param x an x position
	 * @param y an y position
	 * @return this ModelElement
	 */
	public ModelElement at(int x, int y) {
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
	public String getKey() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public Tag buildElement() {
		return Tag.named("g")
				.with(buildFilledRectangle())
				.with(buildDrawnRectangle())
				.with(buildTextElement());
	}
	
	private Tag buildFilledRectangle() {
		return Tag.named("rect").withStyle("fill", getColor())
				.withWidth(Width).withHeight(Height)
				.withX(getX()).withY(getY());
	}
	
	private Tag buildDrawnRectangle() {
		return Tag.named("rect").withValues(RectangleBase)
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

} // ModelElement