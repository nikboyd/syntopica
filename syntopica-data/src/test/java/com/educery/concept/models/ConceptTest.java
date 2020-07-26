package com.educery.concept.models;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

import com.educery.concepts.*;
import com.educery.utils.*;

//@Ignore
public class ConceptTest implements Logging {

    @Test public void variousConcepts() {
        Selector.withVerb("has").dump();
        Selector.withUnary("works").dump();
        Selector.withVerb("has").dump();
        Selector.withVerb("has", "for").dump();
    }

    @Test public void trinaryTemplate() {
        Selector s = Selector.withVerb("has").with("for");
        s.dump();

        Fact f = s.buildFact("Sample", "Dimple", "Example");
        f.dumpMessage();
        f.dumpSentence();

        Domain.getCurrentDomain().dump();
        List<Topic> topics = Domain.getCurrentDomain().getTopics().getItems();
        assertFalse(topics.isEmpty());
    }

} // ConceptTest
