package org.atlasapi.feeds.youview.lovefilm;

import org.atlasapi.feeds.tvanytime.BroadcastEventGenerator;
import org.atlasapi.feeds.youview.hierarchy.ItemBroadcastHierarchy;

import tva.metadata._2010.BroadcastEventType;

public class LoveFilmBroadcastEventGenerator implements BroadcastEventGenerator {

    public LoveFilmBroadcastEventGenerator() { }

    @Override
    public BroadcastEventType generate(ItemBroadcastHierarchy broadcast, String broadcastImi) {
        throw new UnsupportedOperationException("Broadcast Events are not supported for the LOVEFiLM publisher");
    }
}
