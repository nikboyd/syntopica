package com.educery.xml.tags;


/**
 * A connector between a pair of model elements.
 * 
 * <h4>Connector Responsibilities:</h4>
 * <ul>
 * <li>knows a path between two model elements</li>
 * <li>draws a connector between model elements using SVG</li>
 * </ul>
 */
public class Connector implements Tag.Factory {
	
	private static final String Points = "points";
	
	// styling for a SVG connector line
	private static Tag LineStyle = 
		Tag.named("line-style")
		.withStyle(Fill, None)
		.withStyle(FillOpacity, 0)
		.withStyle(StrokeWidth, 2)
		.withStyle(Stroke, Black)
		;
	
	private Path path;
	private int headCount = 1;
	private boolean filledHeads = false;
	private Orientation orientation;
	
	/**
	 * Returns a new Connector.
	 * @param points the points that define a path for this connector
	 * @return a new Connector
	 */
	public static Connector with(Point ... points) {
		Connector result = new Connector();
		result.path = Path.from(points);
		result.orientation = result.path.getOrientation();
		return result;
	}
	
	/**
	 * Configures the arrow head count of this connector.
	 * @param headCount a head count
	 * @return this Connector
	 */
	public Connector withHeads(int headCount) {
		this.headCount = headCount;
		return this;
	}
	
	/**
	 * Makes this connector have filled arrow heads,
	 * @return this Connector
	 */
	public Connector fillHeads() {
		fillHeads(true);
		return this;
	}

	/**
	 * Configures whether this connector has filled arrow heads.
	 * @param filledHeads indicates whether to fill the heads
	 * @return this Connector
	 */
	public Connector fillHeads(boolean filledHeads) {
		this.filledHeads = filledHeads;
		return this;
	}
	
	/**
	 * The orientation of the head of this connector.
	 * @return an Orientation
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Formats the path of this connector.
	 * @return a formatted path
	 */
	public String formatPath() {
		return adjustPath().format();
	}
	
	/**
	 * Adjusts the path to account for empty heads if needed.
	 * @return an adjusted path
	 */
	public Path adjustPath() {
		Point[] heads = getHead();
		return this.path.withHead(heads[0].plus(adjustHead(heads)));
	}
	
	/**
	 * Returns the head segment of this connector.
	 * @return a pair of points
	 */
	public Point[] getHead() {
		return this.path.getHead();
	}
	
	/**
	 * Builds an element from this connector.
	 * @return a Tag
	 */
	public Tag buildElement() {
		Tag result = Tag.graphic().with(buildConnector());
		for (int index = 0; index < this.headCount; index++) {
			result.with(buildArrow(index));
		}
		return result;
	}
	
	private Tag buildConnector() {
		return Tag.polyline().withValues(LineStyle).with(Points, formatPath());
	}
	
	private Tag buildArrow(int index) {
		Tag result = Tag.polygon().with(Points, buildHead(index).format());

		if (this.filledHeads) {
			result.withStyle(Fill, Black);
		}
		else {
			result.withValues(LineStyle);
		}

		return result;
	}
	
	private Path buildHead(int index) {
		return getOrientation().buildArrow(this.path.getHead(), index);
	}
	
	private Point adjustHead(Point[] points) {
		if (filledHeads) return Point.at(0, 0);
		return getOrientation().getTipOffset(this.headCount);
	}

} // Connector