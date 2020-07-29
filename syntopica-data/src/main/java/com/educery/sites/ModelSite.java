package com.educery.sites;

import java.io.*;
import java.util.*;
import freemarker.template.*;

import com.educery.utils.*;
import com.educery.concepts.*;
import com.educery.concepts.Number;
import static com.educery.utils.Utils.*;
import static com.educery.utils.Exceptional.*;

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
public class ModelSite implements Site {

    /**
     * Writes a page using a supplied Writer.
     */
    static interface PageWriter {

        public void writePage(Writer writer) throws Exception;

    } // PageWriter


    private ModelSite() { }
    public static ModelSite withTemplates(String templateFolder) {
        return new ModelSite().initialize(templateFolder); }

    static final String Format = "UTF-8";
    private final Configuration cfg = new Configuration();
    private ModelSite initialize(String templateFolder) {
        runLoudly(() -> {
            cfg.setDirectoryForTemplateLoading(new File(templateFolder));
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setDefaultEncoding(Format);
        });
        return this;
    }

    static final String Slash = "/";
    public ModelSite withBases(String domainFolder, String... linkBases) {
        this.domainFolder = new File(domainFolder);

        if (linkBases.length > 0) {
            this.linkBase = buildBase(linkBases[0], Topics + Slash);
        }
        if (linkBases.length > 1) {
            this.imageBase = buildBase(linkBases[1], Images + Slash);
        }

        Site.SiteSource.register(this);
        return this;
    }

    static String buildBase(String siteBase, String baseFolder) {
        String result = siteBase;
        if (!result.endsWith(Slash)) result += Slash;
        return result + baseFolder; }

    static final String Topics = "topics";
    static final String Images = "images";
    public ModelSite withPages(String pageFolder) {
        this.pageFolder = new File(pageFolder + Slash + Topics);
        this.imageFolder = new File(pageFolder + Slash + Images);
        this.pageFolder.mkdirs();
        this.imageFolder.mkdirs();
        return this;
    }

    private File pageFolder;
    public File pageFolder() { return this.pageFolder; }

    private File imageFolder;
    public File imageFolder() { return this.imageFolder; }

    private File domainFolder;
    @Override public File domainFolder() { return this.domainFolder; }

    public ModelSite withModel(String modelFile) { readDomainFacts(modelFile); return this; }
    private void readDomainFacts(String modelFile) { FactReader.from(new File(domainFolder, modelFile)).readFacts(); }

    public void mapTopic(Topic topic) {
        String key = topic.getTitle();
        topicLinks().put(key, topic.formatPageLink(Number.SingularNumber));
        pluralLinks().put(key, topic.formatPageLink(Number.PluralNumber));
    }

    private final HashMap<String, String> topicLinks = new HashMap<>();
    @Override public HashMap<String, String> topicLinks() { return this.topicLinks; }
    public List<Topic> getLinkedTopics() {
        List<Topic> results = mapList(topicLinks().keySet(), (k) -> getDomain().getTopic(k));
        Collections.sort(results, (a,b) -> a.getTitle().compareTo(b.getTitle()));
        return results; }

    private final HashMap<String, String> pluralLinks = new HashMap<>();
    @Override public HashMap<String, String> pluralLinks() { return this.pluralLinks; }

    private String pageType = HyperText;
    @Override public String pageType() { return this.pageType; }
    public ModelSite withMarkdown() { this.pageType = MarkDown; return this; }

    private String linkBase = Empty;
    @Override public String linkBase() { return this.linkBase; }

    private String imageBase = Empty;
    @Override public String imageBase() { return this.imageBase; }

    public Domain getDomain() { return Domain.getCurrentDomain(); }

    static final String PageReport = "generated %d pages";
    public void generatePages() {
        imageFolder().mkdirs();
        List<Topic> topics = getDomain().getTopics().getItems();
        topics.forEach((topic) -> mapTopic(topic));
        topics.forEach((topic) -> generatePage(topic));
        report(format(PageReport, topics.size())); }

    static final String Graphics = ".svg";
    static final String PageTemplate = "page-template";
    private void generatePage(final Topic topic) {
        String diagram = topic.buildDiagramSVG();
        String discussion = topic.buildDiscussion();
        final HashMap<String, Object> rootMap = new HashMap();
        rootMap.put("topic", topic);
        rootMap.put("domain", getDomain());
        rootMap.put("diagram", diagram);
        rootMap.put("discussion", discussion);
        rootMap.put("pageType", pageType());
        rootMap.put("site", this);

        File pageFile = new File(pageFolder(), topic.getLinkFileName(pageType()));
        writePage(pageFile, (Writer writer) -> getTemplate(PageTemplate + pageType()).process(rootMap, writer));

        File imageFile = new File(imageFolder(), topic.getLinkName() + Graphics);
        writePage(imageFile, (Writer writer) -> writer.write(diagram));
    }

    private Template getTemplate(String templateName) throws Exception {
        return this.cfg.getTemplate(templateName); }

    private OutputStreamWriter buildWriter(File pageFile) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(pageFile)); }

    private void writePage(File pageFile, PageWriter pageWriter) {
        runLoudly(() -> { try (Writer writer = buildWriter(pageFile)) { pageWriter.writePage(writer); } }); }

} // ModelSite
