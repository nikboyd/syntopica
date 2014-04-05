package com.educery.concept.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Domain implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(Domain.class);

	private static Registry<Domain> Domains = Registry.empty();
	private static Domain Current = Domain.named("default");
	
	public static Domain getCurrentDomain() {
		return Current;
	}

	private String name = "";
	private Registry<Topic> topics = Registry.empty();
	private Registry<Predication> predicates = Registry.empty();
	
	public static Domain named(String domainName) {
		return Domains.register(Domain.withName(domainName));
	}
	
	public static Domain withName(String domainName) {
		Domain result = new Domain();
		result.name = domainName;
		return result;
	}
	
	private Domain() { Current = this; }
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String getKey() {
		return getName();
	}
	
	public Registry<Topic> getTopics() {
		return this.topics;
	}
	
	public Registry<Predication> getPredicates() {
		return this.predicates;
	}
	
	public static boolean contains(Predication p) {
		return getCurrentDomain().getPredicates().hasItem(p.getKey());
	}
	
	public static boolean contains(Topic topic) {
		return containsTopic(topic.getKey());
	}
	
	public static boolean containsTopic(String title) {
		return getCurrentDomain().getTopics().hasItem(title);
	}
	
	public static Topic registerTopic(String title) {
		if (!containsTopic(title)) register(Topic.named(title));
		return getCurrentDomain().getTopics().getItem(title);
	}
	
	public static Topic register(Topic topic) {
		return getCurrentDomain().getTopics().register(topic);
	}
	
	public static Predication register(Predication predicate) {
		return getCurrentDomain().getPredicates().register(predicate);
	}
	
	public void dump() {
		Logger.info("domain: " + getName() + ", " + getTopics().countItems() + " topics");
	}

} // Domain