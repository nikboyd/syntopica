package com.educery.facts;

import com.educery.concepts.*;

/**
 * A concept derived from another in a conceptual model.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Breed extends Concept {

    // breed => ( topic named: name ) = fact: ( name extends: base ) "defined"
    Concept base;

    private Breed(String name, String base) { super(name); this.base = new Concept(base); }
    public static Breed named(String name, String base) { return new Breed(name, base); }
    @Override public Concept makeTopic() {
        Selector.withVerb("extends").buildFact(name, base.name);
        Topic.domain().getTopic(name).makeDefined();
        return this;
    }

} // Breed
