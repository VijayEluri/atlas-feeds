package org.atlasapi.feeds.youview.nitro;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.atlasapi.media.channel.Channel;
import org.atlasapi.media.entity.Alias;
import org.atlasapi.media.entity.Image;
import org.atlasapi.media.entity.Publisher;

import com.google.common.collect.Iterables;
import tva.metadata._2010.ControlledTermType;
import tva.metadata._2010.GenreType;
import tva.metadata._2010.ServiceInformationNameType;
import tva.metadata._2010.ServiceInformationType;
import tva.metadata._2010.SynopsisLengthType;
import tva.metadata._2010.SynopsisType;
import tva.metadata.extended._2010.ContentPropertiesType;
import tva.metadata.extended._2010.ExtendedRelatedMaterialType;
import tva.metadata.extended._2010.StillImageContentAttributesType;
import tva.mpeg7._2008.MediaLocatorType;
import tva.mpeg7._2008.TextualType;

import javax.annotation.Nullable;

public abstract class ChannelGenerator {

    private final static String MAIN_GENRE_TYPE = "main";
    private final static String OTHER_GENRE_TYPE = "other";
    private final static String MAIN_GENRE_HREF = "urn:tva:metadata:cs:MediaTypeCS:2005:7.1.3";
    private final static String IMAGE_INTENDED_USE_MAIN = "http://refdata.youview.com/mpeg7cs/YouViewImageUsageCS/2010-09-23#role-primary";
    private final static String HOW_RELATED = "urn:tva:metadata:cs:HowRelatedCS:2010:19";
    private final static String FORMAT = "urn:mpeg:mpeg7:cs:FileFormatCS2001:1";
    private final static String INTERACTIVE_FORMAT = "http://refdata.youview.com/mpeg7cs/YouViewIdentifierTypeCS/2014-09-25#groupId.application.linearEnhancement";
    private final static String INTERACTIVE_MEDIA_LOCATOR_URI = "crid://bbc.co.uk/iplayer/flash_player/1";
    private final static String INTERACTIVE_HOW_RELATED = "urn:tva:metadata:cs:HowRelatedCS:2010:10.5";

    private static final String BBC_IMAGE_TYPE = "bbc:imageType";
    private static final String DOG = "dog";

    protected void setRelatedMaterial(Channel channel,
            ServiceInformationType serviceInformationType, String imageIntendedUse) {
        Image image;
        if (NitroMasterbrandInfoGenerator.IMAGE_INTENDED_USE_2.equals(imageIntendedUse)) {
            image = getBbcDogImage(channel);
        } else {
            image = Iterables.getFirst(channel.getImages(), null);
        }
        ExtendedRelatedMaterialType relatedMaterial = new ExtendedRelatedMaterialType();
        if (image != null) {
            ControlledTermType howRelated = new ControlledTermType();
            howRelated.setHref(HOW_RELATED);
            relatedMaterial.setHowRelated(howRelated);
            ControlledTermType format = new ControlledTermType();
            format.setHref(FORMAT);
            relatedMaterial.setFormat(format);
            setMediaLocator(image, relatedMaterial);
            setPromotionalText(channel, relatedMaterial);
            setContentProperties(image, relatedMaterial, imageIntendedUse);
            serviceInformationType.getRelatedMaterial().add(relatedMaterial);
        }
    }

    private Image getBbcDogImage(Channel channel) {
        Optional<Image> image = FluentIterable.from(channel.getImages())
                .firstMatch(new Predicate<Image>() {
                    public boolean apply(@Nullable Image image) {
                        return image.getAliases() != null &&
                                image.getAliases().contains(new Alias(BBC_IMAGE_TYPE, DOG));
                    }
                });

        return image.isPresent() ? image.get() : null;
    }

    protected void setRelatedMaterialForInteractive(ServiceInformationType serviceInformationType) {
        ExtendedRelatedMaterialType relatedMaterial = new ExtendedRelatedMaterialType();

        ControlledTermType howRelated = new ControlledTermType();
        howRelated.setHref(INTERACTIVE_HOW_RELATED);
        relatedMaterial.setHowRelated(howRelated);

        ControlledTermType format = new ControlledTermType();
        format.setHref(INTERACTIVE_FORMAT);
        relatedMaterial.setFormat(format);

        setMediaLocatorForInteractive(relatedMaterial);

        serviceInformationType.getRelatedMaterial().add(relatedMaterial);
    }

    protected void setContentProperties(Image image, ExtendedRelatedMaterialType relatedMaterialType,
            String imageIntendedUse) {
        ContentPropertiesType contentProperties = new ContentPropertiesType();
        StillImageContentAttributesType contentAttributes = new StillImageContentAttributesType();
        contentAttributes.setHeight(
                image.getHeight() != null && image.getHeight() != 0 ?
                        image.getHeight() : null
        );
        contentAttributes.setWidth(
                image.getWidth() != null && image.getWidth() != 0 ?
                        image.getWidth() : null
        );
        ControlledTermType mainIntendedUse = new ControlledTermType();
        mainIntendedUse.setHref(IMAGE_INTENDED_USE_MAIN);
        contentAttributes.getIntendedUse().add(mainIntendedUse);
        ControlledTermType intendedUse = new ControlledTermType();
        intendedUse.setHref(imageIntendedUse);
        contentAttributes.getIntendedUse().add(intendedUse);
        contentProperties.getContentAttributes().add(contentAttributes);
        relatedMaterialType.setContentProperties(contentProperties);
    }


    protected void setPromotionalText(Channel channel, ExtendedRelatedMaterialType relatedMaterial) {
        TextualType promotionalText = new TextualType();
        promotionalText.setValue(channel.getTitle());
        relatedMaterial.getPromotionalText().add(promotionalText);
    }

    protected void setMediaLocator(Image image, ExtendedRelatedMaterialType relatedMaterial) {
        MediaLocatorType mediaLocator = new MediaLocatorType();
        mediaLocator.setMediaUri(image.getCanonicalUri());
        relatedMaterial.setMediaLocator(mediaLocator);
    }

    protected void setDescriptions(Channel channel, ServiceInformationType serviceInformationType) {
        SynopsisType longDescription = new SynopsisType();
        longDescription.setLength(SynopsisLengthType.LONG);
        longDescription.setValue(channel.getTitle());
        serviceInformationType.getServiceDescription().add(longDescription);
    }

    protected void setNameAndOwner(Channel channel, ServiceInformationType serviceInformationType) {
        ServiceInformationNameType name = new ServiceInformationNameType();
        name.setValue(channel.getTitle());
        Publisher broadcaster = channel.getBroadcaster();
        if (broadcaster != null) {
            serviceInformationType.getOwner().add(broadcaster.title());
        }
        serviceInformationType.getName().add(name);
    }

    protected void setGenres(ServiceInformationType serviceInformationType, String otherGenreHref1,
            String otherGenreHref2) {
        GenreType mainGenre = new GenreType();
        mainGenre.setType(MAIN_GENRE_TYPE);
        mainGenre.setHref(MAIN_GENRE_HREF);
        serviceInformationType.getServiceGenre().add(mainGenre);
        GenreType otherGengre1 = new GenreType();
        otherGengre1.setType(OTHER_GENRE_TYPE);
        otherGengre1.setHref(otherGenreHref1);
        serviceInformationType.getServiceGenre().add(otherGengre1);
        GenreType otherGengre2 = new GenreType();
        otherGengre2.setType(OTHER_GENRE_TYPE);
        otherGengre2.setHref(otherGenreHref2);
        serviceInformationType.getServiceGenre().add(otherGengre2);
    }
}
