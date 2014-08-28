package org.atlasapi.feeds.sitemaps;

import org.atlasapi.media.entity.Content;
import org.joda.time.DateTime;

import com.google.common.base.Function;

public class SiteMapRef {

    public static final Function<Content, SiteMapRef> transformerForHost(final String host) {
        return new Function<Content, SiteMapRef>() {
            @Override
            public SiteMapRef apply(Content brand) {
                return SiteMapRef.sitemapRefFrom(brand, host);
            }
        };
    };
    
    public static final SiteMapRef sitemapRefFrom(Content brand, String host) {
        return new SiteMapRef(String.format("http://%s/feeds/sitemaps/sitemap.xml?brand.uri=%s", host, uriFor(brand)), brand.getLastUpdated());
    }
    
    private static final String uriFor(Content content) {
        if (content.getCurie() != null) {
            return content.getCurie();
        }
        return content.getCanonicalUri();
    }
    
	private final String url;
	private final DateTime lastModified;

	public SiteMapRef(String url, DateTime lastModified) {
		this.url = url;
		this.lastModified = lastModified;
	}
	
	public String getUrl() {
		return url;
	}

	public DateTime getLastModified() {
		return lastModified;
	}
}
