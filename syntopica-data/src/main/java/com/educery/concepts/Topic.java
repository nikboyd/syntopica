package com.educery.concepts;

import java.io.*;
import java.util.*;
import com.educery.tags.*;
import com.educery.utils.*;
import static com.educery.utils.Site.*;
import static com.educery.utils.Utils.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Describes a topic and its associations.
 *
 * <h4>Topic Responsibilities:</h4>
 * <ul>
 * <li>knows a title</li>
 * <li>knows a discussion (if given)</li>
 * <li>knows some associated facts (if configured)</li>
 * </ul>
 */
public class Topic implements Registry.KeySource, Logging {

    public static Domain domain() { return Domain.getCurrentDomain(); }
    public static Number getNumber(String subject) { return Number.getNumber(!Number.isSingular(subject)); }
    public static List<String> namesFrom(String names) { return map(wrap(names.split(Comma)), (n) -> n.trim()); }
    public static List<Topic> listFrom(String names) { return map(namesFrom(names), (n) -> domain().getTopic(n)); }

    private String title = Empty; // is singular!
    private Topic(String title) { this.title = title; }
    public static Topic named(String title) { return new Topic(Number.asSingular(title)); }
    public Topic with(Fact... facts) { for (Fact fact : facts) facts().register(fact); return this; }
    public Topic with(String discussion) { setDiscussion(discussion); return this; }

    public String getTitle() { return this.title; }
    @Override public String getKey() { return getTitle(); }
    public String getTitle(Number aNumber) { return aNumber.isPlural() ? Number.asPlural(getTitle()) : getTitle(); }

    public void reportTopic() { report("topic: " + getTitle()); }
    public void dump() { reportTopic(); if (isDefined()) getFacts()[0].dumpMessage(); }

    private String discussion = Empty;
    public String getDiscussion() { return this.discussion; }
    public void setDiscussion(String discussion) { this.discussion = discussion; }

    private boolean defined = false;
    public boolean isDefined() { return this.defined; }
    public Topic makeDefined() { this.defined = true; return this; }

    private final HashMap<String, String> linkedTopics = new HashMap();
    public Map<String, String> linkedTopics() { return this.linkedTopics; }
    public boolean hasLinkedTopics() { return !linkedTopics().isEmpty(); }
    public String formatLinkedReferences() {
        StringBuilder builder = new StringBuilder();
        linkedTopics().keySet().forEach((k) -> {
            if (builder.length() > 0) builder.append(NewLine);
            builder.append(formatLinkedReference(k));
        });
        return builder.toString();
    }
    public String formatLinkedReference(String subject) {
        return Tag.linkWith(linkedTopics().get(subject)).withContent(subject).formatReference(); }

    private final Registry<Fact> facts = Registry.empty();
    Registry<Fact> facts() { return this.facts; }

    static final Fact[] NoFacts = {};
    public Fact[] getFacts() { return unwrap(facts().getItems(), NoFacts); }

    public String getArticle() { return getArticle(Number.SingularNumber); }
    public String getArticle(Number aNumber) { return Number.getArticle(getTitle()); }

    public String getSubject() { return capitalize(getTitle()); }
    public String getSubject(Number aNumber) { return capitalize(getTitle(aNumber)); }

    static final String Text = ".txt";
    public String domainFileName() { return getLinkName() + Text; }
    public File domainFile() { return new File(Site.getSite().domainFolder(), domainFileName()); }

    public String formatPageLink(Number aNumber, String linkBase) {
        return Tag.linkWith(getLinkFileName()).withBase(linkBase).withContent(getTitle(aNumber)).format(); }

    public String formatPageLink(Number aNumber, String linkBase, String pageType) {
        return Tag.linkWith(getLinkFileName(pageType)).withBase(linkBase).withContent(getTitle(aNumber)).format(); }

    public String getLinkName() { return formatLinkName(getTitle()); }
    public String getLinkFileName(String pageType) { return getLinkName() + pageType; }
    public String getLinkFileName() { return getLinkFileName(PageType); }

    private static final String PageType = ".md";
    private String formatLinkName(String link) { return link.replace(Blank, Period); }
    private String formatLink(String subject) { return formatLinkName(subject) + PageType; }
    public String buildLink(String s) { return Tag.linkWith(formatLink(s)).withContent(s).format(); }

    public String formatFact(Fact fact) {
        String subject = fact.mainTopic();
        String[] parts = fact.getPredicate().getParts();
        StringBuilder builder = new StringBuilder();
        if (fact.defines(this)) {
            builder.append(Number.getArticle(subject));
            builder.append(Blank);
            builder.append(formatRelatedTopics(subject));
            builder.append(Blank);
        }

        builder.append(Tag.italics(parts[0]).format());
        if (fact.getValenceCount() > 1) {
            builder.append(Blank);
            builder.append(Number.getArticle(fact.getTopic(1)));
            builder.append(Blank);
            builder.append(formatRelatedTopics(fact.getTopic(1)));

            if (fact.getValenceCount() > 2) {
                for (int index = 2; index < fact.getValenceCount(); index++) {
                    builder.append(Blank);
                    builder.append(parts[index - 1]);
                    builder.append(Blank);
                    builder.append(Number.getArticle(fact.getTopic(index)));
                    builder.append(Blank);
                    builder.append(formatRelatedTopics(fact.getTopic(index)));
                }
            }
        }
        return builder.toString();
    }

    static final String OR = " or ";
    private String formatRelatedTopics(String topics) {
        StringBuilder builder = new StringBuilder();
        Topic.listFrom(topics).forEach((topic) -> {
            if (builder.length() > 0) builder.append(OR);
            builder.append(topic.formatPageLink());
        });
        return builder.toString(); }

    public String formatReference() { return getArticle() + Blank + formatPageLink(); }
    public String formatPageLink() { return formatPageLink(Number.SingularNumber); }
    public String formatPageLink(Number aNumber) {
        return Tag.linkWith(getLinkFileName()).withContent(getTitle(aNumber)).format(); }

    public Site site() { return Site.getSite(); }
    public String linkBase() { return site().linkBase(); }
    public String formatRefLinks() { return formatRefLink() + NewLine + formatRefLink(Number.PluralNumber); }
    public String formatRefLink() { return formatRefLink(Number.SingularNumber); }
    public String formatRefLink(Number aNumber) {
        return Tag.linkWith(getLinkFileName()).withContent(getTitle(aNumber)).formatReference(); }

    static final String Right = "right";
    static final String Graphics = ".svg";
    public String imageBase() { return site().imageBase(); }
    public String formatImageLink() {
        String link = getLinkName() + Graphics;
        return Tag.imageWith(link).withBase(imageBase()).withAlign(Right).format(); }

    public String buildDiagramSVG() {
        Fact fact = getFacts()[0];
        int boxHeight = fact.getValenceCount() * 3;
        int viewHeight = fact.getValenceCount() * 100;
        int[] viewbox = {10, 8, 350, viewHeight};

        // note: canvas.close() deactivates the canvas
        try (Canvas canvas = Canvas.with(12, boxHeight).activate()) {
            Tag.Factory[] tags = fact.buildTags();
            canvas.with(viewbox).with(tags);
            return canvas.drawElement().format();
        }
    }

    static final String NewLine = "\n";
    static final String Break = "<br/>";
    public String buildDiscussion() {
        if (!domainFile().exists()) return "";

        // replace known links
        TopicReader reader = TopicReader.from(domainFile());
        String text = reader.readDiscussion();
        for (String subject : site().topicLinks().keySet()) {
            text = fixLeadingSubject(subject, text);
            text = fixPluralSubject(subject, text);
            text = fixSubjectLinks(subject, text);
            text = fixLineBreaks(text);
        }

        // replace assigned links
        Map<String, String> linkMap = reader.getLinkMap();
        linkedTopics().putAll(linkMap);
        for (String subject : linkMap.keySet()) {
            text = fixLinkedSubject(subject, text);
        }

        return text;
    }

    private String fixLeadingSubject(String subject, String text) {
        String cap = capitalize(subject);
        String capLink = capitalizeLink(subject);
        // replace leading sentence subject with fixed link
        return text.contains(cap) ? text.replace(cap + Blank, capLink + Blank) : text;
    }

    private String fixPluralSubject(String subject, String text) {
        String plural = Number.asPlural(subject);
        String pluralLink = site().pluralLinks().get(subject);
        return text.contains(plural) ? text.replace(plural, pluralLink) : text;
    }

    private String fixSubjectLinks(String subject, String text) {
        String topicLink = site().topicLinks().get(subject);
        if (text.contains(subject)) {
            text = text.replace(Blank + subject, Blank + topicLink);
            text = text.replace(subject + Blank, topicLink + Blank);
        }
        return text;
    }

    private String fixLinkedSubject(String subject, String text) {
        if (text.contains(subject)) {
            String link = buildLink(subject);
            text = text.replace(Blank + subject, Blank + link);
            text = text.replace(subject + Blank, link + Blank);
        }
        return text;
    }

    private String fixLineBreaks(String text) {
        if (!site().usesMarkdown()) {
            text = text.replace(NewLine + NewLine, Break + Break);
        }
        return text;
    }

    private String capitalizeLink(String subject) {
        String topicLink = site().topicLinks().get(subject);
        String cap = capitalize(subject);
        String subStart = "[" + subject.charAt(0);
        String capStart = "[" + cap.charAt(0);
        return topicLink.replace(subStart, capStart);
    }

} // Topic
