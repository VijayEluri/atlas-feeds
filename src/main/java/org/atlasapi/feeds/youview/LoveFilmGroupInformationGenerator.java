package org.atlasapi.feeds.youview;

import static org.atlasapi.feeds.youview.LoveFilmOutputUtils.getId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.atlasapi.feeds.tvanytime.GroupInformationGenerator;
import org.atlasapi.media.entity.Brand;
import org.atlasapi.media.entity.Content;
import org.atlasapi.media.entity.CrewMember;
import org.atlasapi.media.entity.Episode;
import org.atlasapi.media.entity.Film;
import org.atlasapi.media.entity.MediaType;
import org.atlasapi.media.entity.ParentRef;
import org.atlasapi.media.entity.Series;
import org.atlasapi.media.entity.Specialization;

import tva.metadata._2010.BaseProgramGroupTypeType;
import tva.metadata._2010.BasicContentDescriptionType;
import tva.metadata._2010.ControlledTermType;
import tva.metadata._2010.CreditsItemType;
import tva.metadata._2010.CreditsListType;
import tva.metadata._2010.GenreType;
import tva.metadata._2010.GroupInformationType;
import tva.metadata._2010.MemberOfType;
import tva.metadata._2010.ProgramGroupTypeType;
import tva.metadata._2010.RelatedMaterialType;
import tva.metadata._2010.SynopsisLengthType;
import tva.metadata._2010.SynopsisType;
import tva.metadata.extended._2010.ContentPropertiesType;
import tva.metadata.extended._2010.ExtendedRelatedMaterialType;
import tva.metadata.extended._2010.StillImageContentAttributesType;
import tva.mpeg7._2008.ExtendedLanguageType;
import tva.mpeg7._2008.MediaLocatorType;
import tva.mpeg7._2008.NameComponentType;
import tva.mpeg7._2008.PersonNameType;
import tva.mpeg7._2008.TitleType;

import com.google.common.base.Objects;
import com.google.inject.internal.ImmutableMap;
import com.google.inject.internal.Lists;
import com.metabroadcast.common.text.Truncator;

public class LoveFilmGroupInformationGenerator implements GroupInformationGenerator {

    private static final String YOUVIEW_CREDIT_ROLE = "urn:mpeg:mpeg7:cs:RoleCS:2001:UNKNOWN";
    private static final String YOUVIEW_IMAGE_FORMAT = "urn:mpeg:mpeg7:cs:FileFormatCS:2001:1";
    private static final String YOUVIEW_IMAGE_HOW_RELATED = "urn:tva:metadata:cs:HowRelatedCS:2010:19";
    private static final String YOUVIEW_IMAGE_ATTRIBUTE_PROD_STILL = "http://refdata.youview.com/mpeg7cs/YouViewImageUsageCS/2010-09-23#source-production_still";
    private static final String YOUVIEW_IMAGE_ATTRIBUTE_PRIMARY_ROLE = "http://refdata.youview.com/mpeg7cs/YouViewImageUsageCS/2010-09-23#role-primary";
    private static final int IMAGE_HEIGHT = 360;
    private static final int IMAGE_WIDTH = 640;
    private static final String GROUP_TYPE_PROGRAMCONCEPT = "programConcept";
    private static final String GROUP_TYPE_SERIES = "series";
    private static final String GROUP_TYPE_SHOW = "show";
    private static final String LANGUAGE_TYPE_ORIGINAL = "original";
    private static final String GENRE_TYPE_MAIN = "main";
    private static final String GENRE_TYPE_OTHER = "other";
    private static final String LOVEFILM_URL = "http://lovefilm.com";
    private static final String LOVEFILM_PRODUCT_CRID_PREFIX = "crid://lovefilm.com/product/";
    private static final String TITLE_TYPE_MAIN = "main";
    private static final String LOVEFILM_MEDIATYPE_GENRE_VIDEO = "urn:tva:metadata:cs:MediaTypeCS:2005:7.1.3";
    
    private static final Map<Specialization, String> YOUVIEW_SPECIALIZATION_GENRE_MAPPING = ImmutableMap.<Specialization, String>builder()
        .put(Specialization.FILM, "urn:tva:metadata:cs:OriginationCS:2005:5.7")
        .put(Specialization.TV, "urn:tva:metadata:cs:OriginationCS:2005:5.8")
        .build();
    
    private static final Map<SynopsisLengthType, Integer> YOUVIEW_SYNOPSIS_LENGTH = ImmutableMap.<SynopsisLengthType, Integer>builder()
        .put(SynopsisLengthType.SHORT, 90)
        .put(SynopsisLengthType.MEDIUM, 210)
        .put(SynopsisLengthType.LONG, 1200)
        .build();
    
    private final YouViewGenreMapping genreMapping;
    Truncator truncator = new Truncator().omitTrailingPunctuationWhenTruncated().onlyTruncateAtAWordBoundary().withOmissionMarker("...");
    
    public LoveFilmGroupInformationGenerator(YouViewGenreMapping genreMapping) {
        this.genreMapping = genreMapping;
    }
    
    @Override
    public GroupInformationType generate(Film film) {
        GroupInformationType groupInfo = generateWithCommonFields(film, null);
        
        groupInfo.setGroupType(generateGroupType(GROUP_TYPE_PROGRAMCONCEPT));
        groupInfo.setServiceIDRef(LOVEFILM_URL);
        
        return groupInfo;
    }
    
    @Override
    public GroupInformationType generate(Episode episode) {
        GroupInformationType groupInfo = generateWithCommonFields(episode, null);
        
        groupInfo.setGroupType(generateGroupType(GROUP_TYPE_PROGRAMCONCEPT));
        
        if (episode.getSeriesRef() != null || episode.getContainer() != null) {
            MemberOfType memberOf = new MemberOfType();

            ParentRef parent = Objects.firstNonNull(episode.getSeriesRef(), episode.getContainer());
            memberOf.setCrid(LOVEFILM_PRODUCT_CRID_PREFIX + getId(parent.getUri()));
            memberOf.setIndex(Long.valueOf(episode.getEpisodeNumber()));
            groupInfo.getMemberOf().add(memberOf);
        }
        
        return groupInfo;  
    }
    
    @Override
    public GroupInformationType generate(Series series, Episode episode) {
        GroupInformationType groupInfo = generateWithCommonFields(series, episode);
        
        groupInfo.setGroupType(generateGroupType(GROUP_TYPE_SERIES));
        groupInfo.setOrdered(true);
        MemberOfType memberOf = new MemberOfType();
        if (series.getParent() != null) {
            memberOf.setCrid(LOVEFILM_PRODUCT_CRID_PREFIX + getId(series.getParent().getUri()));
        }
        if (series.getSeriesNumber() != null) {
            memberOf.setIndex(Long.valueOf(series.getSeriesNumber()));
        }
        groupInfo.getMemberOf().add(memberOf);
        
        return groupInfo;
    }
    
    @Override
    public GroupInformationType generate(Brand brand, Episode episode) {
        GroupInformationType groupInfo = generateWithCommonFields(brand, episode);
        
        groupInfo.setGroupType(generateGroupType(GROUP_TYPE_SHOW));
        groupInfo.setOrdered(true);
        groupInfo.setServiceIDRef(LOVEFILM_URL);
        
        return groupInfo;
    }
    
    GroupInformationType generateWithCommonFields(Content content, Episode episode) {
        GroupInformationType groupInfo = new GroupInformationType();
        
        groupInfo.setGroupId(LOVEFILM_PRODUCT_CRID_PREFIX + getId(content.getCanonicalUri()));
        // this needs tweaking depending on type
        groupInfo.setBasicDescription(generateBasicDescription(content, episode));
        
        return groupInfo;
    }
    
    private BaseProgramGroupTypeType generateGroupType(String groupType) {
        ProgramGroupTypeType type = new ProgramGroupTypeType();
        type.setValue(groupType);
        return type;
    }

    private BasicContentDescriptionType generateBasicDescription(Content content, Episode episode) {
        BasicContentDescriptionType basicDescription = new BasicContentDescriptionType();
        
        basicDescription.getTitle().add(generateTitle(content));
        basicDescription.getSynopsis().addAll(generateSynopses(content));
        basicDescription.getGenre().addAll(generateGenres(content));
        basicDescription.getGenre().add(generateGenreFromSpecialization(content));
        basicDescription.getGenre().add(generateGenreFromMediaType(content));
        basicDescription.getLanguage().addAll(generateLanguage(content));
        basicDescription.setCreditsList(generateCreditsList(content));
        if (content instanceof Series) {
            basicDescription.getRelatedMaterial().add(generateRelatedMaterial(episode));
        } else if (content instanceof Brand) {
            basicDescription.getRelatedMaterial().add(generateRelatedMaterial(episode));
        } else {
            basicDescription.getRelatedMaterial().add(generateRelatedMaterial(content));
        }
        
        return basicDescription;
    }

    private RelatedMaterialType generateRelatedMaterial(Content content) {
        ExtendedRelatedMaterialType relatedMaterial = new ExtendedRelatedMaterialType();
        
        relatedMaterial.setHowRelated(generateHowRelated());
        relatedMaterial.setFormat(generateFormat());
        relatedMaterial.setMediaLocator(generateMediaLocator(content));
        relatedMaterial.setContentProperties(generateContentProperties());
        
        return relatedMaterial;
    }

    private ContentPropertiesType generateContentProperties() {
        ContentPropertiesType contentProperties = new ContentPropertiesType();
        StillImageContentAttributesType attributes = new StillImageContentAttributesType();
        
        attributes.setWidth(IMAGE_WIDTH);
        attributes.setHeight(IMAGE_HEIGHT);
        
        ControlledTermType primaryRole = new ControlledTermType();
        primaryRole.setHref(YOUVIEW_IMAGE_ATTRIBUTE_PRIMARY_ROLE);
        attributes.getIntendedUse().add(primaryRole);
        
        ControlledTermType productionStill = new ControlledTermType();
        productionStill.setHref(YOUVIEW_IMAGE_ATTRIBUTE_PROD_STILL);
        attributes.getIntendedUse().add(productionStill);
        
        contentProperties.getContentAttributes().add(attributes);
        return contentProperties;
    }

    private ControlledTermType generateFormat() {
        ControlledTermType controlledTerm = new ControlledTermType();
        controlledTerm.setHref(YOUVIEW_IMAGE_FORMAT);
        return controlledTerm;
    }

    private ControlledTermType generateHowRelated() {
        ControlledTermType controlledTerm = new ControlledTermType();
        controlledTerm.setHref(YOUVIEW_IMAGE_HOW_RELATED);
        return controlledTerm;
    }

    private MediaLocatorType generateMediaLocator(Content content) {
        MediaLocatorType mediaLocator = new MediaLocatorType();
        mediaLocator.setMediaUri(content.getImage());
        return mediaLocator;
    }

    private CreditsListType generateCreditsList(Content content) {
       CreditsListType creditsList = new CreditsListType();
       
       for (CrewMember person : content.people()) {
           CreditsItemType credit = new CreditsItemType();
           credit.setRole(YOUVIEW_CREDIT_ROLE);
           
           PersonNameType personName = new PersonNameType();
           
           NameComponentType nameComponent = new NameComponentType();
           nameComponent.setValue(person.name());
           
           JAXBElement<NameComponentType> nameElem = new JAXBElement<NameComponentType>(new QName("urn:tva:mpeg7:2008", "GivenName"), NameComponentType.class, nameComponent);
           personName.getGivenNameOrLinkingNameOrFamilyName().add(nameElem);
           
           JAXBElement<PersonNameType> personNameElem = new JAXBElement<PersonNameType>(new QName("urn:tva:metadata:2010", "PersonName"), PersonNameType.class, personName);
           credit.getPersonNameOrPersonNameIDRefOrOrganizationName().add(personNameElem);
           
           creditsList.getCreditsItem().add(credit);
       }
       
       return creditsList;
    }

    private List<ExtendedLanguageType> generateLanguage(Content content) {
        List<ExtendedLanguageType> languages = Lists.newArrayList();
        for (String languageStr : content.getLanguages()) {
            ExtendedLanguageType language = new ExtendedLanguageType();
            language.setType(LANGUAGE_TYPE_ORIGINAL);
            language.setValue(languageStr);
            languages.add(language);
        }
        return languages;
    }

    private Collection<GenreType> generateGenres(Content content) {
        List<GenreType> genres = Lists.newArrayList();
        for (String genreStr : content.getGenres()) {
            for (String youViewGenre : genreMapping.get(genreStr)) {
                GenreType genre = new GenreType();
                genre.setType(GENRE_TYPE_MAIN);
                genre.setHref(youViewGenre);
                genres.add(genre);                
            }
        }
        return genres;
    }

    private GenreType generateGenreFromSpecialization(Content content) {
        GenreType genre = new GenreType();
        genre.setType(GENRE_TYPE_MAIN);
        genre.setHref(YOUVIEW_SPECIALIZATION_GENRE_MAPPING.get(content.getSpecialization()));
        return genre;
    }

    private GenreType generateGenreFromMediaType(Content content) {
       GenreType genre = new GenreType();
       genre.setType(GENRE_TYPE_OTHER);
       if (content.getMediaType().equals(MediaType.VIDEO)) {
           genre.setHref(LOVEFILM_MEDIATYPE_GENRE_VIDEO);
       } else {
           throw new RuntimeException("invalid media type " + content.getMediaType() + " on item " + content.getCanonicalUri());
       }
       return genre;
    }

    private List<SynopsisType> generateSynopses(Content content) {
        List<SynopsisType> synopses = Lists.newArrayList();
        for (Entry<SynopsisLengthType, Integer> entry : YOUVIEW_SYNOPSIS_LENGTH.entrySet()) {
            SynopsisType synopsis = new SynopsisType();
            synopsis.setLength(entry.getKey());
            String description = content.getDescription();
            if (description != null) {
                truncator = truncator.withMaxLength(entry.getValue());
                synopsis.setValue(truncator.truncate(description));
                synopses.add(synopsis);
            }
        }
        return synopses;
    }

    private TitleType generateTitle(Content content) {
        TitleType title = new TitleType();
        title.getType().add(TITLE_TYPE_MAIN);
        title.setValue(content.getTitle());
        return title;
    }
}