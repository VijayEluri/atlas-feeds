package org.atlasapi.feeds.interlinking.delta;

import javax.annotation.PostConstruct;

import org.atlasapi.feeds.interlinking.C4PlaylistToInterlinkFeedAdapter;
import org.atlasapi.feeds.interlinking.outputting.InterlinkFeedOutputter;
import org.atlasapi.persistence.content.mongo.LastUpdatedContentFinder;
import org.jets3t.service.security.AWSCredentials;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.metabroadcast.common.scheduling.RepetitionRules;
import com.metabroadcast.common.scheduling.SimpleScheduler;

@Configuration
public class InterlinkingDeltaModule {

    private @Value("${s3.access}") String s3access;
    private @Value("${s3.secret}") String s3secret;
    
    private @Value("${interlinking.delta.enabled}") String enabled;

    @Autowired private LastUpdatedContentFinder executor;
    @Autowired private SimpleScheduler scheduler;

    @Bean
    public InterlinkingDeltaUpdater interlinkingDeltaUpdater() {
        return new InterlinkingDeltaUpdater(new AWSCredentials(s3access, s3secret), executor, new InterlinkFeedOutputter(), new C4PlaylistToInterlinkFeedAdapter());
    }
    
    @PostConstruct
    public void scheduleTasks() {
        
        if(Boolean.parseBoolean(enabled)) {
            SinceLastUpdatedInterlinkingDeltaUpdater sinceLastUpdatedUpdater = new SinceLastUpdatedInterlinkingDeltaUpdater(interlinkingDeltaUpdater());
            scheduler.schedule(sinceLastUpdatedUpdater.withName("Last updated interlink deltas updater"), RepetitionRules.every(Duration.standardMinutes(10)));
            
            CompleteInterlinkingDeltaUpdater completeUpdater = new CompleteInterlinkingDeltaUpdater(interlinkingDeltaUpdater(), 30);
            scheduler.schedule(completeUpdater.withName("Complete interlinking deltas updater"), RepetitionRules.NEVER);
        }
        
    }
}
