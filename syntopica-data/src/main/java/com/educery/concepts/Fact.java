package com.educery.concepts;

import java.util.*;
import com.educery.tags.*;
import com.educery.utils.*;
import com.educery.graphics.Point;
import static com.educery.utils.Utils.*;

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
public class Fact implements Registry.KeySource {

    private final Domain domain;
    public Domain domain() { return this.domain; }
    public Domain getDomain() { return domain(); }

    private Selector predicate;
    public Selector predicate() { return this.predicate; }
    public Selector getPredicate() { return predicate(); }

    private String definedTopic = Empty;
    public String definedTopic() { return this.definedTopic; }
    public boolean isDefined() { return !definedTopic().isEmpty(); }
    public boolean defines(Topic topic) { return isDefined() && definedTopic().equals(topic.getTitle()); }

    static final String[] NoTopics = { };
    private final ArrayList<String> topics = new ArrayList();
    public String getTopic(int index) { return this.topics.get(index); }
    public String[] getTopics() { return unwrap(this.topics, NoTopics); }
    public ArrayList<String> getTopicList() { return new ArrayList(this.topics); }
    public int topicCount() { return this.topics.size(); }
    public String mainTopic() { return getTopic(0); }

    public List<String> getRelatedSubjects() {
        return map(select(this.topics, item -> !(item.trim().isEmpty())), item -> Number.asSingular(item)); }

    /**
     * Parses a message to produce a fact.
     *
     * @param message a message
     * @return a new Fact
     */
    public static Fact parseFrom(String message) {
        String definedTopic = Empty;
        String[] terms = message.split(Blank);

        String topic = Empty;
        String selector = Empty;
        ArrayList<String> topics = new ArrayList();
        for (String term : terms) {
            String aTerm = term.trim().replace(Period, Blank);
            if (!aTerm.isEmpty()) {
                if (aTerm.equals(Tag.Equals.trim())) {
                    definedTopic = topic.trim();
                    topic = Empty;
                } else if (aTerm.endsWith(Colon)) {
                    selector += aTerm;
                    topics.add(topic.trim());
                    topic = Empty;
                } else {
                    topic += aTerm;
                    topic += Blank;
                }
            }
        }

        topics.add(topic.trim());
        if (Domain.accepts(selector, topics)) {
            Domain.named(topics.get(1));
            return null; // domain registered!
        }

        Selector p = Selector.fromSelector(selector);
        Fact result = p.buildFact(topics);
        if (!definedTopic.isEmpty()) {
            result.define(Domain.getCurrentDomain().getTopic(definedTopic));
        }
        return result;
    }

    // prevents inappropriate external construction.
    private Fact() { this.domain = Domain.getCurrentDomain(); }

    public Fact(Selector p) { this(); this.predicate = p; }
    public Fact with(String... topics) { return this.with(wrap(topics)); }
    public Fact with(List<String> topics) {
        this.topics.addAll(checkTopics(topics));

        // register the subject of this statement of fact
        domain().registerTopic(domain().getTopic(mainTopic()).with(this));
        return this;
    }
    private List<String> checkTopics(List<String> topics) {
        if (topics.size() < 1) throw reportMissingSubject();
        if (topics.size() > getValenceCount()) throw reportExcessiveTopics();
        return topics; }

    public String getSentence() { return getMessage().replace(Colon, Empty); }
    public String getMessage() {
        String[] parts = getPredicate().getSelectorParts();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < topicCount(); index++) {
            builder.append(Blank);
            builder.append(getTopic(index));
            if (index < parts.length) {
                builder.append(Blank);
                builder.append(parts[index]);
            }
        }
        return builder.toString().trim() + Period;
    }

    @Override public String toString() { return getMessage(); }
    @Override public String getKey() { return getPredicate().getKey(); }
    public int getValenceCount() { return getPredicate().getValenceCount(); }

    public Fact define(Topic topic) {
        this.definedTopic = topic.getTitle();
        topic.facts().register(this);
        getDomain().getTopics().register(topic);
        return this; }

    static final Tag.Factory[] NoTags = { };
    public Tag.Factory[] buildTags() {
        ModelElement[] elements = buildModels();
        Connector[] connectors = buildConnectors(elements);
        ArrayList<Tag.Factory> results = new ArrayList<>();
        results.addAll(wrap(elements));
        results.addAll(wrap(connectors));
        return unwrap(results, NoTags);
    }

    private Connector[] buildConnectors(ModelElement[] elements) {
        if (elements.length < 2) return new Connector[0];
        String[] topics = getTopics();
        String[] labels = getPredicate().getParts();
        Connector[] results = new Connector[labels.length];
        for (int index = 0; index < labels.length; index++) {
            results[index] = Connector.named(labels[index]);
            if (index > 0) {
                results[index].emptyHeads();
            }
            if (index < elements.length - 1) {
                results[index].between(elements[index + 1], elements[0]);
            }
        }

        elements[0].addTails(results);
        for (int index = 0; index < results.length; index++) {
            int headCount = (Topic.getNumber(topics[index + 1]).isPlural() ? 2 : 1);
            elements[index + 1].addHeads(results[index].withHeads(headCount));
        }
        return results;
    }

    private ModelElement[] buildModels() {
        Point p = Point.at(10, 10);
        List<String> subjects = getRelatedSubjects();
        ModelElement[] results = new ModelElement[subjects.size()];
        for (int index = 0; index < results.length; index++) {
            results[index] = ModelElement.named(subjects.get(index));
            if (index > 0) {
                Point delta = Point.at(0, index * 100);
                results[index].withGrey().at(p.plus(delta));
            } else {
                results[index].withCyan().at(p);
                p = p.plus(Point.at(220, 0));
            }
        }
        return results;
    }

    static final String NewLine = "\n";
    public String formatRefLinks() {
        StringBuilder builder = new StringBuilder();
        getTopicList().forEach((subject) -> {
            if (builder.length() > 0) builder.append(NewLine);
            builder.append(getDomain().getTopic(subject).formatRefLink());
        });
        getTopicList().forEach((subject) -> {
            if (builder.length() > 0) builder.append(NewLine);
            builder.append(getDomain().getTopic(subject).formatRefLink(Number.PluralNumber));
        });
        return builder.toString();
    }

    public void dumpSentence() { report(getPrefix() + getSentence()); }
    public void dumpMessage() {
        report(getPrefix() + getMessage());
        report(getRelatedSubjects().toString()); }

    private String getPrefix() { return (this.isDefined() ? definedTopic() : getClass().getSimpleName()) + Tag.Equals; }

    private RuntimeException reportMissingSubject() {
        return new IllegalArgumentException("missing required subject for predicate " + getKey()); }

    private RuntimeException reportExcessiveTopics() {
        return new IllegalArgumentException("too many topics for predicate " + getKey()); }

} // Fact
