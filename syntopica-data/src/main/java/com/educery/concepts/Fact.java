package com.educery.concepts;

import java.util.*;
import com.educery.tags.*;
import com.educery.utils.*;
import com.educery.graphics.Point;
import static com.educery.utils.Utils.*;
import static com.educery.utils.LineBuilder.*;

/**
 * Expresses a statement of fact.
 *
 * <h4>Fact Responsibilities:</h4>
 * <ul>
 * <li>knows the predicate from which it was derived</li>
 * <li>knows the topics associated with this fact, esp. its subject</li>
 * <li>knows the placement of each topic within the predicate</li>
 * <li>formats XHTML fragments</li>
 * <li>creates facts from messages</li>
 * </ul>
 */
public class Fact implements Registry.KeySource, Logging {

    // prevents inappropriate external construction.
    private Fact() { this.domain = Domain.current(); }
    public Fact(Selector p) { this(); this.predicate = p; }
    public Fact with(String... topics) { return this.with(wrap(topics)); }
    public Fact with(List<String> topics) { topics().addAll(checkTopics(topics)); register(); return this; }

    private final Domain domain;
    public Domain domain() { return this.domain; }
    public Domain getDomain() { return domain(); }
    private void register() { domain().getTopic(mainTopic()).register(this); }

    private List<String> checkTopics(List<String> topics) { // no empty topics!
        List<String> results = select(topics, item -> !(item.trim().isEmpty()));
        if (results.isEmpty()) throw reportMissingSubject();
//        if (results.size() > getValenceCount()) throw reportExcessiveTopics();
        return results; }

    private Selector predicate;
    public Selector predicate() { return this.predicate; }
    public Selector getPredicate() { return predicate(); }
    public String getVerb() { return predicate().getVerb(); }

    private String definedTopic = Empty;
    public String definedTopic() { return this.definedTopic; }
    public boolean isDefined() { return !definedTopic().isEmpty(); }
    public boolean defines(Topic topic) { return isDefined() && definedTopic().equals(topic.getTitle()); }

    static final String[] NoTopics = { };
    private final ArrayList<String> topics = new ArrayList();
    public int topicCount() { return topics().size(); }
    private List<String> topics() { return this.topics; }
    public String[] getTopics() { return unwrap(topics(), NoTopics); }
    public ArrayList<String> getTopicList() { return copyList(topics()); }

    public String mainTopic() { return getTopic(0); }
    public String getTopic(int index) { return topics().get(index); }
    public List<String> getRelatedSubjects() { return map(topics(), item -> Number.asSingular(item)); }
    public List<String> namedElements() { // collect all named topics
        return buildList(names -> { topics().forEach(topic -> names.addAll(Topic.namesFrom(topic))); }); }

    public String getSentence() { return getMessage().replace(Colon, Empty); }
    public String getMessage() { return buildMessage().trim() + Period; }
    private String buildMessage() {
        String[] parts = getPredicate().getSelectorParts();
        return build(b -> {
            for (int index = 0; index < topicCount(); index++) {
                b.blankBeforeEach(getTopic(index));
                if (index < parts.length) {
                    b.blankBeforeEach(parts[index]);
                }
            }
        });
    }

    @Override public String toString() { return getMessage(); }
    @Override public String getKey() { return getPredicate().getKey(); }
    public int getValenceCount() { return getPredicate().getValenceCount(); }

    public Fact define(Topic topic) {
        this.definedTopic = topic.getTitle();
        topic.facts().register(this);
        getDomain().getTopics().register(topic);
        return this; }

    public List<Tag.Factory> buildTags() {
        List<ModelElement> elements = buildModels();
        return buildList(list -> {
            list.addAll(elements);
            list.addAll(buildConnectors(elements));
        });
    }

    private int headCount(String name) { return (Topic.getNumber(name).isPlural() ? 2 : 1); }
    private List<Connector> buildConnectors(List<ModelElement> elements) {
        if (elements.size() < 2) return emptyList();

        List<String> names = namedElements();
        String[] labels = getPredicate().getParts();
        assert(names.size() == elements.size());

        // build a connector per related element
        return buildList(list -> {
            int lx = 0; // label index
            int ex = 1; // element index
            for ( ; ex < elements.size(); ex++) {
                String label = labels[lx]; // fill first heads
                list.add(Connector.named(label).fillHeads(lx < 1));
                if (lx < labels.length - 1) lx++;

                int c = list.size() - 1;
                if (ex < elements.size()) {
                    // connect each related topic to its main subject
                    list.get(c).between(elements.get(ex), elements.get(0));
                }
            }

            // add tails to connectors in results
            reportCounts(list, elements, names);
            elements.get(0).addTails(list);

            // add heads to connectors in results, with appropriate number
            for (int rx = 0; rx < list.size(); rx++) {
                int headCount = headCount(names.get(rx + 1));
                elements.get(rx + 1).addHeads(list.get(rx).withHeads(headCount));
            }
        });
    }

    static final Point Main = Point.at(10, 10);
    static final Point Base = Point.at(230, 0);
    private List<ModelElement> buildModels() {
        List<String> subjects = getRelatedSubjects();
        return buildList(results -> {
            for (int index = 0; index < subjects.size(); index++) {
                List<String> names = Topic.namesFrom(subjects.get(index));
                for (String name : names) {
                    results.add(ModelElement.named(name));
                    int m = results.size() - 1;
                    if (index > 0) { // related element
                        results.get(m).withGrey().at(Base.plus(Point.at(0, m * 100)));
                    } else { // main subject
                        results.get(m).withCyan().at(Main);
                    }
                }
            }
        });
    }

    public String formatRefLinks() {
        return build(b -> {
            getTopicList().forEach((subject) -> {
                if (b.hasSome()) b.newLine();
                b.tie(getDomain().getTopic(subject).formatRefLink());
            });
            getTopicList().forEach((subject) -> {
                if (b.hasSome()) b.newLine();
                b.tie(getDomain().getTopic(subject).formatRefLink(Number.PluralNumber));
            });
        });
    }

    static final String CountsReport = "results: %d, elements: %d, names: %d";
    private void reportCounts(List results, List elements, List names) {
        whisper(format(CountsReport, results.size(), elements.size(), names.size())); }

    public void dumpSentence() { report(formatDefinition(getSentence())); }
    public void dumpMessage() {
        report(formatDefinition(getMessage()));
        report(getRelatedSubjects().toString()); }

    static final String Defined = "%s = %s";
    private String formatDefinition(String value) { return format(Defined, getPrefix(), value); }
    private String getPrefix() { return this.isDefined() ? definedTopic() : getClass().getSimpleName(); }

    private RuntimeException reportMissingSubject() {
        return new IllegalArgumentException("missing required subject for predicate " + getKey()); }

//    private RuntimeException reportExcessiveTopics() {
//        return new IllegalArgumentException("too many topics for predicate " + getKey()); }

} // Fact
