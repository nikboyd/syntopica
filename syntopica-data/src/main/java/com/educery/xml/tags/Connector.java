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
	private String label = "";
	private int headCount = 1;
	private boolean filledHeads = false;
	
	/**
	 * Returns a new Connector.
	 * @param elements the connected elements
	 * @return a new Connector
	 */
	public static Connector between(ModelElement ... elements) {
		Point tip = Point.at(elements[0].getCenter(), elements[0].getTop());
		Point end = Point.at(elements[1].getCenter(), elements[1].getBottom());
		if (tip.getX() == end.getX()) return Connector.with(tip, end);

		int testX = elements[0].getCenter() + 15;
		if (testX > elements[1].getRight()) {
			end = Point.at(elements[1].getRight(), elements[1].getMiddle());
			Point p = Point.at(tip.getX(), end.getY());
			return Connector.with(tip, p, end);
		}
		
		int halfY = ((tip.getY() - end.getY()) / 3) + end.getY();
		Point p = Point.at(tip.getX(), halfY);
		Point q = Point.at(end.getX(), halfY);
		return Connector.with(tip, p, q, end);
	}
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
	 * Configures the label of this connector.
	 * @param label a label
	 * @return this Connector
	 */
	public Connector withLabel(String label) {
		this.label = label;
		return this;
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
		if (this.label.isEmpty()) return result;
		return result.with(buildTextBox());
	}
	
	private Tag buildTextBox() {
		Point[] head = getHead();
		TextBox b = TextBox.named(this.label);
		int bx = (head[0].getX() + head[1].getX() - b.getWidth()) / 2;
		int by = (head[0].getY() + head[1].getY() - b.getHeight()) / 2;
		b = b.withColor(White).at(bx, by);
		return b.buildElement();
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