package com.educery.concepts;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;
import static com.educery.utils.LineBuilder.*;

/**
 * A predicate selector. Represents the skeleton of a complete predicate,
 * as a template for a concrete statement of fact.
 *
 * <h4>Selector Responsibilities:</h4>
 * <ul>
 * <li>knows a predicate (a verb + some parts)</li>
 * <li>knows a valence count (number of terms)</li>
 * <li>produces a message selector</li>
 * <li>produces a fact from supplied topics</li>
 * </ul>
 */
public class Selector implements Registry.KeySource {

    public static final String Named = "named:";

    private int valenceCount = 0;
    public int getValenceCount() { return this.valenceCount; }

    static String[] NoParts = { };
    private final ArrayList<String> parts = new ArrayList();
    public List<String> parts() { return this.parts; }
    public int partCount() { return parts().size(); }
    public boolean hasParts() { return !parts().isEmpty(); }
    public String[] getParts() { return unwrap(parts(), NoParts); }
    public String getVerb() { return parts().get(0); }

    static final String OR = "or";
    static final String AND = "and";
    public String nextPart(int[] px) { return (px[0] < partCount() - 1) ? parts().get(++px[0]) : AND; }

    public static Selector withUnary(String verb) {
        if (verb.isEmpty()) throw reportMissingVerb();
        Selector result = new Selector();
        result.parts.add(verb);
        result.valenceCount = 1;
        return result;
    }

    public static Selector withVerb(String verb, String... parts) { return withVerb(verb, wrap(parts)); }
    public static Selector withVerb(String verb, List<String> parts) {
        if (verb.isEmpty()) throw reportMissingVerb();
        Selector result = new Selector();
        result.valenceCount = 2;
        result.parts.add(verb);
        return result.with(parts);
    }

    public static Selector fromSelector(String selector) {
        if (!selector.endsWith(Colon)) return withUnary(selector);
        return Selector.withParts(wrap(selector.split(Colon)));
    }

    public static Selector withParts(List<String> parts) {
        Selector result = new Selector();
        result.valenceCount = parts.size() + 1;
        result.parts.addAll(parts);
        return result;
    }

    public boolean isLocked() { return Domain.currentlyHasPredicate(getKey()); }
    public Selector with(String... parts) { return with(wrap(parts)); }
    public Selector with(List<String> parts) {
        if (isLocked()) {
            reportLockedPredicate();
            return this;
        }

        if (parts.size() > 0) {
            this.parts.addAll(parts);
            this.valenceCount += parts.size();
        }
        return this;
    }

    public void dump() { report("predicate selector = " + getSelector()); }
    @Override public String getKey() { return getSelector(); }

    private String selector = Empty;
    public boolean hasSelector() { return !this.selector.isEmpty(); }
    private String buildSelector() { return build(b -> { for (String part : getSelectorParts()) b.tie(part); }); }
    public String getSelector() { // build and cache selector
        if (!hasSelector() && hasParts()) this.selector = buildSelector(); return this.selector; }

    public String[] getSelectorParts() {
        int count = getValenceCount();
        String[] results = getParts(); // add a colon at the end of each part
        for (int index = 0; index < results.length; index++) if (count > 1) results[index] += Colon;
        if (results[0].contains(Score)) results[0] = results[0].replace(Score, Colon);
        return results; }

    public Fact buildFact(String subject, String... topics) { return buildFact(wrap(subject, topics)); }
    public Fact buildFact(String subject, List<String> topics) { return buildFact(wrap(subject, topics)); }
    public Fact buildFact(List<String> topics) { Domain.register(this); return new Fact(this).with(topics); }

    private void reportLockedPredicate() { whisper(getSelector() + " was locked after building a fact"); }
    private static RuntimeException reportMissingVerb() {
        return new IllegalArgumentException("predicate requires a verb"); }

} // Selector
