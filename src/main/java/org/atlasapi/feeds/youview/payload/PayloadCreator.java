package org.atlasapi.feeds.youview.payload;

import org.atlasapi.feeds.youview.hierarchy.ItemAndVersion;
import org.atlasapi.feeds.youview.hierarchy.ItemBroadcastHierarchy;
import org.atlasapi.feeds.youview.hierarchy.ItemOnDemandHierarchy;
import org.atlasapi.feeds.youview.tasks.Payload;
import org.atlasapi.media.entity.Content;


public interface PayloadCreator {

    Payload createFrom(Content content);
    Payload createFrom(String versionCrid, ItemAndVersion versionHierarchy);
    Payload createFrom(String broadcastImi, ItemBroadcastHierarchy broadcastHierarchy);
    Payload createFrom(String onDemandImi, ItemOnDemandHierarchy onDemandHierarchy);
}
