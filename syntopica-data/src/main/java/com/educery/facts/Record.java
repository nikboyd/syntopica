package com.educery.facts;

import java.util.*;
import com.educery.concepts.*;
import static com.educery.utils.Utils.*;

/**
 * A composite of named items.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Record extends Alias {

    // record => ( topic named: name ) = fact: ( name contains: partNames ) "defined"
    private Record(String name) { super(name); }
    public static Record named(String name) { return new Record(name); }

    ArrayList<String> partNames = emptyList();
    public List<String> parts() { return this.partNames; }
    public Record withParts(List<String> list) { this.partNames.addAll(list); return this; }

    public void dump() { report(parts().toString()); }
    @Override public Concept makeTopic() {
        Selector.withVerb("has").buildFact(name, partNames);
        Topic.domain().getTopic(name).makeDefined();
        return this;
    }

} // Record
