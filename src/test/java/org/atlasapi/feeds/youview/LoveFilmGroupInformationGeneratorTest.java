package org.atlasapi.feeds.youview;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.atlasapi.feeds.tvanytime.GroupInformationGenerator;
import org.atlasapi.media.entity.Alias;
import org.atlasapi.media.entity.Brand;
import org.atlasapi.media.entity.Certificate;
import org.atlasapi.media.entity.CrewMember;
import org.atlasapi.media.entity.Episode;
import org.atlasapi.media.entity.Film;
import org.atlasapi.media.entity.MediaType;
import org.atlasapi.media.entity.ParentRef;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.Series;
import org.atlasapi.media.entity.Specialization;
import org.atlasapi.media.entity.Version;
import org.joda.time.Duration;
import org.junit.Test;

import tva.metadata._2010.BaseMemberOfType;
import tva.metadata._2010.BasicContentDescriptionType;
import tva.metadata._2010.ControlledTermType;
import tva.metadata._2010.CreditsItemType;
import tva.metadata._2010.GenreType;
import tva.metadata._2010.GroupInformationType;
import tva.metadata._2010.ProgramGroupTypeType;
import tva.metadata._2010.SynopsisLengthType;
import tva.metadata._2010.SynopsisType;
import tva.metadata.extended._2010.ExtendedRelatedMaterialType;
import tva.metadata.extended._2010.StillImageContentAttributesType;
import tva.mpeg7._2008.ExtendedLanguageType;
import tva.mpeg7._2008.NameComponentType;
import tva.mpeg7._2008.PersonNameType;
import tva.mpeg7._2008.TitleType;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.metabroadcast.common.intl.Countries;

public class LoveFilmGroupInformationGeneratorTest {
    
    private static final Function<GenreType, String> TO_HREF = new Function<GenreType, String>() {
        @Override
        public String apply(GenreType input) {
            return input.getHref();
        }
    };
    
    private final SynopsisTypeEquivalence SYNOPSIS_EQUIVALENCE = new SynopsisTypeEquivalence();
    private final NameComponentTypeEquivalence NAME_EQUIVALENCE = new NameComponentTypeEquivalence();
    
    private final YouViewGenreMapping genreMapping = new YouViewGenreMapping(); 
    private final GroupInformationGenerator generator = new LoveFilmGroupInformationGenerator(genreMapping);

    /**
     * This tests both the conversion from lovefilm genre uris to the youview
     * genres, as well as de-duplication of the resulting genre collection
     */
    @Test
    public void testFilmGenreGeneration() {
        
        Film film = createFilm();
        film.setGenres(ImmutableSet.of(
            "http://lovefilm.com/genres/comedy",
            "http://lovefilm.com/genres/comedy/family",
            "http://lovefilm.com/genres/comedy/general"
            ));
        
        GroupInformationType groupInfo = generator.generate(film);
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        List<String> genreHrefs = Lists.newArrayList(Iterables.transform(desc.getGenre(), TO_HREF));
        
        List<String> expectedGenres = Lists.newArrayList(
            "urn:tva:metadata:cs:OriginationCS:2005:5.7",
            "urn:tva:metadata:cs:MediaTypeCS:2005:7.1.3",
            "urn:tva:metadata:cs:ContentCS:2010:3.5.7",
            "urn:tva:metadata:cs:IntendedAudienceCS:2010:4.9.9"
            );
        
        Collections.sort(expectedGenres);
        Collections.sort(genreHrefs);
        
        assertEquals(expectedGenres, genreHrefs);
    }
    
    @Test
    public void testTVGenreGeneration() {
        
        Episode episode = createEpisode();
        episode.setGenres(ImmutableSet.of(
            "http://lovefilm.com/genres/comedy",
            "http://lovefilm.com/genres/comedy/family"
            ));
        
        GroupInformationType groupInfo = generator.generate(episode, Optional.of(createSeries()), Optional.of(createBrand()));
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        List<String> genreHrefs = Lists.newArrayList(Iterables.transform(desc.getGenre(), TO_HREF));
        
        List<String> expectedGenres = Lists.newArrayList(
            "urn:tva:metadata:cs:OriginationCS:2005:5.8",
            "urn:tva:metadata:cs:MediaTypeCS:2005:7.1.3",
            "urn:tva:metadata:cs:ContentCS:2010:3.5.7",
            "urn:tva:metadata:cs:IntendedAudienceCS:2010:4.9.9"
            );
        
        Collections.sort(expectedGenres);
        Collections.sort(genreHrefs);
        
        assertEquals(expectedGenres, genreHrefs);
    }
    
    @Test
    public void testSynopsisGeneration() {
        Film film = createFilm();
        film.setDescription(
            "Some lengthy episode description, that manages to go well over the medium description cut-off and " +
            "thus shows the differences between short, medium and long descriptions, particularly regarding the " +
            "appending or not of ellipses."
            );
        
        GroupInformationType groupInfo = generator.generate(film);
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        SynopsisType shortDesc = new SynopsisType();
        shortDesc.setLength(SynopsisLengthType.SHORT);
        shortDesc.setValue("Some lengthy episode description, that manages to go well over the medium description...");
        
        SynopsisType mediumDesc = new SynopsisType();
        mediumDesc.setLength(SynopsisLengthType.MEDIUM);
        mediumDesc.setValue(
            "Some lengthy episode description, that manages to go well over the medium description cut-off" +
            " and thus shows the differences between short, medium and long descriptions, particularly regarding the appending..."
            );
        
        SynopsisType longDesc = new SynopsisType();
        longDesc.setLength(SynopsisLengthType.LONG);
        longDesc.setValue(
            "Some lengthy episode description, that manages to go well over the medium description cut-off and " +
            "thus shows the differences between short, medium and long descriptions, particularly regarding the appending or " +
            "not of ellipses."
            );
        
        assertTrue(SYNOPSIS_EQUIVALENCE.pairwise().equivalent(
            ImmutableSet.of(shortDesc, mediumDesc, longDesc), 
            desc.getSynopsis()
            ));
    }
    
    @Test
    public void testImageGeneration() {
        Film film = createFilm();
        film.setImage("http://www.lovefilm.com/lovefilm/images/products/heroshots/1/177221-large.jpg");
        
        GroupInformationType groupInfo = generator.generate(film);
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        ExtendedRelatedMaterialType relatedMaterial = (ExtendedRelatedMaterialType) Iterables.getOnlyElement(desc.getRelatedMaterial());

        assertEquals("urn:tva:metadata:cs:HowRelatedCS:2010:19", relatedMaterial.getHowRelated().getHref());
        assertEquals("urn:mpeg:mpeg7:cs:FileFormatCS:2001:1", relatedMaterial.getFormat().getHref());

        assertEquals(
            "http://www.lovefilm.com/lovefilm/images/products/heroshots/1/177221-large.jpg", 
            relatedMaterial.getMediaLocator().getMediaUri()
        );

        StillImageContentAttributesType imageProperties = (StillImageContentAttributesType)
                Iterables.getOnlyElement(relatedMaterial.getContentProperties().getContentAttributes());
        
        assertEquals(Integer.valueOf(640), imageProperties.getWidth());
        assertEquals(Integer.valueOf(360), imageProperties.getHeight());
        
        ControlledTermType usage = Iterables.getOnlyElement(imageProperties.getIntendedUse());
        
        assertEquals("http://refdata.youview.com/mpeg7cs/YouViewImageUsageCS/2010-09-23#role-primary", usage.getHref());
    }
    
    @Test
    public void testPersonGeneration() {
        Film film = createFilm();
        
        CrewMember georgeScott = new CrewMember();
        georgeScott.withName("George C. Scott");
        CrewMember stanley = new CrewMember();
        stanley.withName("Stanley Kubrick");
        film.setPeople(ImmutableList.of(georgeScott, stanley));
        
        GroupInformationType groupInfo = generator.generate(film);
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        List<CreditsItemType> creditsList = desc.getCreditsList().getCreditsItem();
        for (CreditsItemType credit : creditsList) {
            assertEquals("urn:mpeg:mpeg7:cs:RoleCS:2001:UNKNOWN", credit.getRole());
        }
        
        Iterable<NameComponentType> people = Iterables.transform(
            creditsList, 
            new Function<CreditsItemType, NameComponentType>() {
                @Override
                public NameComponentType apply(CreditsItemType input) {
                    PersonNameType firstPerson = (PersonNameType) Iterables.getOnlyElement(input.getPersonNameOrPersonNameIDRefOrOrganizationName()).getValue();
                    return (NameComponentType) Iterables.getOnlyElement(firstPerson.getGivenNameOrLinkingNameOrFamilyName()).getValue();
                }
            });
     
        NameComponentType scott = new NameComponentType();
        scott.setValue("George C. Scott");
        
        NameComponentType kubrick = new NameComponentType();
        kubrick.setValue("Stanley Kubrick");
        
        assertTrue(NAME_EQUIVALENCE.pairwise().equivalent(
                ImmutableSet.of(scott, kubrick), 
                people
                ));
    }
    
    @Test
    public void testFilmOnDemandGeneration() {
        GroupInformationType groupInfo = generator.generate(createFilm());

        assertEquals("crid://lovefilm.com/product/filmAsin", groupInfo.getGroupId());
        assertEquals("http://lovefilm.com/ContentOwning", groupInfo.getServiceIDRef());
        ProgramGroupTypeType groupType = (ProgramGroupTypeType) groupInfo.getGroupType();
        assertEquals("programConcept", groupType.getValue());
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        TitleType title = Iterables.getOnlyElement(desc.getTitle());
        assertEquals("Dr. Strangelove", title.getValue());
        assertEquals("main", Iterables.getOnlyElement(title.getType()));
        
        ExtendedLanguageType language = Iterables.getOnlyElement(desc.getLanguage());
        assertEquals("original", language.getType());
        assertEquals("en", language.getValue());
    }

    @Test
    public void testEpisodeOnDemandGeneration() {
        GroupInformationType groupInfo = generator.generate(createEpisode(), Optional.of(createSeries()), Optional.of(createBrand()));

        assertEquals("crid://lovefilm.com/product/episodeAsin", groupInfo.getGroupId());
        
        BaseMemberOfType memberOf = Iterables.getOnlyElement(groupInfo.getMemberOf());
        assertEquals("crid://lovefilm.com/product/seriesAsin", memberOf.getCrid());
        assertEquals(Long.valueOf(5), memberOf.getIndex());
        
        ProgramGroupTypeType groupType = (ProgramGroupTypeType) groupInfo.getGroupType();
        assertEquals("programConcept", groupType.getValue());
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        TitleType title = Iterables.getOnlyElement(desc.getTitle());
        assertEquals("Episode 1", title.getValue());
        assertEquals("main", Iterables.getOnlyElement(title.getType()));
        
        ExtendedLanguageType language = Iterables.getOnlyElement(desc.getLanguage());
        assertEquals("original", language.getType());
        assertEquals("en", language.getValue());
    }
    
    @Test
    public void testSeriesOnDemandGeneration() {
        Episode episode = createEpisode();
        episode.setImage("episode image");
        
        GroupInformationType groupInfo = generator.generate(createSeries(), Optional.of(createBrand()), episode);

        assertEquals("crid://lovefilm.com/product/seriesAsin", groupInfo.getGroupId());
        assertTrue(groupInfo.isOrdered());
        
        BaseMemberOfType memberOf = Iterables.getOnlyElement(groupInfo.getMemberOf());
        assertEquals("crid://lovefilm.com/product/brandAsin", memberOf.getCrid());
        assertEquals(Long.valueOf(2), memberOf.getIndex());
        
        ProgramGroupTypeType groupType = (ProgramGroupTypeType) groupInfo.getGroupType();
        assertEquals("series", groupType.getValue());
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        TitleType title = Iterables.getOnlyElement(desc.getTitle());
        assertEquals("Series 2", title.getValue());
        assertEquals("main", Iterables.getOnlyElement(title.getType()));

        ExtendedLanguageType language = Iterables.getOnlyElement(desc.getLanguage());
        assertEquals("original", language.getType());
        assertEquals("en", language.getValue());

        ExtendedRelatedMaterialType relatedMaterial = (ExtendedRelatedMaterialType) Iterables.getOnlyElement(desc.getRelatedMaterial());

        assertEquals(
            "episode image", 
            relatedMaterial.getMediaLocator().getMediaUri()
        );
    }
    
    @Test
    public void testBrandOnDemandGeneration() {
        Episode episode = createEpisode();
        episode.setImage("episode image");
        
        GroupInformationType groupInfo = generator.generate(createBrand(), episode);

        assertEquals("crid://lovefilm.com/product/brandAsin", groupInfo.getGroupId());
        assertEquals("http://lovefilm.com/ContentOwning", groupInfo.getServiceIDRef());
        assertTrue(groupInfo.isOrdered());
                
        ProgramGroupTypeType groupType = (ProgramGroupTypeType) groupInfo.getGroupType();
        assertEquals("show", groupType.getValue());
        
        BasicContentDescriptionType desc = groupInfo.getBasicDescription();
        
        TitleType title = Iterables.getOnlyElement(desc.getTitle());
        assertEquals("Northern Lights", title.getValue());
        assertEquals("main", Iterables.getOnlyElement(title.getType()));
        
        ExtendedRelatedMaterialType relatedMaterial = (ExtendedRelatedMaterialType) Iterables.getOnlyElement(desc.getRelatedMaterial());

        assertEquals(
            "episode image", 
            relatedMaterial.getMediaLocator().getMediaUri()
        );
    }
    
    @Test
    public void testSecondaryTitleGeneration() {
        Film film = createFilm();
        
        film.setTitle("The film");
        GroupInformationType groupInfo = generator.generate(film);
        
        List<TitleType> titles = groupInfo.getBasicDescription().getTitle();
        assertThat(titles.size(), is(2));
        
        TitleType first = titles.get(0);
        TitleType second = titles.get(1);
        
        if (Iterables.getOnlyElement(first.getType()).equals("main")) {
            assertEquals("The film", first.getValue());
            assertEquals("film, The", second.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(second.getType()));
        } else if (Iterables.getOnlyElement(second.getType()).equals("main")) {
            assertEquals("The film", second.getValue());
            assertEquals("film, The", first.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(first.getType()));
        }
        
        film.setTitle("the film");
        groupInfo = generator.generate(film);
        
        titles = groupInfo.getBasicDescription().getTitle();
        assertThat(titles.size(), is(2));
        
        first = titles.get(0);
        second = titles.get(1);
        
        if (Iterables.getOnlyElement(first.getType()).equals("main")) {
            assertEquals("the film", first.getValue());
            assertEquals("film, the", second.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(second.getType()));
        } else if (Iterables.getOnlyElement(second.getType()).equals("main")) {
            assertEquals("the film", second.getValue());
            assertEquals("film, the", first.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(first.getType()));
        }
        
        film.setTitle("a film");
        groupInfo = generator.generate(film);
        
        titles = groupInfo.getBasicDescription().getTitle();
        assertThat(titles.size(), is(2));
        
        first = titles.get(0);
        second = titles.get(1);
        
        if (Iterables.getOnlyElement(first.getType()).equals("main")) {
            assertEquals("a film", first.getValue());
            assertEquals("film, a", second.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(second.getType()));
        } else if (Iterables.getOnlyElement(second.getType()).equals("main")) {
            assertEquals("a film", second.getValue());
            assertEquals("film, a", first.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(first.getType()));
        }
        
        film.setTitle("An interesting film");
        groupInfo = generator.generate(film);
        
        titles = groupInfo.getBasicDescription().getTitle();
        assertThat(titles.size(), is(2));
        
        first = titles.get(0);
        second = titles.get(1);
        
        if (Iterables.getOnlyElement(first.getType()).equals("main")) {
            assertEquals("An interesting film", first.getValue());
            assertEquals("interesting film, An", second.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(second.getType()));
        } else if (Iterables.getOnlyElement(second.getType()).equals("main")) {
            assertEquals("An interesting film", second.getValue());
            assertEquals("interesting film, An", first.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(first.getType()));
        }
        
        film.setTitle("Some interesting film");
        groupInfo = generator.generate(film);
        
        TitleType title = Iterables.getOnlyElement(groupInfo.getBasicDescription().getTitle());
        
        assertEquals("Some interesting film", title.getValue());
        assertEquals("main", Iterables.getOnlyElement(title.getType()));
    }
    
    @Test
    public void testSecondaryTitleGenerationDoesntReplaceNonFirstWord() {
        Film film = createFilm();
        
        film.setTitle("the film that contains another instance of the");
        GroupInformationType groupInfo = generator.generate(film);
        
        List<TitleType> titles = groupInfo.getBasicDescription().getTitle();
        assertThat(titles.size(), is(2));
        
        TitleType first = titles.get(0);
        TitleType second = titles.get(1);
        
        if (Iterables.getOnlyElement(first.getType()).equals("main")) {
            assertEquals("the film that contains another instance of the", first.getValue());
            assertEquals("film that contains another instance of the, the", second.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(second.getType()));
        } else if (Iterables.getOnlyElement(second.getType()).equals("main")) {
            assertEquals("the film that contains another instance of the", second.getValue());
            assertEquals("film that contains another instance of the, The", first.getValue());
            assertEquals("secondary", Iterables.getOnlyElement(first.getType()));
        }
    }
    
    private Brand createBrand() {
        Brand brand = new Brand();
        
        brand.setCanonicalUri("http://lovefilm.com/episodes/184930");
        brand.setCurie("lf:e-184930");
        brand.setTitle("Northern Lights");
        brand.setDescription("Some brand description");
        brand.setImage("http://www.lovefilm.com/lovefilm/images/products/heroshots/0/184930-large.jpg");
        brand.setPublisher(Publisher.LOVEFILM);
        brand.setCertificates(ImmutableList.of(new Certificate("15", Countries.GB)));
        brand.setYear(2006);
        brand.setLanguages(ImmutableList.of("en"));
        brand.setMediaType(MediaType.VIDEO);
        brand.setSpecialization(Specialization.TV);
        brand.addAlias(new Alias("gb:amazon:asin", "brandAsin"));
        
        CrewMember robson = new CrewMember();
        robson.withName("Robson Green");
        CrewMember mark = new CrewMember();
        mark.withName("Mark Benton");
        brand.setPeople(ImmutableList.of(robson, mark));
        
        return brand;
    }
    
    private Series createSeries() {
        Series series = new Series();
        
        series.setCanonicalUri("http://lovefilm.com/episodes/179534");
        series.setCurie("lf:e-179534");
        series.setTitle("Series 2");
        series.setDescription("Some series description");
        series.setImage("http://www.lovefilm.com/lovefilm/images/products/heroshots/0/179534-large.jpg");
        series.setPublisher(Publisher.LOVEFILM);
        series.setCertificates(ImmutableList.of(new Certificate("15", Countries.GB)));
        series.setYear(2006);
        series.setLanguages(ImmutableList.of("en"));
        series.setMediaType(MediaType.VIDEO);
        series.setSpecialization(Specialization.TV);
        series.addAlias(new Alias("gb:amazon:asin", "seriesAsin"));
        
        CrewMember robson = new CrewMember();
        robson.withName("Robson Green");
        CrewMember mark = new CrewMember();
        mark.withName("Mark Benton");
        series.setPeople(ImmutableList.of(robson, mark));
        
        ParentRef brandRef = new ParentRef("http://lovefilm.com/series/184930");
        series.setParentRef(brandRef);
        series.withSeriesNumber(2);
        
        return series;
    }
    
    private Episode createEpisode() {
        Episode episode = new Episode();
        
        episode.setCanonicalUri("http://lovefilm.com/episodes/180014");
        episode.setCurie("lf:e-180014");
        episode.setTitle("Episode 1");
        episode.setDescription("some episode description");
        episode.setImage("some episode image");
        episode.setPublisher(Publisher.LOVEFILM);
        episode.setCountriesOfOrigin(ImmutableSet.of(Countries.GB));
        episode.setCertificates(ImmutableList.of(new Certificate("15", Countries.GB)));
        episode.setYear(2006);
        episode.setLanguages(ImmutableList.of("en"));
        episode.setMediaType(MediaType.VIDEO);
        episode.setSpecialization(Specialization.TV);
        episode.addAlias(new Alias("gb:amazon:asin", "episodeAsin"));
        
        Version version = new Version();
        version.setDuration(Duration.standardMinutes(45));
        episode.addVersion(version);
        
        ParentRef seriesRef = new ParentRef("http://lovefilm.com/series/179534");
        episode.setSeriesRef(seriesRef);
        
        episode.setEpisodeNumber(5);
        episode.setSeriesNumber(2);
        
        return episode;
    }

    private Film createFilm() {
        Film film = new Film();
        
        film.setCanonicalUri("http://lovefilm.com/films/177221");
        film.setCurie("lf:f-177221");
        film.setTitle("Dr. Strangelove");
        film.setDescription("Some film description");
        film.setImage("image");
        film.setPublisher(Publisher.LOVEFILM);
        film.setCountriesOfOrigin(ImmutableSet.of(Countries.GB));
        film.setCertificates(ImmutableList.of(new Certificate("PG", Countries.GB)));
        film.setYear(1963);
        film.setLanguages(ImmutableList.of("en"));
        film.setMediaType(MediaType.VIDEO);
        film.setSpecialization(Specialization.FILM);
        film.addAlias(new Alias("gb:amazon:asin", "filmAsin"));
        
        Version version = new Version();
        version.setDuration(Duration.standardMinutes(90));
        film.addVersion(version);
        
        return film;
    }
    
    private class SynopsisTypeEquivalence extends Equivalence<SynopsisType> {

        @Override
        protected boolean doEquivalent(SynopsisType a, SynopsisType b) {
            return a.getValue().equals(b.getValue())
                && a.getLength().equals(b.getLength());
        }

        @Override
        protected int doHash(SynopsisType t) {
            return 0;
        }
    }
    
    private class NameComponentTypeEquivalence extends Equivalence<NameComponentType> {

        @Override
        protected boolean doEquivalent(NameComponentType a, NameComponentType b) {
            return a.getValue().equals(b.getValue());
        }

        @Override
        protected int doHash(NameComponentType t) {
            return 0;
        }
    }
}