package com.educery.tags;

import java.util.*;
import com.educery.utils.*;
import com.educery.graphics.Point;
import static com.educery.utils.Utils.*;
import static com.educery.utils.LineBuilder.*;

/**
 * Represents an X/HTML element and its contents.
 *
 * <h4>Tag Responsibilities:</h4>
 * <ul>
 * <li>knows the attributes and contents of an element</li>
 * <li>formats an element as an X/HTML fragment</li>
 * </ul>
 */
public class Tag implements Registry.KeySource {

    /**
     * Defines a protocol for building tags.
     */
    public static interface Factory {

        public static final String TextStyle = "text-style";
        public static final String SansSerif = "sans-serif";
        public static final String TextAnchor = "text-anchor";
        public static final String FontFamily = "font-family";
        public static final String FontWeight = "font-weight";
        public static final String FontStyle = "font-style";
        public static final String FontSize = "font-size";

        public static final String FillOpacity = "fill-opacity";
        public static final String Fill = "fill";

        public static final String StrokeWidth = "stroke-width";
        public static final String Stroke = "stroke";

        public static final String White = "#ffffff";
        public static final String Black = "#000000";
        public static final String Middle = "middle";
        public static final String Normal = "normal";
        public static final String Italic = "italic";
        public static final String None = "none";

        public Tag drawElement();

        default Point getLocation() { return new Point(); }
        default Point getOffsets()  { return new Point(); }
        default Point getExtent()   { return new Point(); }

    } // Factory


    public static Tag named(String tagName) { return new Tag(tagName); }
    private Tag(String name) { this(); this.name = name; }
    private Tag() {
        namedValues().put(Content, Empty);
        namedValues().put(LinkBase, Empty);
    }

    static final String Anchor = "a";
    public boolean isAnchor() { return getKey().equals(Anchor); }

    static final String MarkDown = ".md";
    public boolean isMarkdown() { return getLink().endsWith(MarkDown); }

    static final String Source = "src";
    static final String HyperLink = "href";
    static final String XLink = "xlink:" + HyperLink;

    static final String[] Links = {XLink, HyperLink, Source};
    static final List<String> LinkTypes = wrap(Links);
    private boolean isLink(String key) { return LinkTypes.contains(key); }

    public Tag withLink(String link) { return with(HyperLink, link); }
    public static Tag linkWith(String reference) { return Tag.named(Anchor).withLink(reference); }
    public static Tag xlinkWith(String reference) { return Tag.named(Anchor).with(XLink, reference); }
    public String getLink() { return (this.hasValue(HyperLink) ? getValue(HyperLink) : getValue(XLink)); }
    public String getLinkName() { String link = getLink(); return link.substring(0, link.length() - MarkDown.length()); }

    static final String LinkBase = "xlink:base";
    public Tag withBase(String linkBase) { return with(LinkBase, linkBase); }

    static final String Polyline = "polyline";
    public static Tag polyline() { return Tag.named(Polyline); }

    static final String Polygon = "polygon";
    public static Tag polygon() { return Tag.named(Polygon); }

    static final String Rectangle = "rect";
    public static Tag rectangle() { return Tag.named(Rectangle); }

    static final String TextSpan = "tspan";
    public static Tag textSpan() { return Tag.named(TextSpan); }

    static final String Text = "text";
    public static Tag textBox() { return Tag.named(Text); }

    static final String SvgContext = "svg";
    public static Tag context() { return Tag.named(SvgContext); }

    static final String Graphic = "g";
    public static Tag graphic() { return Tag.named(Graphic); }

    static final String Italics = "i";
    public static Tag italics(String content) { return Tag.named(Italics).withContent(content); }
    public Tag withContent(String content) { return with(Empty, content); }

    static final String Image = "img";
    public boolean isImage() { return getKey().equals(Image) || Canvas.hasActiveCanvas(); }
    public static Tag imageWith(String reference) { return Tag.named(Image).with(Source, reference); }

    private final HashMap<String, String> namedValues = new HashMap();
    private HashMap<String, String> namedValues() { return this.namedValues; }
    private String getValue(String name) { return this.hasValue(name) ? namedValues().get(name) : Empty; }
    private boolean hasValue(String valueName) { return namedValues().containsKey(valueName); }
    private String getLinkValue(String key) { return isLink(key) ? getLinkBase() + getValue(key) : getValue(key); }
    private String getLinkBase() { return getValue(LinkBase); }

    static final String Content = Empty;
    public String getContent() { return getValue(Content); }

    static final String Dot = ".";
    public String topicRef() { return getContent().replace(Blank, Dot); }

    static final String[] InitialTags = { Content, LinkBase };
    private boolean taggedInitially(String key) { return wrap(InitialTags).contains(key.trim()); }

    private final ArrayList<String> valueNames = new ArrayList();
    private ArrayList<String> valueNames() { return this.valueNames; }
    private List<String> attributeNames() { return select(valueNames(), (k) -> !taggedInitially(k)); }
    public Tag withValues(Tag source) { includeValues(source); return this; }
    private void includeValues(Tag tag) { tag.valueNames().forEach((key) -> this.with(key, tag.getValue(key))); }

    private String name = Empty;
    @Override public String getKey() { return this.name; }

    static final String Align = "align";
    public Tag withAlign(String alignment) { return with(Align, alignment); }

    static final String X = "x";
    public int valueX() { return intValue(X); }
    public Tag withX(int value) { return this.with(X, value + Empty); }

    static final String Y = "y";
    public int valueY() { return intValue(Y); }
    public Tag withY(int value) { return this.with(Y, value + Empty); }

    public int intValue(String name) { return Integer.parseInt(getValue(name)); }
    public Tag withOrigin(Point p) { return this.withX(p.getX()).withY(p.getY()); }
    public Tag withExtent(Point p) { return this.withWidth(p.getX()).withHeight(p.getY()); }
    public Tag copyOrigin(Factory f) { return withOrigin(f.getLocation()); }
    public Tag copyExtent(Factory f) { return withExtent(f.getExtent()); }

    static final String Width = "width";
    public int valueWidth() { return intValue(Width); }
    public Tag withWidth(String value) { return this.with(Width, value); }
    public Tag withWidth(int value) { return this.with(Width, value + Empty); }
    public Tag copyWidth(Tag tag) { return this.withWidth(valueWidth()); }

    private static final String Height = "height";
    public int valueHeight() { return intValue(Height); }
    public Tag withHeight(String value) { return this.with(Height, value); }
    public Tag withHeight(int value) { return this.with(Height, value + Empty); }
    public Tag copyHeight(Tag tag) { return this.withHeight(valueHeight()); }

    static final String Style = "style";
    public Tag withStyle(Tag stylingTag) { return this.withStyle(stylingTag.getValue(Style)); }
    public Tag withStyle(String styleText) { return this.with(Style, styleText); }
    public Tag withStyle(String name, int value) { return this.withStyle(name, value + Empty); }
    public Tag withStyle(String name, String value) {
        String priorValue = getValue(Style);
        if (!priorValue.isEmpty()) priorValue += Semi + Blank;
        String namedValue = name + Colon + Blank + value;
        return this.withStyle(priorValue + namedValue); }

    public Tag with(String name, int value) { return this.with(name, value + Empty); }
    public Tag with(String name, String value) {
        if (!this.hasValue(name)) valueNames().add(name);
        namedValues().put(name, value); return this; }

    private final ArrayList<Tag> contentTags = new ArrayList();
    private ArrayList<Tag> tags() { return this.contentTags; }
    public boolean hasTags() { return !tags().isEmpty(); }
    public boolean hasContent() { return !getContent().isEmpty(); }
    public Tag with(Tag... elements) { return with(wrap(elements)); }
    public Tag with(List<Tag> elements) { tags().addAll(elements); return this; }

    public String format() { return build(b -> linkOrTag(b)); }
    public String formatDetail() { return build(b -> buildTag(b)); }
    public String formatReference() { return build(b -> buildRef(b)); }

//    public void buildPageLink(LineBuilder builder) { builder.term(getLinkBase(), getLink()); }
    private void linkOrTag(LineBuilder b) { if (isAnchor() && isMarkdown()) buildLink(b); else buildTag(b); }
    public void buildLink(LineBuilder b) { b.bangIf(isImage()); b.square(getContent()); b.square(getLinkName()); }
    public void buildRef(LineBuilder b) { b.square(topicRef()); b.tie(Colon, Blank, getLinkBase(), getLink()); }

    private void buildTags(LineBuilder builder) { tags().forEach(tag -> tag.buildTag(builder)); }
    private void buildTag(LineBuilder builder) {
        if (hasTags()) { // nested tags
            buildHead(builder);
            buildTags(builder);
            buildTail(builder);
        }
        else if (hasContent()) {
            buildHead(builder);
            builder.tie(getContent());
            buildTail(builder);
        }
        else { // simple head
            buildHeadOnly(builder);
        }

        if (this.isImage()) {
            builder.newLine();
        }
    }

    private void buildHeadOnly(LineBuilder builder) { builder.angle(getKey(), buildAttributes(), Slash); }
    private void buildHead(LineBuilder builder) { builder.angle(getKey(), buildAttributes()); }
    private void buildTail(LineBuilder builder) { builder.angle(Slash, getKey()); }

    private String buildAttributes() { return build(b -> buildAttributes(b)); }
    private void buildAttributes(LineBuilder b) { attributeNames().forEach(key -> buildAttribute(key, b)); }
    private void buildAttribute(String key, LineBuilder b) { b.blank(); b.nameValue(key, getLinkValue(key)); }

} // Tag
