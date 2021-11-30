package com.educery.concepts;

import java.util.*;
import com.educery.tags.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.*;

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

    private Domain() { Current = this; }
    private Domain(String name) { this(); this.name = name; }
    public static Domain withName(String domainName) { return new Domain(domainName); }
    public static Domain named(String... list) { return named(wrap(list)); }

    private static final Registry<Domain> Domains = Registry.empty();
    public static Domain named(List<String> list) {
        return Domains.register(Domain.withName(joinWith(Blank, list))); }

    private static Domain Current = Domain.named("default");
    public static Domain current() { return Current; }
    public static Domain getCurrentDomain() { return current(); }

    private static final String ClassName = Domain.class.getSimpleName();
    public static boolean accepts(String selector, List<String> topics) {
        return (Selector.Named.equals(selector) && !topics.isEmpty() && ClassName.equals(topics.get(0))); }

    private String name = Empty;
    public String getName() { return this.name; }
    @Override public String getKey() { return getName(); }
    public String getTitle() {
        String[] names = getName().split(Blank);
        return join(mapList(wrap(names), n -> capitalize(n.trim())), Blank); }

    private final Registry<Topic> topics = Registry.empty();
    public Registry<Topic> topics() { return this.topics; }
    public Registry<Topic> getTopics() { return topics(); }
    public static Topic register(Topic topic) { return current().topics().register(topic); }
    public boolean containsTopic(String topicName) { return topics().hasItem(topicName); }
    public static boolean currentlyHasTopic(String topicName) { return current().containsTopic(topicName); }

    public List<Topic> getItems() {
        List<Topic> results = select(topics().getItems(), topic -> topic.hasSignificance());
        Collections.sort(results, (a,b) -> a.getTitle().compareTo(b.getTitle())); return results; }

    public Topic getTopic(String topicName) {
        String aName = Number.asSingular(topicName.trim());
        return (containsTopic(aName) ? topics().getItem(aName) : register(Topic.named(aName))); }

    private final Registry<Selector> predicates = Registry.empty();
    public Registry<Selector> predicates() { return this.predicates; }
    public Registry<Selector> getPredicates() { return predicates(); }
    public Selector registerPredicate(Selector p) { return predicates().register(p); }
    public Selector getPredicate(String predicateName) { return predicates().getItem(predicateName); }
    public static Selector register(Selector predicate) { return current().predicates().register(predicate); }
    public boolean containsPredicate(String predicateName) { return predicates().hasItem(predicateName); }
    public static boolean currentlyHasPredicate(String predicateName) {
        return current().containsPredicate(predicateName); }

    private final HashMap<String, String> topicLinks = new HashMap<>();
    public HashMap<String, String> getTopicLinks() { return this.topicLinks; }

    private final HashMap<String, String> pluralLinks = new HashMap<>();
    public HashMap<String, String> getPluralLinks() { return this.pluralLinks; }

    public String getReferenceLink() { return Tag.linkWith("model.md").withContent("model").format(); }

    static final String DomainReport = "domain: %s has %d topics";
    public void dump() { report(format(DomainReport, getName(), getTopics().countItems())); }

} // Domain
