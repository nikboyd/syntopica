package com.educery.facts;

import com.educery.concepts.*;

/**
 * A pair of equivalent concepts in a conceptual model.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Alike extends Alias {

    // alike => ( topic named: name ) = fact: ( name equals: twin ) "defined"
    Concept twin;

    private Alike(String name, String twin) { this.name = name; this.twin = new Concept(twin); }
    public static Alike named(String name, String twin) { return new Alike(name, twin); }
    @Override public Concept makeTopic() {
        Selector.withVerb("equals").buildFact(name, twin.name);
        Topic.domain().getTopic(name).makeDefined();
        return this;
    }
    
    public static void nameFact(String name, Fact fact) {
        Topic.domain().getTopic(name).with(fact).makeDefined();
    }

} // Alike
