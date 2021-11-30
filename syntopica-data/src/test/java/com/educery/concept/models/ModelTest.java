package com.educery.concept.models;

import java.io.*;
import org.junit.*;

import com.educery.sites.Main;
import com.educery.utils.Logging;
import static com.educery.utils.Utils.*;
import static com.educery.utils.Exceptional.*;

/**
 * Generates model pages from a sample model.
 */
@Ignore
public class ModelTest implements Logging {

    static final String CodeBase = "./../..";
    static final String[] DemoBases = { "eco-depot", "software-requirements" };
    @Test public void testMain() throws Exception {
        wrap(DemoBases).forEach(demo -> runLoudly(() -> {
            File demoBase = new File(new File(CodeBase).getCanonicalPath(), demo);
            Main.main(demoBase.getPath());
        }));
    }

} // ModelTest
