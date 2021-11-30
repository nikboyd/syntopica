package com.educery.tags;

import java.util.*;
import com.educery.graphics.Point;
import static com.educery.utils.Utils.*;

/**
 * A connector anchor. Each anchor "staples" a connector end point to the edge of a model element.
 *
 * <h4>Anchor Responsibilities:</h4>
 * <ul>
 * <li>knows a point of connection</li>
 * <li>knows the attached connectors</li>
 * </ul>
 *
 * @see Connector
 * @see Edge
 * @see ModelElement
 */
public class Anchor {

    private final Point location = new Point();
    public Point getLocation() { return this.location; }
    public boolean locates(Point p) { return getLocation().equals(p); }
    public void setLocation(Point p) {
        this.location.setX(p.getX());
        this.location.setY(p.getY());
    }

    private final ArrayList<Connector> connectors = new ArrayList();
    public List<Connector> connectors() { return this.connectors; }

    static final Connector[] NoConnectors = { };
    public Connector[] getConnectors() { return unwrap(connectors(), NoConnectors); }

    public void addAll(List<Connector> list) { connectors().addAll(list); }
    public void add(Connector... c) { addAll(wrap(c)); }

    public boolean isEmpty() { return connectors().isEmpty(); }
    public int count() { return connectors().size(); }

} // Anchor
