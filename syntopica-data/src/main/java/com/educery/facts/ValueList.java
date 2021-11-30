package com.educery.facts;

import java.util.*;
import com.educery.concepts.*;
import static com.educery.utils.Utils.*;

/**
 * A value list.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class ValueList extends Alias {

    ArrayList<String> values = emptyList();
    public List<String> values() { return this.values; }

    private ValueList(String name, List<String> values) { super(name); this.values.addAll(values); }
    public static ValueList named(String name, List<String> values) { return new ValueList(name, values); }
    @Override public Concept makeTopic() {
        Selector.withVerb("contains").buildFact(name, values);
        Topic.domain().getTopic(name).makeDefined();
        return this;
    }

} // ValueList
