package org.atlasapi.feeds.sitemaps;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.content.criteria.operator.Operators;
import org.atlasapi.media.TransportType;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Identified;
import org.atlasapi.media.entity.Item;
import org.atlasapi.persistence.content.query.KnownTypeQueryExecutor;
import org.atlasapi.query.content.parser.ApplicationConfigurationIncludingQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.webapp.http.CacheHeaderWriter;

@Controller
public class SiteMapController {

    private static final String HOST_PARAM = "host";

    private final KnownTypeQueryExecutor queryExecutor;
    private final SiteMapOutputter outputter = new SiteMapOutputter();
    private final SiteMapIndexOutputter indexOutputter = new SiteMapIndexOutputter();
    private final String defaultHost;
    private final ApplicationConfigurationIncludingQueryBuilder queryBuilder;
    private final CacheHeaderWriter cacheHeaderWriter = CacheHeaderWriter.neverCache();

    public SiteMapController(KnownTypeQueryExecutor queryExecutor, ApplicationConfigurationIncludingQueryBuilder queryBuilder, String defaultHost) {
        this.queryExecutor = queryExecutor;
        this.queryBuilder = queryBuilder;
        this.defaultHost = defaultHost;
    }

    @RequestMapping("/feeds/sitemaps/sitemap.xml")
    public String siteMapForBrand(HttpServletRequest request, HttpServletResponse response, @RequestParam("brand.uri") String brandUri) throws IOException {

        SitemapHackHttpRequest hackedRequest = new SitemapHackHttpRequest(request, brandUri);

        ContentQuery query = queryBuilder.build(hackedRequest);
        List<Identified> brands = queryExecutor.executeUriQuery(uris(hackedRequest, query), query);
        response.setStatus(HttpServletResponse.SC_OK);
        cacheHeaderWriter.writeHeaders(hackedRequest, response);
        outputter.output(Iterables.filter(brands, Item.class), response.getOutputStream());
        return null;
    }

    @RequestMapping("/feeds/sitemaps/index.xml")
    public String siteMapFofPublisher(@RequestParam(value = HOST_PARAM, required = false) final String host, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ContentQuery query = queryBuilder.build(request);

        ContentQuery requestQuery = new ContentQuery(Iterables.concat(query.operands(), ImmutableList.<AtomicQuery> of(Attributes.LOCATION_TRANSPORT_TYPE.createQuery(Operators.EQUALS, ImmutableList
                .of(TransportType.LINK))))).copyWithApplicationConfiguration(query.getConfiguration());

        List<? extends Content> brands = queryExecutor.discover(requestQuery);

        Iterable<SiteMapRef> refs = Iterables.transform(brands, new Function<Content, SiteMapRef>() {

            @Override
            public SiteMapRef apply(Content brand) {
                return new SiteMapRef("http://" + hostOrDefault(host) + "/feeds/sitemaps/sitemap.xml?uri=" + brand.getCurie(), brand.getLastUpdated());
            }

        });
        response.setStatus(HttpServletResponse.SC_OK);
        cacheHeaderWriter.writeHeaders(request, response);
        indexOutputter.output(refs, response.getOutputStream());
        return null;
    }

    private static List<String> uris(HttpServletRequest request, ContentQuery filter) {
        if (!Selection.ALL.equals(filter.getSelection())) {
            throw new IllegalArgumentException("Cannot specifiy a limit or offset here");
        }
        String commaSeperatedUris = request.getParameter("uri");
        if (commaSeperatedUris == null) {
            throw new IllegalArgumentException("No uris specified");
        }
        List<String> uris = ImmutableList.copyOf(URI_SPLITTER.split(commaSeperatedUris));
        if (Iterables.isEmpty(uris)) {
            throw new IllegalArgumentException("No uris specified");
        }
        return uris;
    }

    private static final Splitter URI_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    private String hostOrDefault(String host) {
        return host == null ? defaultHost : host;
    }

    private static class SitemapHackHttpRequest extends HttpServletRequestWrapper {
        private final Map<String, String[]> params;

        @SuppressWarnings("unchecked")
        public SitemapHackHttpRequest(HttpServletRequest request, String brandUri) {
            super(request);
            params = Maps.newHashMap(request.getParameterMap());
            params.put("uri", new String[] { brandUri });
            params.remove("brand.uri");
        }

        public String getParameter(String name) {
            String[] values = this.params.get(name);
            if (values != null && values.length > 0) {
                return values[0];
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            return ImmutableMap.copyOf(this.params);
        }

        @SuppressWarnings("unchecked")
        public Enumeration getParameterNames() {
            return Collections.enumeration(this.params.keySet());
        }

        public String[] getParameterValues(String name) {
            return this.params.get(name);
        }
    }
}
