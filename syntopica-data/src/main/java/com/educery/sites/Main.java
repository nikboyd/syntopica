package com.educery.sites;

import java.io.*;
import java.net.*;
import static org.apache.commons.io.FileUtils.*;

import static com.educery.sites.ModelSite.*;
import static com.educery.concepts.Topic.Text;
import static com.educery.utils.Site.MarkDown;
import static com.educery.utils.Exceptional.*;
import static com.educery.utils.Utils.*;
import com.educery.utils.Logging;

/**
 * A command line interface for Syntopica tool.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Main implements Logging {

    public static void main(String... args) { new Main().launchTool(args); }
    void launchTool(String... args) {
        File baseFolder = locateBase(args);
        if (hasNo(baseFolder)) { reportUsage(); return; }

        File briefsFolder = new File(baseFolder, Briefs);
        File domainFacts = locateFacts(briefsFolder, args);
        if (hasNo(domainFacts)) { reportUsage(); return; }

        File formsFolder = locateForms(baseFolder);
        File topicsFolder = new File(baseFolder, Topics);
        File imagesFolder = new File(baseFolder, Images);

        runQuietly(() -> reportEach(Empty,
            format(FormsReport, formsFolder.getCanonicalPath()),
            format(BriefsReport, briefsFolder.getCanonicalPath()),
            format(BuildReport, topicsFolder.getCanonicalPath()),
            format(ImageReport, imagesFolder.getCanonicalPath())
            ));

        ModelSite.withForms(formsFolder)
            .withBases(baseFolder, briefsFolder, topicsFolder, imagesFolder)
            .withFacts(domainFacts)
            .withMarkdown()
            .generatePages();
    }

    static final String FormsReport  = "  using forms from: %s";
    static final String BriefsReport = "   and briefs from: %s";
    static final String BuildReport  = "building topics in: %s";
    static final String ImageReport  = "     and images in: %s";

    static final String Dot = ".";
    File locateBase(String... args) {
        String basePath = Dot;
        if (args.length > 0) basePath = args[0];
        File baseFolder = new File(basePath);
        if (!baseFolder.exists()) { reportMissing(basePath); return null; }
        return nullOrTryQuietly((b) -> b.getCanonicalFile(), baseFolder);
    }

    static final String Briefs = "briefs";
    static final String Facts = "domain-facts";
    File locateFacts(File briefsFolder, String... args) {
        String factsFile = Facts;
        if (args.length > 1) factsFile = args[1];
        if (!factsFile.endsWith(Text)) factsFile += Text;

        File domainFacts = new File(briefsFolder, factsFile);
        if (!domainFacts.exists()) {
            reportMissing(factsFile); reportPossible(briefsFolder.getPath(), factsFile); return null;
        }

        return domainFacts;
    }

    static final String Forms = "forms";
    File locateForms(File baseFolder) {
        File formsFolder = new File(baseFolder, Forms);
        if (!formsFolder.exists()) copyForms(formsFolder);
        return formsFolder; }

    void copyForms(File formsFolder) {
        formsFolder.mkdirs();
        copyResource(Forms, PageTemplate + MarkDown, formsFolder);
        copyResource(Forms, InventoryTemplate + MarkDown, formsFolder); }

    void copyResource(String folderName, String fileName, File targetFolder) {
        File folder = new File(folderName);
        File file = new File(folder, fileName);
        URL source = getClass().getResource(Slash + file.getPath());
        runLoudly(() -> { copyURLToFile(source, new File(targetFolder, fileName)); }); }

    void reportUsage() {
        reportEach("",
            "syntopica usage: run the following command ...", "",
            "    java -jar syntopica.jar [baseFolder] {domainFacts} ...", "",
            "where [baseFolder] locates the facts and resulting site,",
            "use '.' without quotes for the current folder", "",
            "{domainFacts} ... are name(s) some .txt file(s) that contain domain facts",
            "all such fact files are found in a folder named 'briefs' under the [baseFolder]",
            "if none is provided, 'domain-facts.txt' will be assumed");

        reportEach("",
            "note, if you've only just built the tool, use its location in the command above: ",
            "{repoBase}/syntopica/syntopica-data/target/syntopica.jar");

        reportEach("",
            "see the syntopica project site for more details and options",
            "https://github.com/nikboyd/syntopica/#syntopica");

        reportEach("",
            "Happy Modeling!", "");
    }

    static final String MissingReport = "WARNING! can't locate: '%s'";
    void reportMissing(String location) { reportEach("", format(MissingReport, location)); }

    static final String PossibleReport = "does '%s' contain '%s'?";
    void reportPossible(String location, String possible) { reportEach(format(PossibleReport, location, possible)); }

} // Main
