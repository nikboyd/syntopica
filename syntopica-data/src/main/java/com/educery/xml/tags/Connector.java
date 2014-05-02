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
	
	private static Tag FillStyle =
		Tag.named("fill-style")
		.withStyle(Fill, Black)
		;
	
	private Path path;
	private int headCount = 1;
	private boolean filledHeads = false;
	
	/**
	 * Returns a new Connector.
	 * @param points the points that define a path for this connector
	 * @return a new Connector
	 */
	public static Connector with(Point ... points) {
		Connector result = new Connector();
		result.path = Path.from(points);
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
	
	/** {@inheritDoc} */
	@Override
	public Tag buildElement() {
		Tag result = Tag.graphic().with(buildSegmentedLine());
		for (int index = 0; index < this.headCount; index++) {
			result.with(buildArrow(index));
		}
		return result;
	}
	
	private Tag buildSegmentedLine() {
		return Tag.polyline().withValues(LineStyle).with(Points, formatPath());
	}
	
	private Tag buildArrow(int index) {
		return Tag.polygon().with(Points, buildArrowTriangle(index).format()).withValues(getArrowStyle());
	}

	private String formatPath() {
		return getPath().withHead(getTip().plus(headAdjustment())).format();
	}
	
	private Tag getArrowStyle() {
		return (this.filledHeads ? FillStyle : LineStyle);
	}
	
	private Path buildArrowTriangle(int index) {
		return getOrientation().buildArrow(getHead(), index);
	}
	
	private Point headAdjustment() {
		if (filledHeads) return Point.at(0, 0);
		return getOrientation().getTipOffset(this.headCount);
	}

	private Orientation getOrientation() {
		return getPath().getOrientation();
	}

	private Point[] getHead() {
		return getPath().getHead();
	}
	
	private Point getTip() {
		return getPath().getTip();
	}
	
	private Path getPath() {
		return this.path;
	}

} // Connector