package com.educery.facts;

import com.educery.concepts.Topic;
import com.educery.utils.Logging;

/**
 * A concept in a conceptual model.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Concept implements Logging {

    // concept => topic named: name
    protected String name = "";
    public String name() { return this.name; }

    public Concept() {}
    public Concept(String name) { this.name = name; }
    public Concept makeTopic() { Topic.named(name); return this; }

} // Concept
