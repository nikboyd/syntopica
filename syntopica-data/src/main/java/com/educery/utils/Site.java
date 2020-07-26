package com.educery.utils;

import java.io.*;
import java.util.*;

/**
 * Defines protocols for sites.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public interface Site extends Logging {

    public static interface Source { Site getSite(); void register(Site aSite); }
    public static Source SiteSource = new Source() {
        @Override public Site getSite() { return this.aSite; }
        @Override public void register(Site aSite) { this.aSite = aSite; }
        Site aSite;
    };

    static Site getSite() { return SiteSource.getSite(); }
    default String linkBase() { return Empty; }
    default String imageBase() { return Empty; }
    default File domainFolder() { return null; }

    default Map<String, String> topicLinks() { return new HashMap(); }
    default Map<String, String> pluralLinks() { return new HashMap(); }

    static final String MarkDown = ".md";
    static final String HyperText = ".html";
    default String pageType() { return MarkDown; }
    default boolean usesMarkdown() { return pageType().equals(MarkDown); }

} // Site
