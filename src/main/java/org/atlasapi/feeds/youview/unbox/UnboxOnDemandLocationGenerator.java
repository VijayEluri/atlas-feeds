package org.atlasapi.feeds.youview.unbox;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.atlasapi.feeds.youview.YouViewGeneratorUtils.getAsin;

import java.math.BigInteger;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.atlasapi.feeds.tvanytime.OnDemandLocationGenerator;
import org.atlasapi.feeds.tvanytime.TvAnytimeElementFactory;
import org.atlasapi.feeds.youview.ids.IdGenerator;
import org.atlasapi.media.entity.Encoding;
import org.atlasapi.media.entity.Item;
import org.atlasapi.media.entity.Location;
import org.atlasapi.media.entity.Policy;
import org.atlasapi.media.entity.Version;

import tva.metadata._2010.AVAttributesType;
import tva.metadata._2010.AspectRatioType;
import tva.metadata._2010.AudioAttributesType;
import tva.metadata._2010.BitRateType;
import tva.metadata._2010.CRIDRefType;
import tva.metadata._2010.ControlledTermType;
import tva.metadata._2010.FlagType;
import tva.metadata._2010.GenreType;
import tva.metadata._2010.InstanceDescriptionType;
import tva.metadata._2010.OnDemandProgramType;
import tva.metadata._2010.VideoAttributesType;
import tva.mpeg7._2008.UniqueIDType;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.youview.refdata.schemas._2011_07_06.ExtendedOnDemandProgramType;

public class UnboxOnDemandLocationGenerator implements OnDemandLocationGenerator {

    private static final String UNBOX_ONDEMAND_SERVICE_ID = "http://unbox.amazon.co.uk/OnDemand";
    private static final String UNBOX_DEEP_LINKING_ID = "deep_linking_id.unbox.amazon.co.uk";
    
    private static final String YOUVIEW_MIX_TYPE = "urn:mpeg:mpeg7:cs:AudioPresentationCS:2001:3";
    private static final String YOUVIEW_GENRE_SUBSCRIPTION_REQUIRED = "http://refdata.youview.com/mpeg7cs/YouViewEntitlementTypeCS/2010-11-11#subscription";
    private static final String YOUVIEW_GENRE_MEDIA_AVAILABLE = "http://refdata.youview.com/mpeg7cs/YouViewMediaAvailabilityCS/2010-09-29#media_available";
    private static final String GENRE_TYPE_OTHER = "other";

    private final IdGenerator idGenerator;
    
    public UnboxOnDemandLocationGenerator(IdGenerator idGenerator) {
        this.idGenerator = checkNotNull(idGenerator);
    }
    
    @Override
    public Iterable<OnDemandProgramType> generate(Item item) {
        return FluentIterable.from(item.getVersions())
                .transformAndConcat(toOnDemandProgramType(item));
    }
    
    private Function<Version, Iterable<OnDemandProgramType>> toOnDemandProgramType(final Item item) {
        return new Function<Version, Iterable<OnDemandProgramType>>() {
            @Override
            public Iterable<OnDemandProgramType> apply(Version input) {
                return toOnDemandProgramTypes(item, input, input.getManifestedAs());
            }
        };
    }
    
    private Iterable<OnDemandProgramType> toOnDemandProgramTypes(final Item item, final Version version, 
            Iterable<Encoding> encodings) {
        return FluentIterable.from(encodings)
                .transformAndConcat(toOnDemandProgramTypes(item, version));
    }
    
    private Function<Encoding, Iterable<OnDemandProgramType>> toOnDemandProgramTypes(final Item item, final Version version) {
        return new Function<Encoding, Iterable<OnDemandProgramType>>() {
            @Override
            public Iterable<OnDemandProgramType> apply(Encoding input) {
                return toOnDemandProgramTypes(item, version, input, input.getAvailableAt());
            }
        };
    }
    
    private Iterable<OnDemandProgramType> toOnDemandProgramTypes(final Item item, final Version version, 
            final Encoding encoding, Iterable<Location> locations) {
        return Iterables.transform(locations, new Function<Location, OnDemandProgramType>() {
            @Override
            public OnDemandProgramType apply(Location input) {
                return toOnDemandProgramType(item, version, encoding, input);
            }
        });
    }

    private OnDemandProgramType toOnDemandProgramType(Item item, Version version, Encoding encoding, Location location) {
        ExtendedOnDemandProgramType onDemand = new ExtendedOnDemandProgramType();
        
        onDemand.setServiceIDRef(UNBOX_ONDEMAND_SERVICE_ID);
        onDemand.setProgram(generateProgram(item, version));
        onDemand.setInstanceMetadataId(idGenerator.generateOnDemandImi(item, version, encoding, location));
        onDemand.setInstanceDescription(generateInstanceDescription(item, encoding));
        onDemand.setPublishedDuration(generatePublishedDuration(version));
        onDemand.setStartOfAvailability(generateAvailabilityStart(location));
        onDemand.setEndOfAvailability(generateAvailabilityEnd(location));
        onDemand.setFree(generateFree());

        return onDemand;
    }
    
    // hardcoded
    private FlagType generateFree() {
        FlagType free = new FlagType();
        free.setValue(false);
        return free;
    }

    private CRIDRefType generateProgram(Item item, Version version) {
        CRIDRefType program = new CRIDRefType();
        program.setCrid(idGenerator.generateVersionCrid(item, version));
        return program;
    }

    private InstanceDescriptionType generateInstanceDescription(Item item, Encoding encoding) {
        InstanceDescriptionType instanceDescription = new InstanceDescriptionType();
        
        instanceDescription.getGenre().addAll(generateGenres());
        instanceDescription.setAVAttributes(generateAvAttributes(encoding));
        instanceDescription.getOtherIdentifier().add(generateOtherId(item));
        
        return instanceDescription;
    }

    private AVAttributesType generateAvAttributes(Encoding encoding) {
        AVAttributesType attributes = new AVAttributesType();

        attributes.getAudioAttributes().add(generateAudioAttributes());
        attributes.setVideoAttributes(generateVideoAttributes(encoding));
        Optional<BitRateType> bitRate = generateBitRate(encoding);
        if (bitRate.isPresent()) {
            attributes.setBitRate(bitRate.get());
        }
        
        return attributes;
    }

    private AudioAttributesType generateAudioAttributes() {
        AudioAttributesType attributes = new AudioAttributesType();
        ControlledTermType mixType = new ControlledTermType();
        mixType.setHref(YOUVIEW_MIX_TYPE);
        attributes.setMixType(mixType);
        return attributes;
    }

    private VideoAttributesType generateVideoAttributes(Encoding encoding) {
        VideoAttributesType attributes = new VideoAttributesType();

        attributes.setHorizontalSize(encoding.getVideoHorizontalSize());
        attributes.setVerticalSize(encoding.getVideoVerticalSize());
        if (encoding.getVideoAspectRatio() != null) {
            AspectRatioType aspectRatio = new AspectRatioType();
            aspectRatio.setValue(encoding.getVideoAspectRatio());
            attributes.getAspectRatio().add(aspectRatio);
        }

        return attributes;
    }

    private Optional<BitRateType> generateBitRate(Encoding encoding) {
        Integer bitRate = encoding.getBitRate();
        if (bitRate == null) {
            return Optional.absent();
        }
        BitRateType bitRateType = new BitRateType();
        bitRateType.setVariable(false);
        bitRateType.setValue(BigInteger.valueOf(bitRate));
        return Optional.of(bitRateType);
    }

    private UniqueIDType generateOtherId(Item item) {
        UniqueIDType id = new UniqueIDType();
        id.setAuthority(UNBOX_DEEP_LINKING_ID);
        id.setValue(getAsin(item));
        return id;
    }

    private List<GenreType> generateGenres() {
        GenreType mediaAvailable = new GenreType();
        mediaAvailable.setType(GENRE_TYPE_OTHER);
        mediaAvailable.setHref(YOUVIEW_GENRE_MEDIA_AVAILABLE);
        
        GenreType subRequired = new GenreType();
        subRequired.setType(GENRE_TYPE_OTHER);
        subRequired.setHref(YOUVIEW_GENRE_SUBSCRIPTION_REQUIRED);
        
        return ImmutableList.of(mediaAvailable, subRequired);
    }

    private Duration generatePublishedDuration(Version version) {
        Integer durationInSecs = version.getDuration();
        if (durationInSecs != null) {
            return TvAnytimeElementFactory.durationFrom(org.joda.time.Duration.standardSeconds(durationInSecs));
        } 
        return null;
    }

    private XMLGregorianCalendar generateAvailabilityStart(Location location) {
        Policy policy = location.getPolicy();
        return TvAnytimeElementFactory.gregorianCalendar(policy.getAvailabilityStart());
    }

    private XMLGregorianCalendar generateAvailabilityEnd(Location location) {
        Policy policy = location.getPolicy();
        return TvAnytimeElementFactory.gregorianCalendar(policy.getAvailabilityEnd());
    }
}