package com.educery.concept.models;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains the topics of a discourse domain.
 * 
 * <h4>Domain Responsibilities:</h4>
 * <ul>
 * <li>knows the available discourse domains</li>
 * <li>knows the current discourse domain</li>
 * <li>knows the topics within a domain</li>
 * <li>knows the predicates used within those topics</li>
 * <li>registers the topics and predicates within a domain</li>
 * </ul>
 */
public class Domain implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(Domain.class);
	private static final String ClassName = Domain.class.getSimpleName();

	private static Registry<Domain> Domains = Registry.empty();
	private static Domain Current = Domain.named("default");

	private String name = "";
	private Registry<Topic> topics = Registry.empty();
	private Registry<Selector> predicates = Registry.empty();
	
	/**
	 * Indicates whether a message contains a named domain.
	 * @param selector a message selector
	 * @param topics some topics
	 * @return whether a message contains a named domain
	 */
	public static boolean accepts(String selector, List<String> topics) {
		return (Selector.Named.equals(selector) && 
				!topics.isEmpty() && ClassName.equals(topics.get(0)));
	}
	
	/**
	 * Returns the current domain.
	 * @return a Domain
	 */
	public static Domain getCurrentDomain() {
		return Current;
	}
	
	/**
	 * Returns the domain of a given name.
	 * @param domainName a domain name
	 * @return a Domain
	 */
	public static Domain named(String domainName) {
		return Domains.register(Domain.withName(domainName));
	}
	
	/**
	 * Returns a new Domain.
	 * @param domainName a domain name
	 * @return a new Domain
	 */
	public static Domain withName(String domainName) {
		Domain result = new Domain();
		result.name = domainName;
		return result;
	}
	
	/**
	 * Constructs a new Domain.
	 */
	private Domain() { Current = this; }
	
	/**
	 * Returns the name of this domain.
	 * @return a domain name
	 */
	public String getName() {
		return this.name;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return getName();
	}
	
	/**
	 * Returns the topics associated with this domain.
	 * @return a topic Registry
	 */
	public Registry<Topic> getTopics() {
		return this.topics;
	}

	/**
	 * Returns the predicates associated with this domain.
	 * @return a predicate Registry
	 */
	public Registry<Selector> getPredicates() {
		return this.predicates;
	}
	
	/**
	 * Indicates whether the current domain contains a predicate.
	 * @param predicateName a predicate name
	 * @return whether the current domain contains the predicate
	 */
	public static boolean currentlyHasPredicate(String predicateName) {
		return getCurrentDomain().containsPredicate(predicateName);
	}
	
	/**
	 * Indicates whether the current domain contains a topic.
	 * @param topicName a topic name
	 * @return whether this domain contains the topic
	 */
	public static boolean currentlyHasTopic(String topicName) {
		return getCurrentDomain().containsTopic(topicName);
	}
	
	/**
	 * Returns a topic (if defined).
	 * @param topicName a topic name
	 * @return a Topic, or null
	 */
	public Topic getTopic(String topicName) {
		return getTopics().getItem(topicName);
	}
	
	/**
	 * Returns a predicate (if defined).
	 * @param predicateName a predicate name
	 * @return a Predication, or null
	 */
	public Selector getPredicate(String predicateName) {
		return getPredicates().getItem(predicateName);
	}
	
	/**
	 * Registers a topic in the current domain.
	 * @param topic a topic
	 * @return the registered Topic
	 */
	public static Topic register(Topic topic) {
		return getCurrentDomain().getTopics().register(topic);
	}
	
	/**
	 * Registers a predicate in the current domain.
	 * @param predicate a predicate
	 * @return the registered Predication
	 */
	public static Selector register(Selector predicate) {
		return getCurrentDomain().getPredicates().register(predicate);
	}
	
	/**
	 * Indicates whether this domain contains a topic.
	 * @param topicName a topic name
	 * @return whether this domain contains a topic
	 */
	public boolean containsTopic(String topicName) {
		return getTopics().hasItem(topicName);
	}
	
	/**
	 * Indicates whether this domain contains a predicate.
	 * @param predicateName a predicate name
	 * @return whether this domain contains a predicate
	 */
	public boolean containsPredicate(String predicateName) {
		return getPredicates().hasItem(predicateName);
	}
	
	/**
	 * Registers a topic in this domain.
	 * @param topic a topic
	 * @return the registered Topic
	 */
	public Topic registerTopic(Topic topic) {
		String topicName = topic.getKey();
		if (!this.containsTopic(topicName)) {
			getTopics().register(topic);
		}
		else {
			Logger.warn(getName() + " already has topic " + topicName);
		}
		return getTopics().getItem(topicName);
	}
	
	/**
	 * Registers a predicate in this domain.
	 * @param p a predicate
	 * @return the registered Predication
	 */
	public Selector registerPredicate(Selector p) {
		String predicateName = p.getKey();
		if (!this.containsPredicate(predicateName)) {
			getPredicates().register(p);
		}
		return getPredicates().getItem(predicateName);
	}
	
	/**
	 * Returns a reference link for this domain.
	 * @return a reference link
	 */
	public String getReferenceLink() {
		return Tag.linkWith("model.html").withContent("Model").format();
	}
	
	/**
	 * Dumps a description of this domain.
	 */
	public void dump() {
		Logger.info("domain: " + getName() + ", " + getTopics().countItems() + " topics");
	}

} // Domain