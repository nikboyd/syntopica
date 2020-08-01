package com.educery.tags;

import java.io.*;
import java.util.*;
import static java.lang.System.*;
import static com.educery.utils.Utils.*;
import com.educery.utils.*;

/**
 * A drawing canvas. This represents a SVG view box.
 *
 * <h4>Canvas Responsibilities:</h4>
 * <ul>
 * <li>knows a location and view box</li>
 * <li>knows / contains some elements</li>
 * <li>draws a diagram using SVG</li>
 * </ul>
 */
public class Canvas implements Registry.KeySource, Tag.Factory, Closeable {

    private int width = 0;
    private int height = 0;
    private Canvas() { }
    private Canvas(int w, int h) { this(); this.width = w; this.height = h; }
    public static Canvas with(int width, int height) { return new Canvas(width, height); }

    private final int[] viewbox = { 0, 0, 0, 0 };
    private int[] viewbox() { return this.viewbox; }
    public Canvas with(int[] viewbox) { if (viewbox.length == 4) arraycopy(viewbox, 0, viewbox(), 0, 4); return this; }

    static final String ViewboxSpec = "%d %d %d %d";
    private String getViewbox() { return format(ViewboxSpec, viewbox()[0], viewbox()[1], viewbox()[2], viewbox()[3] ); }

    private final ArrayList<Tag.Factory> elements = emptyList();
    private List<Tag.Factory> elements() { return this.elements; }
    @Override public Tag drawElement() { return drawElements(buildContext()); }
    public Canvas with(Tag.Factory... elements) { elements().addAll(wrap(elements)); return this; }
    private Tag drawElements(Tag tag) { elements().forEach((element) -> tag.with(element.drawElement())); return tag; }

    static final String Measure = "cm";
    static final String Viewbox = "viewbox";
    static final String Namespace = "http://www.w3.org/2000/svg";
    static final String LinkNamespace = "http://www.w3.org/1999/xlink";
    private Tag buildContext() {
        return Tag.context()
            .withWidth(this.width + Measure)
            .withHeight(this.height + Measure)
            .with(Viewbox, getViewbox())
            .with("xmlns", Namespace)
            .with("xmlns:xlink", LinkNamespace)
            ; }


    static Canvas ActiveCanvas = null;
    public static boolean hasActiveCanvas() { return hasSome(ActiveCanvas); }
    public Canvas activate() { ActiveCanvas = this; return this; }
    @Override public void close() { ActiveCanvas = null; }

} // Canvas
