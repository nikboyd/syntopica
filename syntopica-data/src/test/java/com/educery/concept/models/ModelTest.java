package com.educery.concept.models;

import java.io.*;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.junit.*;

import com.educery.xml.tags.*;

/**
 * Generates model pages from a sample model.
 */
public class ModelTest {

//	private static final Log Logger = LogFactory.getLog(ModelTest.class);
	private static final String Cyan = "#add8e6";
	private static final String Grey = "#bfbfbf";
	private static final String Bluish = "#d8e5e5";

	/**
	 * Generates web pages from a model.
	 */
	@Test
	public void generatePages() {
		String pageFolder = getClass().getResource("/pages").getFile();
		String templateFolder = getClass().getResource("/templates").getFile();
		ModelSite.withTemplates(templateFolder)
			.withPages(pageFolder)
			.withModel("/sample-domain.txt")
			.generatePages();
	}
	
	/**
	 * Generates SVG sample page.
	 * @throws Exception
	 */
	@Test
	public void svgSample() throws Exception {
		String pageFolder = getClass().getResource("/pages").getFile();
		File sampleFile = new File(pageFolder, "sample.html");
		FileOutputStream stream = new FileOutputStream(sampleFile);
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		writer.write(buildPage().format());
		writer.close();
	}
	
	private Tag buildBody() {
		ModelElement expector = ModelElement.named("Expector").withColor(Cyan).at(80, 50);
		ModelElement feature = ModelElement.named("Feature").withColor(Grey).at(350, 50);
		ModelElement activity = ModelElement.named("Activity").withColor(Grey).at(80, 250);
		ModelElement business = ModelElement.named("Business").withColor(Bluish).at(350, 400);
		ModelElement value = ModelElement.named("Value").withColor(Bluish).at(180, 400);
		
		Connector uses = Connector.named("uses").between(feature, expector);
		expector.addTails(uses);
		feature.addHeads(uses);

		Connector performs = Connector.named("performs").between(activity, expector);
		activity.addHeads(performs);
		expector.addTails(performs);

		Connector improves = Connector.named("improves").between(activity, feature);
		activity.addHeads(improves);
		feature.addTails(improves);

		Connector produces = Connector.named("produces").between(value, activity);
		Connector producesFor = Connector.named("for").emptyHeads().between(business, activity);
		activity.addTails(produces, producesFor);
		business.addHeads(produces);
		value.addHeads(producesFor);

		int[] viewbox = { 10, 10, 500, 500 };
		Tag.Factory[] tags = { 
			expector, feature, activity, value, business, 
			uses, performs, improves, 
			produces, producesFor
		};
		
		Canvas canvas = Canvas.with(12, 13).with(viewbox).with(tags);
		return Tag.named("body").with(canvas.drawElement());
	}
	
	private Tag buildPage() {
		return Tag.named("html").with(buildHeader(), buildBody());
	}
	
	private Tag buildHeader() {
		return Tag.named("head").with(Tag.named("title").withContent("Sample"));
	}

} // ModelTest