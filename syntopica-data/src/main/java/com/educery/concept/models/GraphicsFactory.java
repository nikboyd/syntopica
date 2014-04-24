package com.educery.concept.models;

public class GraphicsFactory {
	
	public Tag makeGraphicsContext() {
		return Tag.named("svg")
				.with("width", "12cm")
				.with("height", "13cm")
				.with("viewbox", "58 18 443 506")
				.with("xmlns", "http://www.w3.org/2000/svg")
				.with("xmlns:xlink", "http://www.w3.org/1999/xlink")
				;
	}
	
	public Tag makeModelElement(String name, String color, int x, int y) {
		return makeGraphicsElement()
				.with(makeFilledRectangle(color, x, y))
				.with(makeRectangle(x, y))
				.with(makeTextElement(name, x + 70, y + 25));
	}
	
	public Tag makeGraphicsElement() {
		return Tag.named("g");
	}
	
	public Tag makeFilledRectangle(String color, int x, int y) {
		return Tag.named("rect")
				.withStyle("fill", color)
				.withX(x).withY(y)
				.withWidth(140)
				.withHeight(44)
				;
	}
	
	public Tag makeRectangle(int x, int y) {
		return Tag.named("rect")
				.withStyle("fill", "none")
				.withStyle("fill-opacity", 0)
				.withStyle("stroke-width", 2)
				.withStyle("stroke", "#000000")
				.withX(x).withY(y)
				.withWidth(140)
				.withHeight(44)
				;
	}
	
	public Tag makeTextElement(String text, int x, int y) {
		return Tag.named("text")
				.with("font-size", 17)
				.withStyle("fill", "#000000")
				.withStyle("text-anchor", "middle")
				.withStyle("font-family", "sans-serif")
				.withStyle("font-style", "normal")
				.withStyle("font-weight", 700)
				.withX(x).withY(y)
				.with(makeTextSpan(text, x, y))
				;
	}
	
	private Tag makeTextSpan(String text, int x, int y) {
		return Tag.named("tspan").withContent(text).withX(x).withY(y);
	}

} // GraphicsFactory