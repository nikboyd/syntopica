package com.educery.concept.models;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

@Ignore
public class ConceptTest {
	
//	private static final Log Logger = LogFactory.getLog(ConceptTest.class);
	
	@Test
	public void readFacts() throws Exception {
		FactReader.with(getClass().getResourceAsStream("/sample.txt")).readFacts();
		Domain.getCurrentDomain().dump();
	}
	
	@Test
	public void binaryTemplate() {
		Selector p = Selector.withVerb("has");
		p.dump();
	}
	
	@Test
	public void trinaryTemplate() {
		Selector p = Selector.withVerb("has").with("for");
		p.dump();

		Fact f = p.buildFact("Sample", "Dimple", "Example");
		f.dumpMessage();
		f.dumpSentence();

		Domain.getCurrentDomain().dump();
		List<Topic> topics = Domain.getCurrentDomain().getTopics().getItems();
		assertFalse(topics.isEmpty());
	}
	
	@Test
	public void unaryConcept() {
		Selector p = Selector.withUnary("works");
		p.dump();
	}
	
	@Test
	public void binaryConcept() {
		Selector p = Selector.withVerb("has");
		p.dump();
	}
	
	@Test
	public void complexConcept() {
		Selector p = Selector.withVerb("has", "for");
		p.dump();
	}

} // ConceptTest