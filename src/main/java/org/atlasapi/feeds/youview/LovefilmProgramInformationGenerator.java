package org.atlasapi.feeds.youview;

import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.atlasapi.feeds.tvanytime.ProgramInformationGenerator;
import org.atlasapi.media.entity.Certificate;
import org.atlasapi.media.entity.Episode;
import org.atlasapi.media.entity.Film;
import org.atlasapi.media.entity.Item;
import org.atlasapi.media.entity.Version;

import tva.metadata._2010.BasicContentDescriptionType;
import tva.metadata._2010.DerivedFromType;
import tva.metadata._2010.ProgramInformationType;
import tva.metadata._2010.TVAParentalGuidanceType;
import tva.metadata._2010.TVATimeType;
import tva.metadata.extended._2010.ExtendedContentDescriptionType;
import tva.metadata.extended._2010.TargetingInformationType;
import tva.mpeg7._2008.ControlledTermUseType;
import tva.mpeg7._2008.UniqueIDType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.internal.ImmutableMap;
import com.metabroadcast.common.intl.Countries;
import com.metabroadcast.common.intl.Country;

public class LovefilmProgramInformationGenerator implements ProgramInformationGenerator {

    private static final String LOVEFILM_LINK_SUFFIX = "L";
    private static final String LOVEFILM_CRID_SEPARATOR = "_r";
    private static final String LOVEFILM_PRODUCT_CRID_PREFIX = "crid://lovefilm.com/product/";
    private static final String YOUVIEW_DEFAULT_RATING = "http://refdata.youview.com/mpeg7cs/YouViewContentRatingCS/2010-11-25#unrated";
    private static final String LOVEFILM_CRID_PREFIX = LOVEFILM_PRODUCT_CRID_PREFIX;
    private static final String LOVEFILM_PRODUCT_ID = "product_id.lovefilm.com";
    private static final String LOVEFILM_DEEP_LINKING_ID = "deep_linking_id.lovefilm.com";
    private static final String LOVEFILM_URI_PATTERN = "http:\\/\\/lovefilm\\.com\\/[a-z]*\\/";
//    private static final String LOVEFILM_SHOW_PREFIX = "http://lovefilm.com/shows/";
//    private static final String LOVEFILM_SEASON_PREFIX = "http://lovefilm.com/seasons/";
    
    private static final Map<String, String> YOUVIEW_CERTIFICATE_MAPPING = ImmutableMap.<String, String>builder()
            .put("U", "http://bbfc.org.uk/BBFCRatingCS/2002#U")
            .put("PG", "http://bbfc.org.uk/BBFCRatingCS/2002#PG")
            .put("12", "http://bbfc.org.uk/BBFCRatingCS/2002#12")
            .put("15", "http://bbfc.org.uk/BBFCRatingCS/2002#15")
            .put("18", "http://bbfc.org.uk/BBFCRatingCS/2002#18")
            .build();

    private DatatypeFactory datatypeFactory;

    /**
     * NB DatatypeFactory is required for creation of javax Durations
     * This DatatypeFactory class may not be threadsafe
     */
    public LovefilmProgramInformationGenerator() {
        try {
            this.datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            Throwables.propagate(e);
        }
    }

    @Override
    public ProgramInformationType generate(Item item) {
        ProgramInformationType progInfo = new ProgramInformationType();

        // TODO digital_release_id not ingested yet, currently a placeholder of id + '_version'
        progInfo.setProgramId(LOVEFILM_PRODUCT_CRID_PREFIX + getId(item) + LOVEFILM_CRID_SEPARATOR + getId(item) + "_version");
        progInfo.setBasicDescription(generateBasicDescription(item));
        progInfo.setDerivedFrom(generateDerivedFrom(item));
        progInfo.getOtherIdentifier().add(generateOtherId(item));

        return progInfo;
    }
    
    private String getId(Item item) {
        return item.getCanonicalUri().replaceAll(LOVEFILM_URI_PATTERN, "");
    }

    private UniqueIDType generateOtherId(Item item) {
        UniqueIDType id = new UniqueIDType();
        if (item instanceof Film) {
            id.setAuthority(LOVEFILM_DEEP_LINKING_ID);
        } else if (item instanceof Episode) {
            id.setAuthority(LOVEFILM_PRODUCT_ID);
        }
        id.setValue(getId(item) + LOVEFILM_LINK_SUFFIX);
        return id;
    }

    private BasicContentDescriptionType generateBasicDescription(Item item) {
        ExtendedContentDescriptionType basicDescription = new ExtendedContentDescriptionType();

        // ParentalGuidance
        basicDescription.setParentalGuidance(generateParentalGuidance(item));
        // ProductionDate
        basicDescription.setProductionDate(generateProductionDate(item));
        // ProductionLocation
        basicDescription.getProductionLocation().addAll(generateProductLocations(item));
        // Duration
        basicDescription.setDuration(generateDuration(item));
        // tva2:TargetingInformation
        basicDescription.getTargetingInformationOrTargetingInformationRef().add(new TargetingInformationType());

        return basicDescription;
    }

    private List<String> generateProductLocations(Item item) {
        return ImmutableList.copyOf(Iterables.transform(item.getCountriesOfOrigin(), new Function<Country, String>() {
            @Override
            public String apply(Country input) {
                return input.code().toLowerCase();
            }
        }));
    }

    private TVAParentalGuidanceType generateParentalGuidance(Item item) {
        TVAParentalGuidanceType parentalGuidance = new TVAParentalGuidanceType();
        // first with country of GB
        Certificate certificate = Iterables.getOnlyElement(
            Iterables.filter(item.getCertificates(), new Predicate<Certificate>() {
                @Override
                public boolean apply(Certificate input) {
                    return input.country().equals(Countries.GB);
                }
            }));

        ControlledTermUseType useType = new ControlledTermUseType();
        String href = YOUVIEW_CERTIFICATE_MAPPING.get(certificate.classification());
        if (href == null) {
            href = YOUVIEW_DEFAULT_RATING;
        }
        useType.setHref(href);
        parentalGuidance.setParentalRating(useType);
        return parentalGuidance;
    }

    private Duration generateDuration(Item item) {
        Version version = Iterables.getOnlyElement(item.getVersions());
        Integer durationInSecs = version.getDuration();
        if (durationInSecs != null) {
            return datatypeFactory.newDuration(durationInSecs * 1000);
        } 
        return null;
    }

    private TVATimeType generateProductionDate(Item item) {
        TVATimeType productionDate = new TVATimeType();
        productionDate.setTimePoint(item.getYear().toString());
        return productionDate;
    }

    private DerivedFromType generateDerivedFrom(Item item) {
        DerivedFromType derivedFrom = new DerivedFromType();
        derivedFrom.setCrid(LOVEFILM_CRID_PREFIX + getId(item));
//            if (item instanceof Film) {
//                derivedFrom.setCrid(LOVEFILM_CRID_PREFIX + getId(item));
//            } else if (item instanceof Episode) {
//                String parentUri = item.getContainer().getUri();
//                if (parentUri.contains(LOVEFILM_SEASON_PREFIX)) {
//                    derivedFrom.setCrid(LOVEFILM_CRID_PREFIX + parentUri.replace(LOVEFILM_SEASON_PREFIX, ""));
//                } else {
//                    derivedFrom.setCrid(LOVEFILM_CRID_PREFIX + parentUri.replace(LOVEFILM_SHOW_PREFIX, ""));
//                }
//            }
        return derivedFrom;
    }
}
