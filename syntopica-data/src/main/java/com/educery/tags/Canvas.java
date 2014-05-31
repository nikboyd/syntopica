package com.educery.tags;

import java.util.ArrayList;

import com.educery.utils.Registry;
import com.educery.utils.Tag;

/**
 * A drawing canvas. This represents a SVG viewbox.
 * 
 * <h4>Canvas Responsibilities:</h4>
 * <ul>
 * <li>knows a location and view box</li>
 * <li>knows / contains some elements</li>
 * <li>draws a diagram using SVG</li>
 * </ul>
 */
public class Canvas implements Registry.KeySource, Tag.Factory {
	
	private static final String Measure = "cm";
	private static final String Viewbox = "viewbox";
	private static final String Namespace = "http://www.w3.org/2000/svg";
	private static final String LinkNamespace = "http://www.w3.org/1999/xlink";
	
	private int width = 0;
	private int height = 0;
	private int[] viewbox = { 0, 0, 0, 0 };
	private ArrayList<Tag.Factory> elements = new ArrayList<Tag.Factory>();
	
	/**
	 * Returns a new Canvas.
	 * @param width a width
	 * @param height a height
	 * @return a new Canvas
	 */
	public static Canvas with(int width, int height) {
		Canvas result = new Canvas();
		result.width = width;
		result.height = height;
		return result;
	}
	
	private Canvas() { }
	
	/**
	 * Configures this canvas with a view box.
	 * @param viewbox a view box
	 * @return this Canvas
	 */
	public Canvas with(int[] viewbox) {
		if (viewbox.length == 4) {
			for (int index = 0; index < 4; index++) {
				this.viewbox[index] = viewbox[index];
			}
		}
		return this;
	}
	
	/**
	 * Adds some element(s) to this canvas.
	 * @param elements some element(s)
	 * @return this Canvas
	 */
	public Canvas with(Tag.Factory ... elements) {
		for (Tag.Factory element : elements) {
			this.elements.add(element);
		}
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return Empty;
	}
	
	/** {@inheritDoc} */
	@Override
	public Tag drawElement() {
		Tag result = buildContext();		
		for (Tag.Factory element : this.elements) {
			result.with(element.drawElement());
		}
		return result;
	}
	
	private Tag buildContext() {
		return Tag.context()
				.withWidth(this.width + Measure)
				.withHeight(this.height + Measure)
				.with(Viewbox, getViewbox())
				.with("xmlns", Namespace)
				.with("xmlns:xlink", LinkNamespace);
	}
	
	private String getViewbox() {
		return  this.viewbox[0] + Blank + 
				this.viewbox[1] + Blank + 
				this.viewbox[2] + Blank + 
				this.viewbox[3];
	}

} // Canvas