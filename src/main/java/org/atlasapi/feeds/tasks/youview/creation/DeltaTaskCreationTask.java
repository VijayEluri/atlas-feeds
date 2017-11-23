package org.atlasapi.feeds.tasks.youview.creation;

import java.util.Iterator;
import java.util.List;

import org.atlasapi.equiv.OutputContentMerger;
import org.atlasapi.feeds.tasks.Action;
import org.atlasapi.feeds.tasks.persistence.TaskStore;
import org.atlasapi.feeds.tasks.youview.processing.UpdateTask;
import org.atlasapi.feeds.youview.hierarchy.ContentHierarchyExpander;
import org.atlasapi.feeds.youview.ids.IdGenerator;
import org.atlasapi.feeds.youview.payload.PayloadCreator;
import org.atlasapi.feeds.youview.persistence.YouViewLastUpdatedStore;
import org.atlasapi.feeds.youview.persistence.YouViewPayloadHashStore;
import org.atlasapi.feeds.youview.resolution.YouViewContentResolver;
import org.atlasapi.media.channel.ChannelResolver;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.SimilarContentRef;
import org.atlasapi.persistence.content.ResolvedContent;

import com.metabroadcast.common.stream.MoreCollectors;
import com.metabroadcast.representative.api.RepresentativeIdResponse;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.metabroadcast.representative.util.Utils.decode;

public class DeltaTaskCreationTask extends TaskCreationTask {

    private static final Logger log = LoggerFactory.getLogger(TaskCreationTask.class);

    public static final Duration UPDATE_WINDOW_GRACE_PERIOD = Duration.standardHours(2);

    private static final Ordering<Content> HIERARCHICAL_ORDER = new HierarchicalOrdering();

    private final YouViewContentResolver contentResolver;
    private final ChannelResolver channelResolver;
    private final UpdateTask updateTask;

    public DeltaTaskCreationTask(
            YouViewLastUpdatedStore lastUpdatedStore,
            Publisher publisher,
            ContentHierarchyExpander hierarchyExpander,
            IdGenerator idGenerator,
            TaskStore taskStore,
            TaskCreator taskCreator,
            PayloadCreator payloadCreator,
            UpdateTask updateTask,
            YouViewContentResolver contentResolver,
            YouViewPayloadHashStore payloadHashStore,
            ChannelResolver channelResolver
    ) {
        super(
                lastUpdatedStore,
                publisher,
                hierarchyExpander,
                idGenerator,
                taskStore,
                taskCreator,
                payloadCreator,
                payloadHashStore
        );
        this.channelResolver = checkNotNull(channelResolver);
        this.contentResolver = checkNotNull(contentResolver);
        this.updateTask = checkNotNull(updateTask);
    }

    @Override
    protected void runTask() {
        Optional<DateTime> lastUpdated = getLastUpdatedTime();
        if (!lastUpdated.isPresent()) {
            throw new IllegalStateException("The bootstrap for "+ getPublisher()
                                            + " has not successfully run. Please run the "
                                            + "bootstrap upload and ensure that it succeeds before "
                                            + "running the delta upload.");
        }
        
        Optional<DateTime> startOfTask = Optional.of(new DateTime());
        log.info("Started a new delta run for {}", getPublisher());

        Iterator<Content> updatedContent = contentResolver.updatedSince(
                lastUpdated.get().minus(UPDATE_WINDOW_GRACE_PERIOD)
        );

        YouViewContentProcessor uploadProcessor = contentProcessor(lastUpdated.get(), Action.UPDATE);
        YouViewContentProcessor deletionProcessor = contentProcessor(lastUpdated.get(), Action.DELETE);

        List<Content> deleted;
        if(getPublisher().equals(Publisher.BBC_NITRO)){
            deleted = uploadFromBBC(updatedContent, uploadProcessor);
        }
        else if(getPublisher().equals(Publisher.AMAZON_UNBOX)){
            deleted = uploadFromAmazon(updatedContent, uploadProcessor);
        } else {
            throw new IllegalStateException("Uploading to "+getPublisher()+" to YV is not supported.");
        }

        List<Content> orderedForDeletion = orderContentForDeletion(deleted);
        int deletingContent= 0;
        for (Content toBeDeleted : orderedForDeletion) {
            log.info("@@@ processing content " + toBeDeleted.getId() + "to be DELETED no:" + ++deletingContent + getPublisher());
            deletionProcessor.process(toBeDeleted);
            reportStatus("Deletes: " + deletionProcessor.getResult());
        }

        log.info("@@@" + getPublisher() + " setting last update time to " + startOfTask.get());
        setLastUpdatedTime(startOfTask.get());

        log.info("@@@" + getPublisher() + " Starting the upload task.");
        reportStatus("Uploading tasks to YouView");
        updateTask.run();

        log.info("@@@" + getPublisher() + " Done");
        reportStatus("Done uploading tasks to YouView");
    }

    private List<Content> uploadFromAmazon(Iterator<Content> content,
            YouViewContentProcessor uploadProcessor) {

        OutputContentMerger contentMerger = new OutputContentMerger();

        List<Content> deleted = Lists.newArrayList();
        while (content.hasNext()) {
            Content updated = content.next();
            if (updated.isActivelyPublished()) {
                //Run equivalence on this piece of content.
                List<SimilarContentRef> similarContent = updated.getSimilarContent();

                ImmutableList<String> equivUris = similarContent.stream()
                        .map(SimilarContentRef::getUri)
                        .collect(MoreCollectors.toImmutableList());

                ResolvedContent resolvedEquiv = contentResolver.findByUris(equivUris);
                ImmutableList<Content> equivContent = resolvedEquiv.getAllResolvedResults()
                        .stream()
                        .filter(input -> input instanceof Content)
                        .map(input -> (Content) input)
                        .collect(MoreCollectors.toImmutableList());

                List<Content> mergedContent = contentMerger.merge(getApplication(), equivContent);

                //Update the existing content ID with a representative ID
                RepresentativeIdResponse repIdResponse;
                try {
                    repIdResponse = getRepIdClient().getRepId(updated.getId());
                } catch (IllegalArgumentException e) {
                    log.error("Cannot process item.", e);
                    continue;
                }

                log.info( "{} swapped {} for repId {}",
                        (updated.getId()) == (decode(repIdResponse.getRepresentative().getId())),
                        updated.getId(),
                        decode(repIdResponse.getRepresentative().getId())
                );
                updated.setId(decode(repIdResponse.getRepresentative().getId()));

                uploadProcessor.process(updated);
                reportStatus("Uploads: " + uploadProcessor.getResult());
            } else {
                deleted.add(updated);
            }
        }
        return deleted;
    }

    private List<Content> uploadFromBBC(
            Iterator<Content> updatedContent,
            YouViewContentProcessor uploadProcessor) {

        List<Content> deleted = Lists.newArrayList();
        while (updatedContent.hasNext()) {
            Content updated = updatedContent.next();
            if (updated.isActivelyPublished()) {
                uploadProcessor.process(updated);
                reportStatus("Uploads: " + uploadProcessor.getResult());
            } else {
                deleted.add(updated);
            }
        }
        return deleted;
    }
    
    private static List<Content> orderContentForDeletion(Iterable<Content> toBeDeleted) {
        return HIERARCHICAL_ORDER.immutableSortedCopy(toBeDeleted);
    }
}
