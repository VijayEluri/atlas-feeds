package org.atlasapi.feeds.youview.hierarchy;

import java.util.Map;

import org.atlasapi.media.entity.Item;


public interface ContentHierarchyExpander {

    Map<String, ItemAndVersion> versionHierarchiesFor(Item item);
    Map<String, ItemBroadcastHierarchy> broadcastHierarchiesFor(Item item);
    Map<String, ItemOnDemandHierarchy> onDemandHierarchiesFor(Item item);
}