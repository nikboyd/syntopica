package com.educery.xml.tags;

import com.educery.utils.Registry;

public class ModelElement implements Registry.KeySource {
	
	private static int Width = 140;
	private static int Height = 44;
	private static int[] Offsets = { Width / 2, Height / 2 + 5 };
	
	private static Tag RectangleBase =
		Tag.named("rect-style")
		.withStyle("fill", "none")
		.withStyle("fill-opacity", 0)
		.withStyle("stroke-width", 2)
		.withStyle("stroke", "#000000")
		.withWidth(Width)
		.withHeight(Height)
		;
	
	private static Tag TextStyle = 
		Tag.named("text-style")
		.withStyle("fill", "#000000")
		.withStyle("text-anchor", "middle")
		.withStyle("font-family", "sans-serif")
		.withStyle("font-style", "normal")
		.withStyle("font-weight", 700)
		.with("font-size", 17)
		;
	
	private String name = Empty;
	private String color = "none";
	private int[] location = { 0, 0 };
	
	public static ModelElement named(String name) {
		ModelElement result = new ModelElement();
		result.name = name;
		return result;
	}
	
	public ModelElement withColor(String color) {
		this.color = color;
		return this;
	}
	
	public ModelElement at(int x, int y) {
		this.location[0] = x;
		this.location[1] = y;
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return this.name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public int getX() {
		return this.location[0];
	}
	
	public int getOffsetX() {
		return getX() + Offsets[0];
	}
	
	public int getY() {
		return this.location[1];
	}
	
	public int getOffsetY() {
		return getY() + Offsets[1];
	}
	
	public Tag buildGraphicElement() {
		return Tag.named("g")
				.with(buildFilledRectangle())
				.with(buildDrawnRectangle())
				.with(buildTextElement());
	}
	
	private Tag buildFilledRectangle() {
		return Tag.named("rect").withStyle("fill", getColor())
				.withWidth(Width).withHeight(Height)
				.withX(getX()).withY(getY())
				;
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