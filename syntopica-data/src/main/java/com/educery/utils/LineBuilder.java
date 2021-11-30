package com.educery.utils;

import java.util.*;
import java.util.function.Consumer;
import static com.educery.utils.Utils.*;

/**
 * Builds a line of text.
 * @author nik <nikboyd@sonic.net>
 * @see "Copyright 2020 Nikolas S Boyd."
 * @see "Permission is granted to copy this work provided this copyright statement is retained in all copies."
 */
public class LineBuilder implements Logging {
    
    public static String build(Consumer<LineBuilder> build) {
        LineBuilder b = new LineBuilder(); build.accept(b); return b.toString(); }
    
    StringBuilder b = new StringBuilder();
    public StringBuilder builder() { return this.b; }
    public boolean hasSome() { return builder().length() > 0; }
    @Override public String toString() { return builder().toString(); }
    
    public LineBuilder newLine() { tie(NewLine); return this; }
    public LineBuilder blank() { tie(Blank); return this; }

    static final String Bang = "!";
    public LineBuilder bang() { tie(Bang); return this; }
    public LineBuilder bangIf(boolean needsBang) { return needsBang ? bang() : this; }

    public static final String Equal = "=";
    public LineBuilder equal() { tie(Equal); return this; }
    public LineBuilder nameValue(String name, String value) { tie(name); equal(); quote(value); return this; }

    static final String Quote = "\"";
    public LineBuilder quote(String ... texts) { tie(Quote); tie(texts); tie(Quote); return this; }

    static final String LeftEnd = "(";
    static final String RightEnd = ")";
    public LineBuilder term(String ... texts) { tie(LeftEnd); tie(texts); tie(RightEnd); return this; }

    static final String LeftMark = "[";
    static final String RightMark = "]";
    public LineBuilder square(String ... texts) { tie(LeftMark); tie(texts); tie(RightMark); return this; }

    static final String LeftBracket = "<";
    static final String RightBracket = ">";
    public LineBuilder angle(String ... texts) { tie(LeftBracket); tie(texts); tie(RightBracket); return this; }

    public LineBuilder tieAfterSome(String ... texts) { return tieAfterSome(wrap(texts)); }
    public LineBuilder tieAfterSome(List<String> texts) { return (hasSome() ? tie(texts) : this); }

    public LineBuilder tie(String ... texts) { return tie(wrap(texts)); }
    public LineBuilder tie(List<String> texts) { texts.forEach(it -> builder().append(it)); return this; }

    public LineBuilder blankAfterEach(String ... texts) { return blankAfterEach(wrap(texts)); }
    public LineBuilder blankAfterEach(List<String> texts) { 
        texts.forEach(it -> { builder().append(it); builder().append(Blank); }); return this; }

    public LineBuilder blankBeforeEach(String ... texts) { return blankBeforeEach(wrap(texts)); }
    public LineBuilder blankBeforeEach(List<String> texts) { 
        texts.forEach(it -> { builder().append(Blank); builder().append(it); }); return this; }

} // LineBuilder
