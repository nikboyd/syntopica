package com.educery.concept.models;

import org.junit.Test;
//import org.apache.commons.logging.*;

public class ConceptTest {
	
//	private static final Log Logger = LogFactory.getLog(ConceptTest.class);
	
	@Test
	public void readFacts() throws Exception {
		MessageReader.with(getClass().getResourceAsStream("/sample.txt")).readFacts();
		Domain.getCurrentDomain().dump();
	}
	
	@Test
	public void binaryTemplate() {
		Predication p = Predication.withVerb("has");
		p.dump();
	}
	
	@Test
	public void trinaryTemplate() {
		Predication p = Predication.withVerb("has").with("for");
		p.dump();

		Fact f = p.buildFact("Sample", "Dimple", "Example");
		f.dumpMessage();
		f.dumpSentence();

		Domain.getCurrentDomain().dump();
	}
	
	@Test
	public void unaryConcept() {
		Predication p = Predication.withUnary("works");
		p.dump();
	}
	
	@Test
	public void binaryConcept() {
		Predication p = Predication.withVerb("has");
		p.dump();
	}
	
	@Test
	public void complexConcept() {
		Predication p = Predication.withVerb("has", "for");
		p.dump();
	}

} // ConceptTest