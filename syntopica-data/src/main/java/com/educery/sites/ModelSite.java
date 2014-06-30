package com.educery.sites;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;

import freemarker.template.*;

import com.educery.concepts.*;
import com.educery.concepts.Topic.Number;
import com.educery.graphics.Point;
import com.educery.tags.*;
import com.educery.utils.*;

/**
 * Generates a web site from a domain model.
 * 
 * <h4>ModelSite Responsibilities:</h4>
 * <ul>
 * <li>reads domain model facts from a model file</li>
 * <li>generates model pages from the domain model</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>provide locations for a model file and page folder during construction</li>
 * </ul>
 */
public class ModelSite implements Registry.KeySource {

	private static final Log Logger = LogFactory.getLog(ModelSite.class);

	private static final String Slash = "/";
	private static final String Format = "UTF-8";
	private static final String MarkDown = ".md";
	private static final String HyperText = ".html";
	private static final String PageTemplate = "page-template";
	private static final String NewLine = "\n";
	private static final String Break = "<br/>";

	private File pageFolder;
	private File domainFolder;
	private String pageType = HyperText;
	private Configuration cfg = new Configuration();

	private String linkBase = "";
	private String imageBase = "";
	private HashMap<String, String> topicLinks = new HashMap<>();
	private HashMap<String, String> pluralLinks = new HashMap<>();

	/**
	 * Returns a new ModelSite.
	 * @param templateFolder a page template folder
	 * @return a new ModelSite
	 */
	public static ModelSite withTemplates(String templateFolder) {
		ModelSite result = new ModelSite();
		result.initialize(templateFolder);
		return result;
	}

	private ModelSite() { }
	
	private void initialize(String templateFolder) {
		try {
			cfg.setDirectoryForTemplateLoading(new File(templateFolder));
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setDefaultEncoding(Format);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	public ModelSite withBases(String ... linkBase) {
		if (linkBase.length > 0) {
			this.linkBase = linkBase[0];
			if (!this.linkBase.endsWith(Slash)) this.linkBase += Slash;
		}
		if (linkBase.length > 1) {
			this.imageBase = linkBase[1];
			if (!this.imageBase.endsWith(Slash)) this.imageBase += Slash;
		}
		return this;
	}
	
	public ModelSite withMarkdown() {
		this.pageType = MarkDown;
		return this;
	}

	/**
	 * Configures this site with a page folder.
	 * @param pageFolder a page folder
	 * @return this ModelSite
	 */
	public ModelSite withPages(String pageFolder) {
		this.pageFolder = new File(pageFolder);
		return this;
	}

	/**
	 * Configures this site with a model.
	 * @param modelFile a model file
	 * @return this ModelSite
	 */
	public ModelSite withModel(String domainFolder, String modelFile) {
		this.domainFolder = new File(domainFolder);
		FactReader.from(new File(domainFolder + modelFile)).readFacts();
		return this;
	}
	
	/**
	 * The registered topic links.
	 * @return a topic link map
	 */
	public HashMap<String, String> getTopicLinks() {
		return this.topicLinks;
	}
	
	/**
	 * The registered plural links.
	 * @return a plural link map
	 */
	public HashMap<String, String> getPluralLinks() {
		return this.pluralLinks;
	}
	
	public String getPageType() {
		return this.pageType;
	}
	
	public String getLinkBase() {
		return this.linkBase;
	}
	
	public String getImageBase()  {
		return this.imageBase;
	}
	
	public Domain getDomain() {
		return Domain.getCurrentDomain();
	}

	/**
	 * Generates the model site pages.
	 */
	public void generatePages() {
		List<Topic> topics = getDomain().getTopics().getItems();
		for (Topic topic : topics) mapTopic(topic);
		for (Topic topic : topics) generatePage(topic);
		Logger.info("generated " + topics.size() + " pages");
	}
	
	private void generatePage(Topic topic) {
		HashMap<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("topic", topic);
		rootMap.put("domain", getDomain());
		rootMap.put("diagram", buildDiagram(topic));
		rootMap.put("discussion", buildDiscussion(topic));
		rootMap.put("pageType", getPageType());
		rootMap.put("site", this);
		
		try {
			File pageFile = new File(this.pageFolder, topic.getLinkFileName(getPageType()));
			Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile));
			getTemplate(PageTemplate + getPageType()).process(rootMap, writer);
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
		}
		
		try {
			File pageFile = new File(this.pageFolder, topic.getLinkName() + ".svg");
			Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile));
			writer.write(buildDiagram(topic));
			writer.close();
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	private void mapTopic(Topic topic) {
		String key = topic.getSubject(Number.SingularNumber);
		getTopicLinks().put(key, formatPageLink(topic, Number.SingularNumber));
		getPluralLinks().put(key, formatPageLink(topic, Number.PluralNumber));
	}
	
	public String formatFact(Fact fact, Topic context) {
		String subject = fact.getTopic(0);
		String[] parts = fact.getPredicate().getParts();
		StringBuilder builder = new StringBuilder();
		if (fact.isDefined() && 
			!context.getTitle().equals(subject)) {
			builder.append(formatRelatedTopics(subject));
			builder.append(Blank);
		}

		builder.append(Tag.italics(parts[0]).format());
		if (fact.getValenceCount() > 1) {
			builder.append(Blank);
			builder.append(formatRelatedTopics(fact.getTopic(1)));
			if (fact.getValenceCount() > 2) {
				for (int index = 2; index < fact.getValenceCount(); index++) {
					builder.append(Blank);
					builder.append(parts[index - 1]);
					builder.append(Blank);
					builder.append(formatRelatedTopics(fact.getTopic(index)));
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * Formats the topics related to this fact as HTML fragments.
	 * @param topics a comma-separated list of topics
	 * @return a comma-separated list of topics formatted as HTML fragments
	 */
	private String formatRelatedTopics(String topics) {
		List<String> topicNames = Topic.namesFrom(topics);
		StringBuilder builder = new StringBuilder();
		for (String topicName : topicNames) {
			String subject = topicName.trim();
			String singularSubject = Number.convertToSingular(subject);
			Number aNumber = Number.getNumber(subject.length() > singularSubject.length());

			if (builder.length() > 0) builder.append(Comma + Blank);
			builder.append(formatPageLink(getDomain().getTopic(singularSubject), aNumber));
		}
		return builder.toString();
	}
	
	public String formatPageLink(Topic topic) {
		return formatPageLink(topic, Number.SingularNumber);
	}
	
	public String formatPageLink(Topic topic, Number aNumber) {
		String link = getLinkBase() + topic.getLinkName() + getPageType();
		return Tag.linkWith(link).withContent(topic.getSubject(aNumber)).format();
	}
	
	public String formatImageLink(Topic topic) {
		String link = getImageBase() + topic.getLinkName() + ".svg";
		return Tag.imageWith(link).withAlign("right").format();
	}
	
	public String buildDiscussion(Topic topic) {
		String fileName = this.domainFolder + Slash + topic.getLinkName() + ".txt";
		File topicFile = new File(fileName);
		if (!topicFile.exists()) return "";
		return buildDiscussion(topicFile);
	}

	/**
	 * Builds a topic discussion.
	 * @return a topic discussion
	 */
	private String buildDiscussion(File topicFile) {
		TopicReader reader = TopicReader.from(topicFile);
		String discussion = reader.readDiscussion();
		Map<String, String> linkMap = reader.getLinkMap();

		for (String subject : getTopicLinks().keySet()) {
			if (discussion.contains(subject)) {
				String plural = Number.convertToPlural(subject);
				discussion = discussion .replace(plural, getPluralLinks().get(subject));
				discussion = discussion.replace(Blank + subject, Blank + getTopicLinks().get(subject));
				discussion = discussion.replace(NewLine + NewLine, Break + Break);
			}
		}
		
		for (String subject : linkMap.keySet()) {
			if (discussion.contains(subject)) {
				String link = Tag.linkWith(linkMap.get(subject)).withContent(subject).format();
				discussion = discussion.replace(Blank + subject, Blank + link);
			}
		}
		
		return discussion;
	}
	
	public String buildDiagram(Topic topic) {
		Fact fact = topic.getFacts()[0];
		int boxHeight = fact.getValenceCount() * 3;
		int viewHeight = fact.getValenceCount() * 100;
		int[] viewbox = { 10, 8, 350, viewHeight };
		Tag.Factory[] tags = buildTags(fact);
		Canvas canvas = Canvas.with(12, boxHeight).with(viewbox).with(tags);
		return canvas.drawElement().format();
	}
	
	private Tag.Factory[] buildTags(Fact fact) {
		ModelElement[] elements = buildModels(fact);
		Connector[] connectors = buildConnectors(fact, elements);
		ArrayList<Tag.Factory> results = new ArrayList<>();
		results.addAll(Arrays.asList(elements));
		results.addAll(Arrays.asList(connectors));
		return results.stream().toArray(Tag.Factory[]::new);
	}

	private Connector[] buildConnectors(Fact fact, ModelElement[] elements) {
		if (elements.length < 2) return new Connector[0];
		String[] topics = fact.getTopics();
		String[] labels = fact.getPredicate().getParts();
		Connector[] results = new Connector[labels.length];
		for (int index = 0; index < labels.length; index++) {
			results[index] = Connector.named(labels[index]);
			if (index > 0) results[index].emptyHeads();
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
	
	private ModelElement[] buildModels(Fact fact) {
		Point p = Point.at(10, 10);
		List<String> topics = fact.getRelatedSubjects();
		ModelElement[] results = new ModelElement[topics.size()];
		for (int index = 0; index < results.length; index++) {
			results[index] = ModelElement.named(topics.get(index));
			if (index > 0) {
				Point delta = Point.at(0, index * 100);
				results[index].withGrey().at(p.plus(delta));
			}
			else {
				results[index].withCyan().at(p);
				p = p.plus(Point.at(220, 0));
			}
		}
		return results;
	}

	private Template getTemplate(String templateName) throws Exception {
		return this.cfg.getTemplate(templateName);
	}

	@Override
	public String getKey() {
		return Empty;
	}

} // ModelSite