package org.atlasapi.feeds.youview.unbox;

import java.util.regex.Pattern;

import org.atlasapi.feeds.MbstCridGenerator;
import org.atlasapi.feeds.youview.ids.IdGenerator;
import org.atlasapi.media.channel.Channel;
import org.atlasapi.media.entity.Broadcast;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.Encoding;
import org.atlasapi.media.entity.Item;
import org.atlasapi.media.entity.Location;
import org.atlasapi.media.entity.Version;

public class UnboxIdGenerator implements IdGenerator {

    private static final String AMAZON_IMI_PREFIX = "imi:amazon.com/";
    public static final String VERSION_SUFFIX = "_version";
    
    @Override
    public String generateVersionCrid(Item item, Version version) {
        return baseCridFrom(item) + VERSION_SUFFIX;
    }

    @Override
    public String generateContentCrid(Content content) {
        return baseCridFrom(content);
    }
    
    @Override
    public String generateOnDemandImi(Item item, Version version, Encoding encoding, Location location) {
        return AMAZON_IMI_PREFIX + getAsin(item);
    }
    
    @Override
    public String generateBroadcastImi(String serviceId, Broadcast broadcast) {
        throw new UnsupportedOperationException("Broadcasts are not supported for the Amazon Unbox publisher");
    }

    @Override
    public String generateChannelCrid(Channel channel) {
        throw new UnsupportedOperationException("Channels are not supported for the Amazon Unbox publisher");
    }

    public static Pattern getVersionCridPattern(){
        return Pattern.compile(MbstCridGenerator.getContentCrid("stage","") + "[A-Za-z0-9]*" + UnboxIdGenerator.VERSION_SUFFIX);
    }

    private static String baseCridFrom(Content content) {

        return MbstCridGenerator.getContentCrid("stage",content); //todo:hardcoded enviroment.
        //return "crid://amazon.com/exec/obidos/ASIN/" + getAsin(content); old way of generating crids
    }

    private static String getAsin(Content content) {
        String[] splinters = content.getCanonicalUri().split("/");
        return splinters[splinters.length-1];
    }
}
