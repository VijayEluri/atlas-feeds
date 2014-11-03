package org.atlasapi.feeds.tvanytime;

import org.atlasapi.media.entity.Item;

import tva.metadata._2010.BroadcastEventType;


public interface BroadcastEventGenerator {

    Iterable<BroadcastEventType> generate(Item item);
}
