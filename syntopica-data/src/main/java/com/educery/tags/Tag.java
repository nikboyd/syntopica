package com.educery.tags;

import com.educery.utils.Registry;
import java.util.*;
import static com.educery.utils.Utils.*;

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

        public static final String TextAnchor = "text-anchor";
        public static final String FontFamily = "font-family";
        public static final String FontWeight = "font-weight";
        public static final String FontStyle = "font-style";
        public static final String FontSize = "font-size";

        public static final String Fill = "fill";
        public static final String FillOpacity = "fill-opacity";

        public static final String StrokeWidth = "stroke-width";
        public static final String Stroke = "stroke";

        public static final String White = "#ffffff";
        public static final String Black = "#000000";
        public static final String SansSerif = "sans-serif";
        public static final String Middle = "middle";
        public static final String Normal = "normal";
        public static final String Italic = "italic";
        public static final String None = "none";

        /**
         * Builds a new Tag.
         *
         * @return a new Tag
         */
        public Tag drawElement();

    } // Factory


    public static Tag named(String tagName) { return new Tag(tagName); }
    private Tag(String name) { this(); this.name = name; }
    private Tag() {
        namedValues().put(Content, Empty);
        namedValues().put(LinkBase, Empty);
    }

    static final String Anchor = "a";
    public boolean isAnchor() { return getKey().equals(Anchor); }

    static final String HyperLink = "href";
    public Tag withLink(String link) { return with(HyperLink, link); }
    public static Tag linkWith(String reference) { return Tag.named(Anchor).withLink(reference); }

    static final String Source = "src";
    static final String XLink = "xlink:" + HyperLink;
    public static Tag xlinkWith(String reference) { return Tag.named(Anchor).with(XLink, reference); }
    private String getLink() { return (this.hasValue(HyperLink) ? getValue(HyperLink) : getValue(XLink)); }

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

    static final String Content = "";
    public String getContent() { return getValue(Content); }

    static final String LinkBase = "xlink:base";
    public Tag withBase(String linkBase) { return with(LinkBase, linkBase); }

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

    public Tag withX(int value) { return this.with("x", value + Empty); }
    public Tag withY(int value) { return this.with("y", value + Empty); }

    static final String Width = "width";
    public Tag withWidth(String value) { return this.with(Width, value); }
    public Tag withWidth(int value) { return this.with(Width, value + Empty); }

    private static final String Height = "height";
    public Tag withHeight(String value) { return this.with(Height, value); }
    public Tag withHeight(int value) { return this.with(Height, value + Empty); }

    static final String SemiColon = ";";
    static final String Style = "style";
    public Tag withStyle(Tag stylingTag) { return this.withStyle(stylingTag.getValue(Style)); }
    public Tag withStyle(String styleText) { return this.with(Style, styleText); }
    public Tag withStyle(String name, int value) { return this.withStyle(name, value + Empty); }
    public Tag withStyle(String name, String value) {
        String priorValue = getValue(Style);
        if (!priorValue.isEmpty()) priorValue += SemiColon + Blank;
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
    public Tag with(Tag... elements) { tags().addAll(wrap(elements)); return this; }

    static final String MarkDown = ".md";
    public boolean isMarkdown() { return getLink().endsWith(MarkDown); }
    public String getLinkName() {
        String link = getLink(); return link.substring(0, link.length() - MarkDown.length()); }

    public String format() {
        StringBuilder builder = new StringBuilder();
        if (this.isAnchor() && this.isMarkdown()) {
            buildMarkdownLink(builder);
        } else {
            buildTag(builder);
        }
        return builder.toString();
    }

    static final String LeftMark = "[";
    static final String RightMark = "]";
    public void buildMarkdownLink(StringBuilder builder) {
        markImage(builder);
        builder.append(LeftMark);
        builder.append(getContent());
        builder.append(RightMark);
        builder.append(LeftMark);
        builder.append(getLinkName());
        builder.append(RightMark);
    }

    static final String Bang = "!";
    private void markImage(StringBuilder builder) { if (this.isImage()) builder.append(Bang); }

    static final String LeftEnd = "(";
    static final String RightEnd = ")";
    public void buildPageLink(StringBuilder builder) {
        builder.append(LeftEnd);
        builder.append(getLinkBase());
        builder.append(getLink());
        builder.append(RightEnd);
    }

    public String formatReference() { StringBuilder b = new StringBuilder(); buildReference(b); return b.toString(); }
    private void buildReference(StringBuilder builder) {
        builder.append(LeftMark);
        builder.append(getContent());
        builder.append(RightMark);
        builder.append(Colon);
        builder.append(Blank);
        builder.append(getLinkBase());
        builder.append(getLink());
    }

    static final String Slash = "/";
    static final String NewLine = "\n";
    static final String LeftBracket = "<";
    static final String RightBracket = ">";
    private void buildTags(StringBuilder builder) { tags().forEach((tag) -> tag.buildTag(builder)); }
    private void buildTag(StringBuilder builder) {
        if (hasTags()) { // nested tags
            buildHead(builder);
            buildTags(builder);
            buildTail(builder);
        }
        else if (hasContent()) {
            buildHead(builder);
            builder.append(getContent());
            buildTail(builder);
        }
        else { // simple head
            buildHeadOnly(builder);
        }

        if (this.isImage()) {
            builder.append(NewLine);
        }
    }

    private void buildHeadOnly(StringBuilder builder) {
        builder.append(LeftBracket);
        builder.append(getKey());
        buildAttributes(builder);
        builder.append(Slash);
        builder.append(RightBracket);
    }

    private void buildHead(StringBuilder builder) {
        builder.append(LeftBracket);
        builder.append(getKey());
        buildAttributes(builder);
        builder.append(RightBracket);
    }

    private void buildTail(StringBuilder builder) {
        builder.append(LeftBracket);
        builder.append(Slash);
        builder.append(getKey());
        builder.append(RightBracket);
    }

    static final String Quote = "\"";
    public static final String Equals = " = ";
    static final String[] Links = {XLink, HyperLink, Source};
    static final List<String> LinkTypes = wrap(Links);
    private boolean isLink(String key) { return LinkTypes.contains(key); }
    private void buildAttributes(StringBuilder builder) { attributeNames().forEach((key) -> buildAttribute(key, builder)); }
    private void buildAttribute(String key, StringBuilder builder) {
        builder.append(Blank);
        builder.append(key);
        builder.append(Equals.trim());
        builder.append(Quote);
        builder.append(getLinkValue(key));
        builder.append(Quote);
    }

} // Tag
