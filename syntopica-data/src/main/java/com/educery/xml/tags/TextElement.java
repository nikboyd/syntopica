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
public class TextElement implements Registry.KeySource {

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
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return this.name;
	}

} // TextElement