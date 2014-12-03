package org.atlasapi.feeds.youview.resolution;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.joda.time.DateTimeConstants.DECEMBER;

import java.util.Iterator;

import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.persistence.content.mongo.LastUpdatedContentFinder;
import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.metabroadcast.common.time.DateTimeZones;


public class UpdatedContentResolver implements YouViewContentResolver {

    @VisibleForTesting
    // TODO update this to point back to the genuine start of time once testing is complete
    static final DateTime START_OF_TIME = new DateTime(2014, DECEMBER, 2, 0, 0, 0, 0, DateTimeZones.UTC);
    
    private final LastUpdatedContentFinder contentFinder;
    private final Publisher publisher;
    
    public UpdatedContentResolver(LastUpdatedContentFinder contentFinder, Publisher publisher) {
        this.contentFinder = checkNotNull(contentFinder);
        this.publisher = checkNotNull(publisher);
    }

    @Override
    public Iterator<Content> updatedSince(DateTime dateTime) {
        return fetchContentUpdatedSinceDate(Optional.of(dateTime));
    }

    @Override
    public Iterator<Content> allContent() {
        return fetchContentUpdatedSinceDate(Optional.<DateTime>absent());
    }
    
    private Iterator<Content> fetchContentUpdatedSinceDate(Optional<DateTime> since) {
        DateTime start = since.isPresent() ? since.get() : START_OF_TIME;
        return contentFinder.updatedSince(publisher, start);
    }
}