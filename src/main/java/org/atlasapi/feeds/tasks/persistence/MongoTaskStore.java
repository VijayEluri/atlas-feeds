package org.atlasapi.feeds.tasks.persistence;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.ACTION_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.CONTENT_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.CREATED_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.DESTINATION_TYPE_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.ELEMENT_ID_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.ELEMENT_TYPE_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.LAST_ERROR_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.PUBLISHER_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.REMOTE_ID_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.STATUS_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.UPLOAD_TIME_KEY;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.fromDBObject;
import static org.atlasapi.feeds.tasks.persistence.TaskTranslator.toDBObject;

import org.atlasapi.feeds.tasks.Payload;
import org.atlasapi.feeds.tasks.Response;
import org.atlasapi.feeds.tasks.Status;
import org.atlasapi.feeds.tasks.Task;
import org.atlasapi.feeds.tasks.TaskQuery;
import org.atlasapi.feeds.tasks.Destination.DestinationType;
import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.metabroadcast.common.persistence.mongo.DatabasedMongo;
import com.metabroadcast.common.persistence.mongo.MongoQueryBuilder;
import com.metabroadcast.common.persistence.mongo.MongoSortBuilder;
import com.metabroadcast.common.persistence.mongo.MongoUpdateBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class MongoTaskStore implements TaskStore {
    
    private static final String COLLECTION_NAME = "youviewTasks";
    
    private final DBCollection collection;
    
    public MongoTaskStore(DatabasedMongo mongo) {
        this.collection = checkNotNull(mongo).collection(COLLECTION_NAME);
    }

    private DBCursor getOrderedCursor(DBObject query) {
        return collection.find(query).sort(new MongoSortBuilder().descending(UPLOAD_TIME_KEY).build());
    }

    @Override
    public Task save(Task task) {
        collection.save(toDBObject(task));
        return task;
    }

    @Override
    public void updateWithStatus(Long taskId, Status status) {
        DBObject idQuery = new MongoQueryBuilder()
                .idEquals(taskId)
                .build();
        DBObject updateStatus = new MongoUpdateBuilder()
                .setField(STATUS_KEY, status.name())
                .build();
        
        collection.update(idQuery, updateStatus, false, false);
    }
    
    @Override
    public void updateWithLastError(Long taskId, String lastError) {
        DBObject idQuery = new MongoQueryBuilder()
                                .idEquals(taskId)
                                .build();
        
        DBObject updateLastError = new MongoUpdateBuilder()
                                      .setField(LAST_ERROR_KEY, lastError)
                                      .build();
        collection.update(idQuery, updateLastError, false, false);
    }

    @Override
    public void updateWithRemoteId(Long taskId, Status status, String remoteId, DateTime uploadTime) {
        DBObject idQuery = new MongoQueryBuilder()
                .idEquals(taskId)
                .build();
        DBObject updateStatus = new MongoUpdateBuilder()
                .setField(TaskTranslator.STATUS_KEY, status.name())
                .setField(TaskTranslator.REMOTE_ID_KEY, remoteId)
                .setField(TaskTranslator.UPLOAD_TIME_KEY, uploadTime)
                .build();
        
        collection.update(idQuery, updateStatus, false, false);
    }

    @Override
    public void updateWithResponse(Long taskId, Response response) {
        DBObject idQuery = new MongoQueryBuilder()
                .idEquals(taskId)
                .build();
        DBObject updateStatus = new MongoUpdateBuilder()
                .push(TaskTranslator.REMOTE_STATUSES_KEY, ResponseTranslator.toDBObject(response))
                .setField(STATUS_KEY, response.status().name())
                .build();
        
        collection.update(idQuery, updateStatus, false, false);
    }

    @Override
    public void updateWithPayload(Long taskId, Payload payload) {
        DBObject idQuery = new MongoQueryBuilder()
                .idEquals(taskId)
                .build();
        DBObject updateStatus = new MongoUpdateBuilder()
                .setField(TaskTranslator.PAYLOAD_KEY, PayloadTranslator.toDBObject(payload))
                .build();
        
        collection.update(idQuery, updateStatus, false, false);
    }

    @Override
    public Optional<Task> taskFor(Long taskId) {
        DBObject idQuery = new MongoQueryBuilder()
                .idEquals(taskId)
                .build();
        return Optional.fromNullable(fromDBObject(collection.findOne(idQuery)));
    }

    @Override
    public Iterable<Task> allTasks(DestinationType type, Status status) {
        MongoQueryBuilder mongoQuery = new MongoQueryBuilder()
                .fieldEquals(DESTINATION_TYPE_KEY, type.name())
                .fieldEquals(STATUS_KEY, status.name());
        
        DBCursor cursor = getOrderedCursor(mongoQuery.build());
        
        return FluentIterable.from(cursor)
                .transform(TaskTranslator.fromDBObjects())
                .filter(Predicates.notNull());
    }

    @Override
    public Iterable<Task> allTasks(TaskQuery query) {
        MongoQueryBuilder mongoQuery = new MongoQueryBuilder();
        
        mongoQuery.fieldEquals(PUBLISHER_KEY, query.publisher().key())
                .fieldEquals(DESTINATION_TYPE_KEY, query.destinationType().name());
        
        if (query.contentUri().isPresent()) {
            mongoQuery.regexMatch(CONTENT_KEY, transformToRegexPattern(query.contentUri().get()));
        }
        if (query.remoteId().isPresent()) {
            mongoQuery.regexMatch(REMOTE_ID_KEY, transformToRegexPattern(query.remoteId().get()));
        }
        if (query.status().isPresent()) {
            mongoQuery.fieldEquals(STATUS_KEY, query.status().get().name());
        }
        if (query.action().isPresent()) {
            mongoQuery.fieldEquals(ACTION_KEY, query.action().get().name());
        }
        if (query.elementType().isPresent()) {
            mongoQuery.fieldEquals(ELEMENT_TYPE_KEY, query.elementType().get().name());
        }
        if (query.elementId().isPresent()) {
            mongoQuery.fieldEquals(ELEMENT_ID_KEY, transformToRegexPattern(query.elementId().get()));
        }
        
        DBCursor cursor = getOrderedCursor(mongoQuery.build())
                .skip(query.selection().getOffset())
                .limit(query.selection().getLimit());
        
        return FluentIterable.from(cursor)
                .transform(TaskTranslator.fromDBObjects())
                .filter(Predicates.notNull());
    }

    private String transformToRegexPattern(String input) {
        return input.replace(".", "\\.");
    }

    @Override
    public void removeBefore(DateTime removalDate) {
        DBObject mongoQuery = new MongoQueryBuilder()
            .fieldBefore(CREATED_KEY, removalDate)
            .build();
        
        collection.remove(mongoQuery);
    }
}
