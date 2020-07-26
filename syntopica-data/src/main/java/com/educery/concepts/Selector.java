package com.educery.concepts;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

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
    public String[] getParts() { return unwrap(this.parts, NoParts); }
    public String getVerb() { return this.parts.get(0); }

    public static Selector withUnary(String verb) {
        if (verb.isEmpty()) throw reportMissingVerb();
        Selector result = new Selector();
        result.parts.add(verb);
        result.valenceCount = 1;
        return result;
    }

    public static Selector withVerb(String verb, String... parts) {
        if (verb.isEmpty()) throw reportMissingVerb();
        Selector result = new Selector();
        result.valenceCount = 2;
        result.parts.add(verb);
        return result.with(parts);
    }

    public static Selector fromSelector(String selector) {
        if (!selector.endsWith(Colon)) return withUnary(selector);
        String[] parts = selector.split(Colon);
        return Selector.withParts(wrap(parts));
    }

    private static Selector withParts(List<String> parts) {
        Selector result = new Selector();
        result.valenceCount = parts.size() + 1;
        result.parts.addAll(parts);
        return result;
    }

    public boolean isLocked() { return Domain.currentlyHasPredicate(getKey()); }

    /**
     * Adds some more parts to this predication.
     *
     * @param parts some parts
     * @return this Predication
     */
    public Selector with(String... parts) {
        if (isLocked()) {
            reportLockedPredicate();
            return this;
        }

        if (parts.length > 0) {
            this.parts.addAll(wrap(parts));
            this.valenceCount += parts.length;
        }
        return this;
    }

    public void dump() { report("predicate selector = " + getSelector()); }
    @Override public String getKey() { return getSelector(); }
    public String getSelector() {
        StringBuilder builder = new StringBuilder();
        for (String part : getSelectorParts()) builder.append(part);
        return builder.toString(); }

    public String[] getSelectorParts() {
        int count = getValenceCount();
        String[] results = getParts(); // add a colon at the end of each part
        for (int index = 0; index < results.length; index++) if (count > 1) results[index] += Colon;
        return results; }

    public Fact buildFact(List<String> topics) { Domain.register(this); return new Fact(this).with(topics); }
    public Fact buildFact(String subject, String... topics) { return buildFact(wrap(subject)).with(wrap(topics)); }

    private static RuntimeException reportMissingVerb() {
        return new IllegalArgumentException("predicate requires a verb"); }

    private void reportLockedPredicate() {
        warn(getSelector() + " was locked after building a fact"); }

} // Selector
