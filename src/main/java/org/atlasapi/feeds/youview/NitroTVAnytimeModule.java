package org.atlasapi.feeds.youview;

import org.atlasapi.feeds.youview.hierarchy.BroadcastHierarchyExpander;
import org.atlasapi.feeds.youview.hierarchy.ContentHierarchyExpander;
import org.atlasapi.feeds.youview.hierarchy.OnDemandHierarchyExpander;
import org.atlasapi.feeds.youview.hierarchy.VersionHierarchyExpander;
import org.atlasapi.feeds.youview.nitro.BbcServiceIdResolver;
import org.atlasapi.feeds.youview.nitro.ChannelResolvingBbcServiceIdResolver;
import org.atlasapi.feeds.youview.nitro.NitroBroadcastEventGenerator;
import org.atlasapi.feeds.youview.nitro.NitroBroadcastServiceMapping;
import org.atlasapi.feeds.youview.nitro.NitroContentHierarchyExpander;
import org.atlasapi.feeds.youview.nitro.NitroGenreMapping;
import org.atlasapi.feeds.youview.nitro.NitroGroupInformationGenerator;
import org.atlasapi.feeds.youview.nitro.NitroIdGenerator;
import org.atlasapi.feeds.youview.nitro.NitroOnDemandLocationGenerator;
import org.atlasapi.feeds.youview.nitro.NitroProgramInformationGenerator;
import org.atlasapi.feeds.youview.nitro.NitroTvAnytimeElementCreator;
import org.atlasapi.media.channel.ChannelResolver;
import org.atlasapi.persistence.content.ContentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.hash.Hashing;


@Configuration
public class NitroTVAnytimeModule {

    private @Autowired ContentResolver contentResolver;
    private @Autowired ChannelResolver channelResolver;
    
    @Bean
    public NitroTvAnytimeElementCreator nitroElementCreator() {
        return new NitroTvAnytimeElementCreator(
                new NitroProgramInformationGenerator(nitroIdGenerator()), 
                new NitroGroupInformationGenerator(nitroIdGenerator(), nitroGenreMapping(), bbcServiceIdResolver()), 
                new NitroOnDemandLocationGenerator(nitroIdGenerator()), 
                new NitroBroadcastEventGenerator(nitroIdGenerator()),
                contentHierarchy()
        );
    }

    @Bean
    public NitroBroadcastServiceMapping nitroServiceMapping() {
        return new NitroBroadcastServiceMapping("nitro_service_mapping.csv");
    }
    
    @Bean
    public NitroIdGenerator nitroIdGenerator() {
        return new NitroIdGenerator(Hashing.md5());
    }
    
    @Bean
    public NitroGenreMapping nitroGenreMapping() {
        return new NitroGenreMapping();
    }

    @Bean
    public BbcServiceIdResolver bbcServiceIdResolver() {
        return new ChannelResolvingBbcServiceIdResolver(channelResolver);
    }

    private ContentHierarchyExtractor contentHierarchy() {
        return new ContentResolvingContentHierarchyExtractor(contentResolver);
    }
    
    @Bean
    public ContentHierarchyExpander contentHierarchyExpander() {
        return new NitroContentHierarchyExpander(versionHierarchyExpander(), broadcastHierarchyExpander(), onDemandHierarchyExpander());
    }
    
    private VersionHierarchyExpander versionHierarchyExpander() {
        return new VersionHierarchyExpander(nitroIdGenerator());
    }
    
    @Bean
    public OnDemandHierarchyExpander onDemandHierarchyExpander() {
        return new OnDemandHierarchyExpander(nitroIdGenerator());
    }

    @Bean
    public BroadcastHierarchyExpander broadcastHierarchyExpander() {
        return new BroadcastHierarchyExpander(nitroIdGenerator(), nitroServiceMapping(), bbcServiceIdResolver());
    }
}