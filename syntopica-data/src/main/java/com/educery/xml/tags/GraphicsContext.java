package com.educery.xml.tags;

import com.educery.utils.Registry;

public class GraphicsContext implements Registry.KeySource {
	
	private static String Measure = "cm";
	private static String Namespace = "http://www.w3.org/2000/svg";
	private static String LinkNamespace = "http://www.w3.org/1999/xlink";
	
	private int width = 0;
	private int height = 0;
	private int[] viewbox = { 0, 0, 0, 0 };
	
	public static GraphicsContext with(int width, int height) {
		GraphicsContext result = new GraphicsContext();
		result.width = width;
		result.height = height;
		return result;
	}
	
	public GraphicsContext with(int[] viewbox) {
		if (viewbox.length == 4) {
			for (int index = 0; index < 4; index++) {
				this.viewbox[index] = viewbox[index];
			}
		}
		return this;
	}
	
	@Override
	public String getKey() {
		return Empty;
	}
	
	public Tag buildContext() {
		return Tag.named("svg")
				.with("width", this.width + Measure)
				.with("height", this.height + Measure)
				.with("viewbox", getViewbox())
				.with("xmlns", Namespace)
				.with("xmlns:xlink", LinkNamespace)
				;
	}
	
	private String getViewbox() {
		return  this.viewbox[0] + Blank + 
				this.viewbox[1] + Blank + 
				this.viewbox[2] + Blank + 
				this.viewbox[3];
	}

} // GraphicsContext