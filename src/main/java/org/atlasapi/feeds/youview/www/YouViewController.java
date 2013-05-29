package org.atlasapi.feeds.youview.www;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atlasapi.application.ApplicationConfiguration;
import org.atlasapi.application.query.ApplicationConfigurationFetcher;
import org.atlasapi.feeds.tvanytime.TvAnytimeGenerator;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Identified;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.persistence.content.ContentResolver;
import org.atlasapi.persistence.content.ResolvedContent;
import org.atlasapi.persistence.content.mongo.LastUpdatedContentFinder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.base.Maybe;
import com.metabroadcast.common.media.MimeType;
import com.metabroadcast.common.time.DateTimeZones;

@Controller
public class YouViewController {

    // TODO if more publishers are required, make this a list & a parameter of the class
    private static final Publisher PUBLISHER = Publisher.LOVEFILM;
    private static final DateTime START_OF_TIME = new DateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZones.UTC);
    
    private final DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinuteSecond().withZone(DateTimeZones.UTC);
    private final TvAnytimeGenerator feedGenerator;
    private final LastUpdatedContentFinder contentFinder;
    private final ContentResolver contentResolver;
    private final ApplicationConfigurationFetcher configFetcher;
    
    public YouViewController(ApplicationConfigurationFetcher configFetcher, TvAnytimeGenerator feedGenerator, LastUpdatedContentFinder contentFinder, ContentResolver contentResolver) {
        this.configFetcher = configFetcher;
        this.feedGenerator = feedGenerator;
        this.contentFinder = contentFinder;
        this.contentResolver = contentResolver;
    }
    
    /**
     * Produces the feed for YouView
     * @param response
     * @param lastUpdated - if present, the endpoint will return a delta feed of all items 
     *                      updated since lastUpdated, otherwise it will return a full 
     *                      bootstrap feed
     * @throws IOException 
     */
    @RequestMapping("/feeds/youview/lovefilm")
    public void generateFeed(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "lastUpdated", required = false) String lastUpdated) throws IOException {
        try {
            final ApplicationConfiguration appConfig = appConfig(request);
            if (!appConfig.isEnabled(PUBLISHER)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentLength(0);
                return;
            }
            response.setContentType(MimeType.APPLICATION_ATOM_XML.toString());
            response.setStatus(HttpServletResponse.SC_OK);
            
            Optional<String> since = Optional.fromNullable(lastUpdated);
            feedGenerator.generateXml(getContentSinceDate(since), response.getOutputStream());
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentLength(0);
        }
    }
    
    @RequestMapping("/feeds/youview/lovefilm/content")
    public void generateFeedForItem(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "uri", required = true) String uri) throws IOException {
        try {
            final ApplicationConfiguration appConfig = appConfig(request);
            if (!appConfig.isEnabled(PUBLISHER)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentLength(0);
                return;
            }
            response.setContentType(MimeType.APPLICATION_ATOM_XML.toString());
            response.setStatus(HttpServletResponse.SC_OK);
            
            ResolvedContent resolvedContent = contentResolver.findByCanonicalUris(ImmutableList.of(uri));
            Collection<Identified> resolved = resolvedContent.asResolvedMap().values();
            feedGenerator.generateXml(ImmutableList.of((Content) Iterables.getOnlyElement(resolved)), response.getOutputStream());
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentLength(0);
        }
    }
    
    private Iterable<Content> getContentSinceDate(Optional<String> since) {
        
        DateTime start = since.isPresent() ? fmt.parseDateTime(since.get()) : START_OF_TIME;
        return ImmutableList.copyOf(contentFinder.updatedSince(PUBLISHER, start));
    }
    
    private ApplicationConfiguration appConfig(HttpServletRequest request) {
        Maybe<ApplicationConfiguration> config = configFetcher.configurationFor(request);
        return config.hasValue() ? config.requireValue() : ApplicationConfiguration.defaultConfiguration();
    }
}
