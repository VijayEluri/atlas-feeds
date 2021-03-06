package org.atlasapi.feeds.radioplayer.upload;

import org.atlasapi.feeds.radioplayer.RadioPlayerService;
import org.atlasapi.media.channel.ChannelResolver;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.persistence.content.listing.ContentLister;
import org.atlasapi.persistence.content.mongo.LastUpdatedContentFinder;
import org.atlasapi.persistence.logging.AdapterLog;
import org.atlasapi.reporting.telescope.FeedsReporterNames;

import com.metabroadcast.common.scheduling.ScheduledTask;
import com.metabroadcast.common.time.DayRangeGenerator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import static com.google.common.base.Preconditions.checkNotNull;

public class RadioPlayerUploadTaskBuilder {

    private final RadioPlayerUploadServicesSupplier uploadServicesSupplier;
    private final RadioPlayerRecordingExecutor executor;
    private AdapterLog log;
    private final ContentLister contentLister;
    private final LastUpdatedContentFinder lastUpdatedContentFinder;
    private final Publisher publisher;
    private final RadioPlayerUploadResultStore resultStore;
    private ChannelResolver channelResolver;

    public RadioPlayerUploadTaskBuilder(
            RadioPlayerUploadServicesSupplier uploadServicesSupplier,
            RadioPlayerRecordingExecutor executor,
            LastUpdatedContentFinder lastUpdatedContentFinder,
            ContentLister contentLister,
            Publisher publisher,
            RadioPlayerUploadResultStore resultStore,
            ChannelResolver channelResolver) {
        this.uploadServicesSupplier = uploadServicesSupplier;
        this.executor = executor;
        this.lastUpdatedContentFinder = lastUpdatedContentFinder;
        this.contentLister = contentLister;
        this.publisher = publisher;
        this.resultStore = checkNotNull(resultStore);
        this.channelResolver = channelResolver;
    }
    
    public RadioPlayerUploadTaskBuilder withLog(AdapterLog log) {
        this.log = log;
        return this;
    }
    
    public ScheduledTask newScheduledPiTask(Iterable<RadioPlayerService> services, DayRangeGenerator dayGenerator,
            FeedsReporterNames telescopeName) {
        return new RadioPlayerScheduledPiUploadTask(
                uploadServicesSupplier,
                executor,
                services,
                dayGenerator,
                log,
                publisher,
                channelResolver,
                telescopeName
        );
    }
    
    public Runnable newBatchPiTask(Iterable<RadioPlayerService> services, Iterable<LocalDate> days,
            FeedsReporterNames telescopeName) {
        return new RadioPlayerPiBatchUploadTask(
                uploadServicesSupplier.get(
                        new DateTime(DateTimeZone.UTC),
                        FileType.PI
                ),
                executor,
                services,
                days,
                log,
                publisher,
                channelResolver,
                telescopeName
        );
    }
    
    public ScheduledTask newScheduledOdTask(
            Iterable<RadioPlayerService> services,
            boolean fullSnapshot,
            FeedsReporterNames telescopeName) {
        return new RadioPlayerScheduledOdUpdateTask(
                uploadServicesSupplier,
                executor,
                services,
                log,
                fullSnapshot,
                lastUpdatedContentFinder,
                contentLister,
                publisher,
                resultStore,
                channelResolver,
                telescopeName
        );
    }
    
    public Runnable newBatchOdTask(
            Iterable<RadioPlayerService> services,
            LocalDate day,
            FeedsReporterNames telescopeName) {
        return new RadioPlayerOdBatchUploadTask(
                uploadServicesSupplier.get(new DateTime(DateTimeZone.UTC), FileType.OD),
                executor,
                services,
                day,
                false,
                log,
                lastUpdatedContentFinder,
                contentLister,
                publisher,
                resultStore,
                channelResolver,
                telescopeName
        );
    }
}
