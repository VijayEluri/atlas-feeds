package org.atlasapi.feeds;

import org.atlasapi.feeds.interlinking.InterlinkingModule;
import org.atlasapi.feeds.lakeview.LakeviewModule;
import org.atlasapi.feeds.sitemaps.SiteMapModule;
import org.atlasapi.feeds.xmltv.XmlTvModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({InterlinkingModule.class, SiteMapModule.class, LakeviewModule.class, XmlTvModule.class})
public class AtlasFeedsModule {

}
