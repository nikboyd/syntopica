package com.educery.sites;

import java.io.*;
import java.util.*;
import freemarker.template.*;
import static freemarker.template.TemplateExceptionHandler.*;

import com.educery.utils.*;
import com.educery.concepts.*;
import com.educery.concepts.Number;
import com.educery.facts.FactParser;
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
    public static ModelSite withForms(File formsFolder) {
        return new ModelSite().initialize(formsFolder); }

    static final String Format = "UTF-8";
    private final Configuration cfg = new Configuration();
    private Template getForm(String formName) { return nullOrTryLoudly(() -> this.cfg.getTemplate(formName)); }
    private ModelSite initialize(File formsFolder) {
        runLoudly(() -> { // configure FreeMarker
            cfg.setDirectoryForTemplateLoading(formsFolder);
            cfg.setTemplateExceptionHandler(HTML_DEBUG_HANDLER);
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setDefaultEncoding(Format);
        });
        return this;
    }

    public ModelSite withBases(File... baseFolders) {
        this.baseFolder   = baseFolders[0]; // base
        this.domainFolder = baseFolders[1]; // briefs
        this.pageFolder   = baseFolders[2]; // topics
        this.imageFolder  = baseFolders[3]; // images

        runQuietly(() -> {
            pageFolder().mkdirs();
            imageFolder().mkdirs();
            this.linkBase = "../" + Topics + Slash;
            this.imageBase = "../" + Images + Slash;
        });

        Site.SiteSource.register(this);
        return this;
    }

    static final String Topics = "topics";
    static final String Images = "images";

    private File baseFolder;
    public File baseFolder() { return this.baseFolder; }

    private File pageFolder;
    public File pageFolder() { return this.pageFolder; }

    private File imageFolder;
    public File imageFolder() { return this.imageFolder; }

    private File domainFolder;
    @Override public File domainFolder() { return this.domainFolder; }

    public ModelSite withFacts(File factsFile) { readDomainFacts(factsFile); return this; }
    void readDomainFacts(File factsFile) { new FactParser(factsFile).parseTokens(); }
    public Domain getDomain() { return Domain.getCurrentDomain(); }

    public void mapTopic(Topic topic) {
        String key = topic.getTitle();
        topicLinks().put(key, topic.formatPageLink(Number.SingularNumber));
        pluralLinks().put(key, topic.formatPageLink(Number.PluralNumber));
    }

    private final HashMap<String, String> topicLinks = emptyMap();
    @Override public HashMap<String, String> topicLinks() { return this.topicLinks; }
    public List<Topic> getLinkedTopics() {
        List<Topic> results = mapList(topicLinks().keySet(), (k) -> getDomain().getTopic(k));
        Collections.sort(results, (a,b) -> a.getTitle().compareTo(b.getTitle()));
        return results; }

    private final HashMap<String, String> pluralLinks = emptyMap();
    @Override public HashMap<String, String> pluralLinks() { return this.pluralLinks; }

    private String pageType = HyperText;
    @Override public String pageType() { return this.pageType; }
    public ModelSite withMarkdown() { this.pageType = MarkDown; return this; }

    private String linkBase = Empty;
    @Override public String linkBase() { return this.linkBase; }

    private String imageBase = Empty;
    @Override public String imageBase() { return this.imageBase; }

    static final String PageReport = "generated %d pages for: '%s'";
    public void generatePages() {
        List<Topic> topics = getDomain().getTopics().getItems();
        topics.forEach((topic) -> mapTopic(topic));
        topics.forEach((topic) -> generatePage(topic));
        generateInventory();
        report(format(PageReport, topics.size(), getDomain().getName())); }

    static final String DomainInventory = "domain-inventory";
    static final String InventoryTemplate = "inventory-template";
    private void generateInventory() {
        final HashMap<String, Object> rootMap = new HashMap();
        rootMap.put("domain", getDomain());
        rootMap.put("pageType", pageType());
        rootMap.put("site", this);

        File pageFile = new File(baseFolder(), DomainInventory + pageType());
        writePage(pageFile, (Writer writer) -> getForm(InventoryTemplate + pageType()).process(rootMap, writer));
    }

    static final String Graphics = ".svg";
    static final String PageTemplate = "page-template";
    private void generatePage(final Topic topic) {
//        Fact[] facts = topic.getLinkedFacts();
        for (Fact fact : topic.getLinkedFacts()) {
            String diagram = topic.buildDiagramSVG(fact);
            File imageFile = new File(imageFolder(), topic.formImageName(fact) + Graphics);
            writePage(imageFile, (Writer writer) -> writer.write(diagram));
        }

        String discussion = topic.buildDiscussion();
        final HashMap<String, Object> rootMap = new HashMap();
        rootMap.put("topic", topic);
        rootMap.put("domain", getDomain());
        rootMap.put("discussion", discussion);
        rootMap.put("pageType", pageType());
        rootMap.put("site", this);

        File pageFile = new File(pageFolder(), topic.getLinkFileName(pageType()));
        writePage(pageFile, (Writer writer) -> getForm(PageTemplate + pageType()).process(rootMap, writer));
    }

    private OutputStreamWriter buildWriter(File pageFile) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(pageFile)); }

    private void writePage(File pageFile, PageWriter pageWriter) {
        runLoudly(() -> { try (Writer writer = buildWriter(pageFile)) { pageWriter.writePage(writer); } }); }

} // ModelSite
