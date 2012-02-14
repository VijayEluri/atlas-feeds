package org.atlasapi.query.content.parser;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.atlasapi.application.Application;
import org.atlasapi.application.ApplicationConfiguration;
import org.atlasapi.application.persistence.ApplicationReader;
import org.atlasapi.application.query.ApiKeyConfigurationFetcher;
import org.atlasapi.application.query.ApplicationConfigurationFetcher;
import org.atlasapi.content.criteria.ContentQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.metabroadcast.common.servlet.StubHttpServletRequest;

public class ApplicationConfigurationIncludingQueryBuilderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testBuild() {
		final String testApiKey = "testKey";
		final Application testApp = new Application("testSlug");
		testApp.setConfiguration(ApplicationConfiguration.DEFAULT_CONFIGURATION);
		
		Mockery context = new Mockery();
		final ApplicationReader reader = context.mock(ApplicationReader.class);
		
		
		context.checking(new Expectations(){{
			allowing(reader).applications();
			oneOf(reader).applicationForKey(testApiKey);
			will(returnValue(testApp));
		}});
		
		ApplicationConfigurationFetcher configFetcher = new ApiKeyConfigurationFetcher(reader);
		ApplicationConfigurationIncludingQueryBuilder builder = new ApplicationConfigurationIncludingQueryBuilder(new QueryStringBackedQueryBuilder(), configFetcher) ;

		HttpServletRequest request = new StubHttpServletRequest().withParam("tag", "East").withParam("apiKey", testApiKey);
		ContentQuery query = builder.build(request);
		
		assertEquals(testApp.getConfiguration(), query.getConfiguration());		
	}

}
