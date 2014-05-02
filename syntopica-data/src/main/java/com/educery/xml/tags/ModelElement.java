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
		.withStyle(Fill, None)
		.withStyle(FillOpacity, 0)
		.withStyle(StrokeWidth, 2)
		.withStyle(Stroke, Black)
		.withWidth(Width)
		.withHeight(Height)
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
				.with(buildDrawnRectangle())
				.with(buildTextElement());
	}
	
	private Tag buildDrawnRectangle() {
		return Tag.rectangle().withValues(RectangleBase)
				.withX(getX()).withY(getY());
	}
	
	@Override
	public int getWidth() {
		return Width;
	}

	@Override
	public int getHeight() {
		return Height;
	}

} // ModelElement