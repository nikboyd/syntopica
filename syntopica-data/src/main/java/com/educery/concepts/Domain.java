package com.educery.concepts;

import com.educery.concepts.Number;
import java.util.*;
import com.educery.utils.*;

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

    private static final String ClassName = Domain.class.getSimpleName();

    private static final Registry<Domain> Domains = Registry.empty();
    private static Domain Current = Domain.named("default");
    public static Domain getCurrentDomain() { return Current; }

    private String name = "";
    private final Registry<Topic> topics = Registry.empty();
    public Registry<Topic> topics() { return this.topics; }
    public Registry<Topic> getTopics() { return topics(); }

    private final Registry<Selector> predicates = Registry.empty();
    public Registry<Selector> predicates() { return this.predicates; }
    public Registry<Selector> getPredicates() { return predicates(); }

    private final HashMap<String, String> topicLinks = new HashMap<>();
    public HashMap<String, String> getTopicLinks() { return this.topicLinks; }

    private final HashMap<String, String> pluralLinks = new HashMap<>();
    public HashMap<String, String> getPluralLinks() { return this.pluralLinks; }

    private Domain() { Current = this; }
    private Domain(String name) { this(); this.name = name; }
    public static Domain withName(String domainName) { return new Domain(domainName); }
    public static Domain named(String domainName) { return Domains.register(Domain.withName(domainName)); }

    public String getName() { return this.name; }
    @Override public String getKey() { return getName(); }

    public static boolean currentlyHasPredicate(String predicateName) {
        return getCurrentDomain().containsPredicate(predicateName); }

    public static boolean accepts(String selector, List<String> topics) {
        return (Selector.Named.equals(selector) && !topics.isEmpty() && ClassName.equals(topics.get(0))); }

    public static boolean currentlyHasTopic(String topicName) { return getCurrentDomain().containsTopic(topicName); }
    public Topic getTopic(String topicName) {
        String aName = Number.asSingular(topicName.trim());
        return (this.containsTopic(aName) ? getTopics().getItem(aName) : Topic.named(aName)); }

    public Selector getPredicate(String predicateName) { return getPredicates().getItem(predicateName); }
    public static Topic register(Topic topic) { return getCurrentDomain().getTopics().register(topic); }
    public static Selector register(Selector predicate) { return getCurrentDomain().getPredicates().register(predicate); }

    public boolean containsTopic(String topicName) { return getTopics().hasItem(topicName); }
    public boolean containsPredicate(String predicateName) { return getPredicates().hasItem(predicateName); }

    public Topic registerTopic(Topic topic) {
        String topicName = topic.getKey();
        if (containsTopic(topicName)) reportRegistered(topicName);
        else getTopics().register(topic);
        return topic; }

    public Selector registerPredicate(Selector p) { return getPredicates().register(p); }

    public String getReferenceLink() { return Tag.linkWith("model.md").withContent("model").format(); }

    static final String DomainReport = "domain: %s has %d topics";
    public void dump() { report(format(DomainReport, getName(), getTopics().countItems())); }

    static final String AlreadyRegistered = "%s already has topic %s";
    private void reportRegistered(String topic) { warn(format(AlreadyRegistered, getName(), topic)); }

} // Domain
