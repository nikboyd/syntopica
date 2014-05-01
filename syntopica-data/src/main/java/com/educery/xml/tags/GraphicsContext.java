package com.educery.xml.tags;

import java.util.ArrayList;

import com.educery.utils.Registry;

/**
 * A graphics context. This represents a SVG viewbox.
 * 
 * <h4>GraphicsContext Responsibilities:</h4>
 * <ul>
 * <li>knows a location and view box</li>
 * <li>knows / contains some elements</li>
 * <li>draws the contents of a graphic diagram in SVG</li>
 * </ul>
 */
public class GraphicsContext implements Registry.KeySource, Tag.Factory {
	
	private static final String Measure = "cm";
	private static final String Viewbox = "viewbox";
	private static final String Namespace = "http://www.w3.org/2000/svg";
	private static final String LinkNamespace = "http://www.w3.org/1999/xlink";
	
	private int width = 0;
	private int height = 0;
	private int[] viewbox = { 0, 0, 0, 0 };
	private ArrayList<Tag.Factory> elements = new ArrayList<Tag.Factory>();
	
	/**
	 * Returns a new GraphicsContext.
	 * @param width a width
	 * @param height a height
	 * @return a new GraphicsContext
	 */
	public static GraphicsContext with(int width, int height) {
		GraphicsContext result = new GraphicsContext();
		result.width = width;
		result.height = height;
		return result;
	}
	
	private GraphicsContext() { }
	
	/**
	 * Configures this context with a view box.
	 * @param viewbox a view box
	 * @return this GraphicsContext
	 */
	public GraphicsContext with(int[] viewbox) {
		if (viewbox.length == 4) {
			for (int index = 0; index < 4; index++) {
				this.viewbox[index] = viewbox[index];
			}
		}
		return this;
	}
	
	/**
	 * Adds an element to this context.
	 * @param element an element
	 * @return this GraphicsContext
	 */
	public GraphicsContext with(Tag.Factory element) {
		this.elements.add(element);
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return Empty;
	}
	
	/** {@inheritDoc} */
	@Override
	public Tag buildElement() {
		Tag result = buildSVG();		
		for (Tag.Factory element : this.elements) {
			result.with(element.buildElement());
		}
		return result;
	}
	
	private Tag buildSVG() {
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

} // GraphicsContext