package com.educery.xml.tags;

import java.util.*;

import com.educery.xml.tags.Connector.Anchor;

/**
 * An edge of a model element (rectangle). Each edge contains three anchors.
 * 
 * <h4>Edge Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li></li>
 * </ul>
 * 
 * @see Anchor
 */
public class Edge {
	
	/**
	 * A border surrounding a model element. Each border contains four edges.
	 * 
	 * <h4>Border Responsibilities:</h4>
	 * <ul>
	 * <li></li>
	 * </ul>
	 *
	 * <h4>Client Responsibilities:</h4>
	 * <ul>
	 * <li></li>
	 * </ul>
	 */
	public static class Border {
		
		private Edge[] edges = { 
			new Edge(Index.Left.ordinal()), 
			new Edge(Index.Right.ordinal()), 
			new Edge(Index.Top.ordinal()), 
			new Edge(Index.Bottom.ordinal()) 
		};
		
		public Edge getEdge(Point p) {
			for (Edge edge : this.edges) {
				Anchor post = edge.getAnchor(p);
				if (post != null && post.getLocation().equals(p)) {
					return edge;
				}
			}
			return null;
		}
		
		public Anchor getAnchor(Point p) {
			for (Edge edge : this.edges) {
				Anchor post = edge.getAnchor(p);
				if (post != null && post.getLocation().equals(p)) {
					return post;
				}
			}
			return null;
		}
		
		public Edge getEdge(Index index) {
			return getEdge(index.ordinal());
		}
		
		private Edge getEdge(int index) {
			return this.edges[index];
		}
		
		public void locate(Point origin, Point area) {
			int x = origin.getX();
			int y = origin.getY();
			int width = area.getX();
			int height = area.getY();

			this.edges[Index.Top.ordinal()].setCenter(Point.at(x + (width / 2), y), width);
			this.edges[Index.Bottom.ordinal()].setCenter(Point.at(x + (width / 2), y + height), width);
			this.edges[Index.Left.ordinal()].setMiddle(Point.at(x, y + (height / 2)), height);
			this.edges[Index.Right.ordinal()].setMiddle(Point.at(x + width, y + (height / 2)), height);
		}
		
		public void addHeads(Connector ... heads) {
			Anchor a = getAnchor(heads[0].getTip());
			if (a != null) a.add(heads);
		}
		
		public void addTails(Connector ... tails) {
			Anchor a = getAnchor(tails[0].getEnd());
			if (a != null) a.add(tails);
		}
		
		public Point assignHead(Point tip, Point end) {
			Edge edge = getBestEdge(tip, end);
			if (edge.accepts(tip, end)) {
				return edge.assign(tip, end);
			}
			
			edge = getNextEdge(tip, end);
			if (edge.accepts(tip, end)) {
				return edge.assign(tip, end);
			}

			return null;
		}
		
		public Point assignTail(Point tip, Point end) {
			Point delta = end.minus(tip);
			int deltaY = delta.getY();
			if (deltaY < 0) deltaY = 0 - deltaY;

			if (deltaY < OptimalY) {
				Edge edge = getNextEdge(end, tip);
				if (edge.accepts(end, tip)) {
					return edge.assign(end, tip);
				}
			}
			
			Edge edge = getBestEdge(end, tip);
			if (edge.accepts(end, tip)) {
				return edge.assign(end, tip);
			}
			
			return null;
		}
		
		private Edge getBestEdge(Point tip, Point end) {
			Point delta = end.minus(tip);
			Point pole = delta.signs();
			Point offset = pole.plus(Point.unity());
			int index = offset.combine(Edge.Anchors);
			return getEdge(BestIndex[index]);
		}
		
		private Edge getNextEdge(Point tip, Point end) {
			Point delta = end.minus(tip);
			Point pole = delta.signs();
			Point offset = pole.plus(Point.unity());
			int index = offset.combine(Edge.Anchors);
			return getEdge(NextIndex[index]);
		}
		
	} // Border


	private static final int Center = 1;
	private static final int Anchors = 3;
	static final int OptimalY = 4 * ModelElement.Height;
	
	private static final Index[] BestIndex = {
		Index.Top, Index.Top, Index.Top,
		Index.Left, Index.Top, Index.Right,
		Index.Bottom, Index.Bottom, Index.Bottom
	};
	
	private static final Index[] NextIndex = {
		Index.Left, Index.Top, Index.Right,
		Index.Left, Index.Top, Index.Right,
		Index.Left, Index.Bottom, Index.Right
	};
	
	public static enum Index {
		Left, Right, Top, Bottom
	}
	
	public static enum Vertical {
		Top, Middle, Bottom
	}
	
	public static enum Horizontal {
		Left, Center, Right
	}

	private Index index;
	private Anchor[] anchors = { new Anchor(),  new Anchor(),  new Anchor() };
	private HashMap<String, Anchor> anchorMap = new HashMap<String, Anchor>();
	
	/**
	 * Constructs a new Edge.
	 * @param index indicates which edge is this
	 */
	public Edge(int index) {
		this.index = Index.values()[index];
	}
	
	/**
	 * The edge index.
	 * @return an index
	 */
	public Index getIndex() {
		return this.index;
	}
	
	/**
	 * Returns the anchor at a point.
	 * @param p a point
	 * @return an Anchor
	 */
	public Anchor getAnchor(Point p) {
		return this.anchorMap.get(p.format());
	}
	
	public void setCenter(Point p, int width) {
		for (int index = -1; index < 2; index++) {
			Point delta = Point.at(width / 3, 0);
			Point location = p.plus(delta.times(index));
			this.anchors[index + Center].setLocation(location);
			this.anchorMap.put(location.format(), this.anchors[index + Center]);
		}
	}
	
	public void setMiddle(Point p, int height) {
		for (int index = -1; index < 2; index++) {
			Point delta = Point.at(0, height / 3);
			Point location = p.plus(delta.times(index));
			this.anchors[index + Center].setLocation(location);
			this.anchorMap.put(location.format(), this.anchors[index + Center]);
		}
	}
	
	public Point assign(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result == null ? null : result.getLocation());
	}
	
	public boolean accepts(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result != null);
	}
	
	private Anchor getBestAnchor(Point tip, Point end) {
		Anchor result = getAnchor(Center);
		if (result.isEmpty()) return result;

		Point pole = end.minus(tip).signs();
		if (getIndex().ordinal() < Index.Top.ordinal()) {
			result = getAnchor(Center + pole.getY());
			if (result.isEmpty()) return result;
		}
		else {
			result = getAnchor(Center + pole.getX());
			if (result.isEmpty()) return result;
		}

		return null;
	}
	
	private Anchor getAnchor(int index) {
		return this.anchors[index];
	}
	
	public List<Connector> getConnectors() {
		ArrayList<Connector> results = new ArrayList<Connector>();
		for (Anchor a : this.anchors) {
			if (a.count() > 0) {
				results.addAll(Arrays.asList(a.getConnectors()));
			}
		}
		return results;
	}
	
	public int count() {
		int result = 0;
		for (Anchor a : this.anchors) {
			result += a.count();
		}
		return result;
	}
	
	public boolean isEmpty() {
		return count() == 0;
	}

} // Edge