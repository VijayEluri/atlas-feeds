package org.atlasapi.feeds.xmltv;

import javax.annotation.PostConstruct;

import org.atlasapi.feeds.upload.persistence.MongoFileUploadResultStore;
import org.atlasapi.feeds.upload.s3.S3FileUploader;
import org.atlasapi.feeds.utils.DescriptionWatermarker;
import org.atlasapi.feeds.utils.WatermarkModule;
import org.atlasapi.feeds.xmltv.upload.XmlTvUploadHealthProbe;
import org.atlasapi.feeds.xmltv.upload.XmlTvUploadService;
import org.atlasapi.feeds.xmltv.upload.XmlTvUploadTask;
import org.atlasapi.media.channel.ChannelResolver;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.persistence.content.KnownTypeContentResolver;
import org.atlasapi.persistence.content.ScheduleResolver;
import org.atlasapi.persistence.logging.AdapterLog;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.collect.ImmutableSet;
import com.metabroadcast.common.health.HealthProbe;
import com.metabroadcast.common.persistence.mongo.DatabasedMongo;
import com.metabroadcast.common.scheduling.RepetitionRules;
import com.metabroadcast.common.scheduling.SimpleScheduler;
import com.metabroadcast.common.security.UsernameAndPassword;
import com.metabroadcast.common.webapp.health.HealthController;

@Configuration
@Import( { WatermarkModule.class } )
public class XmlTvModule {
    
    private static final String SERVICE_NAME = "xmltv";
    
    @Autowired ScheduleResolver scheduleResolver;
    @Autowired ChannelResolver channelResolver;
    @Autowired KnownTypeContentResolver contentResolver;
    @Autowired SimpleScheduler scheduler;
    @Autowired DatabasedMongo mongo;
    @Autowired HealthController health;
    @Autowired AdapterLog log;
    @Autowired DescriptionWatermarker descriptionWatermarker;
    
    private @Value("${xmltv.upload.enabled}") String uploadEnabled;
    private @Value("${xmltv.upload.bucket}") String s3bucket;
    private @Value("${xmltv.upload.folder}") String s3folder;
    private @Value("${s3.access}") String s3access;
    private @Value("${s3.secret}") String s3secret;

    public @Bean XmlTvChannelLookup xmlTvChannelLookup() {
    	return new XmlTvChannelLookup(channelResolver);
    }
    public @Bean XmlTvController xmlTvController() {
        return new XmlTvController(xmltvFeedCompiler(), xmlTvChannelLookup().getXmlTvChannelMap(), health);
    }
    
    public @Bean XmlTvFeedCompiler xmltvFeedCompiler() {
        return new XmlTvFeedCompiler(scheduleResolver, contentResolver, Publisher.PA, descriptionWatermarker);
    }
    
    @PostConstruct
    public void scheduleUploadTask() {
        if(Boolean.valueOf(uploadEnabled)) {
            final XmlTvUploadService uploadService = new XmlTvUploadService(SERVICE_NAME, new S3FileUploader(new UsernameAndPassword(s3access, s3secret), s3bucket, s3folder));
            final MongoFileUploadResultStore resultStore = new MongoFileUploadResultStore(mongo);
            final XmlTvUploadTask uploadTask = new XmlTvUploadTask(uploadService, resultStore, xmltvFeedCompiler(), xmlTvChannelLookup().getXmlTvChannelMap(), log);
            scheduler.schedule(uploadTask.withName("XmlTv Upload"), RepetitionRules.daily(new LocalTime(04,30,00)));
            health.addProbes(ImmutableSet.<HealthProbe>of(new XmlTvUploadHealthProbe(xmlTvChannelLookup().getXmlTvChannelMap(), resultStore)));
        }
    }

    static final String FEED_PREABMLE = "\t\nAfter many happy years of service, this feed has now been decommissioned."
            + "\nMore details here: https://metabroadcast.com/blog/changes-to-atlas-access";
    
}
