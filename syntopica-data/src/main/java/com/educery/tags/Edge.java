package com.educery.tags;

import java.util.*;

import com.educery.graphics.Point;
import com.educery.tags.Connector.Anchor;

/**
 * An edge of a model element (rectangle). Each edge contains three anchors.
 * 
 * <h4>Edge Responsibilities:</h4>
 * <ul>
 * <li>knows its three anchors and their locations</li>
 * <li>assigns the best available anchor to each connection</li>
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
	 * <li>knows its four edges and their locations</li>
	 * <li>assigns the best available anchor to each connection</li>
	 * </ul>
	 * 
	 * @see Edge
	 */
	public static class Border {
		
		private Edge[] edges = { 
			new Edge(Index.Left.ordinal()), 
			new Edge(Index.Right.ordinal()), 
			new Edge(Index.Top.ordinal()), 
			new Edge(Index.Bottom.ordinal()) 
		};
		
		/**
		 * Returns the edge located at a point.
		 * @param p a connection point
		 * @return an Edge
		 */
		public Edge getEdge(Point p) {
			for (Edge edge : this.edges) {
				Anchor post = edge.getAnchor(p);
				if (post != null && post.getLocation().equals(p)) {
					return edge;
				}
			}
			return null;
		}
		
		/**
		 * Returns the anchor located at a point.
		 * @param p a connection point
		 * @return an Anchor
		 */
		public Anchor getAnchor(Point p) {
			for (Edge edge : this.edges) {
				Anchor post = edge.getAnchor(p);
				if (post != null && post.getLocation().equals(p)) {
					return post;
				}
			}
			return null;
		}
		
		/**
		 * Returns the indicated edge.
		 * @param index an edge index
		 * @return an Edge
		 */
		public Edge getEdge(Index index) {
			return getEdge(index.ordinal());
		}
		
		/**
		 * Locates the edges of this border.
		 * @param origin the origin of this border rectangle
		 * @param area the area of this border rectangle
		 */
		public void locate(Point origin, Point area) {
			int x = origin.getX();
			int y = origin.getY();
			int width = area.getX();
			int height = area.getY();

			this.edges[Index.Top.ordinal()].setCenter(Point.at(x + (width / 2), y), width);
			this.edges[Index.Bottom.ordinal()].setCenter(Point.at(x + (width / 2), y + height), width);
			this.edges[Index.Left.ordinal()].setCenter(Point.at(x, y + (height / 2)), height);
			this.edges[Index.Right.ordinal()].setCenter(Point.at(x + width, y + (height / 2)), height);
		}
		
		/**
		 * Adds the heads of the supplied connectors to this border.
		 * @param heads the connectors
		 */
		public void addHeads(Connector ... heads) {
			Anchor a = getAnchor(heads[0].getTip());
			if (a != null) a.add(heads);
		}
		
		/**
		 * Adds the tails of the supplied connectors to this border.
		 * @param tails the connectors
		 */
		public void addTails(Connector ... tails) {
			Anchor a = getAnchor(tails[0].getEnd());
			if (a != null) a.add(tails);
		}
		
		/**
		 * Assigns the best available anchor point to a connector head.
		 * @param tip a connector tip location
		 * @param end a connector end location
		 * @return an anchor Point
		 */
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
		
		/**
		 * Assigns the best available anchor point to a connector tail.
		 * @param tip a connector tip location
		 * @param end a connector end location
		 * @return an anchor Point
		 */
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
		
		/**
		 * Returns the best available edge for a connection.
		 * @param tip a connection tip
		 * @param end a connection end
		 * @return an Edge, or null
		 */
		private Edge getBestEdge(Point tip, Point end) {
			Point delta = end.minus(tip);
			Point pole = delta.signs();
			Point offset = pole.plus(Point.unity());
			int index = offset.combine(Edge.Anchors);
			return getEdge(BestIndex[index]);
		}
		
		/**
		 * Returns the next available edge for a connection.
		 * @param tip a connection tip
		 * @param end a connection end
		 * @return an Edge, or null
		 */
		private Edge getNextEdge(Point tip, Point end) {
			Point delta = end.minus(tip);
			Point pole = delta.signs();
			Point offset = pole.plus(Point.unity());
			int index = offset.combine(Edge.Anchors);
			return getEdge(NextIndex[index]);
		}
		
		private Edge getEdge(int index) {
			return this.edges[index];
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
	
	public static enum Orientation {
		Horizontal,
		Vertical
	}
	
	public static final Orientation[] Directions = {
		Orientation.Vertical, 
		Orientation.Vertical, 
		Orientation.Horizontal, 
		Orientation.Horizontal, 
	};

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
	
	/**
	 * Sets the locations of the anchors for this edge.
	 * @param p the center anchor location
	 * @param length the length of this edge
	 */
	public void setCenter(Point p, int length) {
		Orientation o = Directions[getIndex().ordinal()];
		Point delta = (o == Orientation.Horizontal ? 
						Point.at(length / Anchors, 0) : 
						Point.at(0, length / Anchors));
		
		for (int index = 0; index < Anchors; index++) {
			Point location = p.plus(delta.times(index - Center));
			this.anchors[index].setLocation(location);
			this.anchorMap.put(location.format(), this.anchors[index]);
		}
	}
	
	/**
	 * Assigns the best available anchor to a connector tip.
	 * @param tip a connector tip
	 * @param end a connector end
	 * @return an Anchor, or null
	 */
	public Point assign(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result == null ? null : result.getLocation());
	}
	
	/**
	 * Indicates whether a best available anchor exists.
	 * @param tip a connector tip
	 * @param end a connector end
	 * @return whether a best available anchor exists
	 */
	public boolean accepts(Point tip, Point end) {
		Anchor result = getBestAnchor(tip, end);
		return (result != null);
	}

	/**
	 * Returns the allocated connectors.
	 * @return a Connector list
	 */
	public List<Connector> getConnectors() {
		ArrayList<Connector> results = new ArrayList<Connector>();
		for (Anchor a : this.anchors) {
			if (a.count() > 0) {
				results.addAll(Arrays.asList(a.getConnectors()));
			}
		}
		return results;
	}

	/**
	 * Counts the allocated connectors.
	 * @return a connector count
	 */
	public int count() {
		int result = 0;
		for (Anchor a : this.anchors) {
			result += a.count();
		}
		return result;
	}
	
	/**
	 * Indicates whether any connectors have been acquired by this edge.
	 * @return whether this edge has been connected yet
	 */
	public boolean isEmpty() {
		return count() == 0;
	}

	/**
	 * Returns the best available anchor given a connection.
	 * @param tip a connection tip
	 * @param end a connection end
	 * @return an Anchor, or null
	 */
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

} // Edge