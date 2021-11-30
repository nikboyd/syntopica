package com.educery.concepts;

import java.io.*;
import java.util.*;
import com.educery.tags.*;
import com.educery.utils.*;
import static com.educery.utils.Site.*;
import static com.educery.utils.Utils.*;
import static com.educery.concepts.Number.*;
import static com.educery.utils.LineBuilder.*;
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
    public static Number getNumber(String subject) { return Number.getNumber(!isSingular(subject)); }
    public static List<String> namesFrom(String names) { return map(wrap(names.split(Comma)), (n) -> n.trim()); }
    public static List<Topic> listFrom(String names) { return map(namesFrom(names), (n) -> domain().getTopic(n)); }

    private String title = Empty; // is singular!
    private Topic(String title) { this.title = title; }
    public static Topic named(String title) { return new Topic(asSingular(title)); }
    public Topic with(String discussion) { setDiscussion(discussion); return this; }

    public String getTitle() { return this.title; }
    public String[] titleWords() { return getTitle().split(Blank); }
    public List<String> subjectWords() { return map(wrap(titleWords()), w -> capitalize(w)); }
    public String titleSubject() { return joinWith(Blank, subjectWords()); }

    @Override public String getKey() { return getTitle(); }
    public String getImageName() { return getTitle().replaceAll(Blank, Score); }
    public String getTitle(Number aNumber) {
        return aNumber.isPlural() ? asPlural(getTitle()) : asSingular(getTitle()); }

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
    public Registry<Fact> facts() { return this.facts; }
    public boolean hasFacts() { return !facts().isEmpty(); }
    public Topic register(Fact f) { facts().register(f); return this; }
    public Topic with(Fact... facts) { for (Fact fact : facts) register(fact); return this; }

    static final Fact[] NoFacts = { };
    public Fact[] getLinkedFacts() { return unwrap(facts().getItems(), NoFacts); }
    public Fact[] getFacts() { return getLinkedFacts(); }

    public String getArticle() { return getArticle(SingularNumber); }
    public String getArticle(Number aNumber) { return Number.getArticle(getTitle()); }

    public String getSubject() { return capitalize(getTitle()); }
    public String getSubject(Number aNumber) { return getTitle(aNumber); }
    public String formatSubjectLink() {
        return Tag.linkWith(getLinkFileName()).withBase("topics/").withContent(getSubject(SingularNumber)).formatDetail();
    }

    public static final String Text = ".txt";
    public String domainFileName() { return getLinkName() + Text; }
    public File domainFile() { return new File(Site.getSite().domainFolder(), domainFileName()); }
    public boolean hasSignificance() { return hasFacts() || domainFile().exists(); }

    public String formatPageLink(Number aNumber, String linkBase) {
        return Tag.linkWith(getLinkFileName()).withBase(linkBase).withContent(getTitle(aNumber)).format(); }

    public String formatPageLink(Number aNumber, String linkBase, String pageType) {
        return Tag.linkWith(getLinkFileName(pageType)).withBase(linkBase).withContent(getTitle(aNumber)).format(); }

    public String getLinkName() { return formatLinkName(getTitle()); }
    public String getLinkFileName(String pageType) { return getLinkName() + pageType; }
    public String getLinkFileName() { return getLinkFileName(PageType); }

    private static final String PageType = ".md";
    private String formatLinkName(String link) { return link.replaceAll(Blank, Period); }
    private String formatLink(String subject) { return formatLinkName(subject) + PageType; }
    public String buildLink(String s) { return Tag.linkWith(formatLink(s)).withContent(s).format(); }

    public String formatFact(Fact fact) { return formatFact(fact, Empty); }
    public String formatFact(Fact fact, String base) {
        return build((b) -> {
            String name = fact.mainTopic();
            Topic topic = domain().getTopic(name);
            if (fact.defines(this))
                b.blankAfterEach(Number.getArticle(name),
                    topic.formatFactLink(Number.getNumber(name), base));

            int[] px = { 0 };
            b.tie(Tag.italics(fact.getPredicate().getVerb()).format());
            if (fact.getValenceCount() > 1) {
                for (int index = 1; index < fact.topicCount(); index++) {
                    if (index > 1) {
                        b.blankBeforeEach(fact.getPredicate().nextPart(px));
                    }
                    name = fact.getTopic(index);
                    topic = domain().getTopic(name);
                    b.blankBeforeEach(Number.getArticle(name),
                        topic.formatFactLink(Number.getNumber(name), base));
                }
            }
        });
    }

    public String formatFactLink(Number n, String base) {
        return Tag.linkWith(getLinkFileName()).withBase(base).withContent(getTitle(n)).formatDetail(); }

    public String formatReference() { return getArticle() + Blank + formatPageLink(); }
    public String formatPageLink() { return formatPageLink(SingularNumber); }
    public String formatPageLink(Number n) {
        return Tag.linkWith(getLinkFileName()).withContent(getTitle(n)).format(); }

    public Site site() { return Site.getSite(); }
    public String linkBase() { return site().linkBase(); }
    public String formatRefLinks() { return formatRefLink() + NewLine + formatRefLink(PluralNumber); }
    public String formatRefLink() { return formatRefLink(SingularNumber); }
    public String formatRefLink(Number aNumber) {
        return Tag.linkWith(getLinkFileName()).withContent(getTitle(aNumber)).formatReference(); }

    static final String Right = "right";
    static final String Graphics = ".svg";
    public String imageBase() { return site().imageBase(); }
    public String formatImageLink() {
        String link = getLinkName() + Graphics;
        return Tag.imageWith(link).withBase(imageBase()).withAlign(Right).format(); }

    static final String ImageForm = "%s_%s";
    public String formImageName(Fact fact) { return format(ImageForm, getImageName(), fact.getVerb()); }
    public String buildDiagramSVG(Fact fact) {
        List<String> elements = fact.namedElements();
        int boxHeight = elements.size() * 3;
        int viewHeight = elements.size() * 100;
        int[] viewbox = {10, 8, 350, viewHeight};

        // note: canvas.close() deactivates the canvas
        try (Canvas canvas = Canvas.with(12, boxHeight).activate()) {
            return canvas.with(viewbox).with(fact.buildTags()).drawElement().format();
        }
    }

    public String buildDiscussion() {
        if (!domainFile().exists()) return "";

        // replace known links
        TopicReader reader = TopicReader.from(domainFile());
        String text = reader.readDiscussion();
        List<String> keys = new ArrayList(site().topicLinks().keySet());
        Collections.sort(keys, (Object o1, Object o2) -> {
            return o2.toString().length() - o1.toString().length();
        });
        for (String subject : keys) {
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
        if (!text.contains(cap)) return text;
        if (text.contains(LeftMark + cap)) return text;
        if (text.contains(cap + RightMark)) return text;
        return text.replace(cap + Blank, capLink + Blank);
    }

    private String fixPluralSubject(String subject, String text) {
        String plural = asPlural(subject);
        String pluralLink = site().pluralLinks().get(subject);
        if (!text.contains(plural)) return text;
        if (text.contains(LeftMark + plural)) return text;
        if (text.contains(plural + RightMark)) return text;
        return text.replace(plural, pluralLink);
    }

    private String fixSubjectLinks(String subject, String text) {
        return fixSubjectLink(subject, text, site().topicLinks().get(subject));
    }

    static final String LeftMark = "[";
    static final String RightMark = "]";
    private String fixSubjectLink(String subject, String text, String topicLink) {
        if (!text.contains(subject)) return text;
        if (text.contains(LeftMark + asPlural(subject))) return text;
        if (text.contains(LeftMark + asSingular(subject))) return text;
        if (text.contains(asPlural(subject) + RightMark)) return text;
        if (text.contains(asSingular(subject) + RightMark)) return text;

        text = text.replace(Blank + subject, Blank + topicLink);
        text = text.replace(subject + Blank, topicLink + Blank);
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

    static final String Break = "<br/>";
    private String fixLineBreaks(String text) {
        if (!site().usesMarkdown()) {
            text = text.replace(NewLine + NewLine, Break + Break);
        }
        return text;
    }

    private String capitalizeLink(String subject) {
        String topicLink = site().topicLinks().get(subject);
        String cap = capitalize(subject);
        String subStart = LeftMark + subject.charAt(0);
        String capStart = LeftMark + cap.charAt(0);
        return topicLink.replace(subStart, capStart);
    }

} // Topic
