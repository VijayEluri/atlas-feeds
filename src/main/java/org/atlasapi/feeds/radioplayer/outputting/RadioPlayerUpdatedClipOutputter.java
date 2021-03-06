package org.atlasapi.feeds.radioplayer.outputting;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.atlasapi.feeds.radioplayer.RadioPlayerFeedSpec;
import org.atlasapi.feeds.radioplayer.RadioPlayerOdFeedSpec;
import org.atlasapi.feeds.radioplayer.RadioPlayerService;
import org.atlasapi.media.entity.Clip;
import org.atlasapi.media.entity.Container;
import org.atlasapi.media.entity.Described;
import org.atlasapi.media.entity.Encoding;
import org.atlasapi.media.entity.Identified;
import org.atlasapi.media.entity.Item;
import org.atlasapi.media.entity.Location;
import org.atlasapi.media.entity.MediaType;
import org.atlasapi.media.entity.Version;

import com.metabroadcast.common.base.MorePredicates;
import com.metabroadcast.common.intl.Countries;
import com.metabroadcast.common.intl.Country;
import com.metabroadcast.common.stream.MoreCollectors;
import com.metabroadcast.common.time.DateTimeZones;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import nu.xom.Attribute;
import nu.xom.Element;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;

public class RadioPlayerUpdatedClipOutputter extends RadioPlayerXMLOutputter {
    
    private static final String ORIGINATOR = "Metabroadcast";
    private static final DateTime MAX_AVAILABLE_TILL = new DateTime(2037, 1, 1, 0, 0, 0, 0, DateTimeZones.UTC);
    
    private final RadioPlayerGenreElementCreator genreElementCreator;

    public RadioPlayerUpdatedClipOutputter(RadioPlayerGenreElementCreator genreElementCreator) {
        this.genreElementCreator = genreElementCreator;
    }
    
    @Override
    protected Element createFeed(RadioPlayerFeedSpec spec, Iterable<RadioPlayerBroadcastItem> items) {
        
        checkArgument(spec instanceof RadioPlayerOdFeedSpec);
        Optional<DateTime> since = ((RadioPlayerOdFeedSpec)spec).getSince();
        Iterable<RadioPlayerBroadcastItem> validItems = StreamSupport
                .stream(items.spliterator(), false)
                .filter(hasUpdatedAndAvailableClip(since)::apply)
                .collect(Collectors.toList());

;

        Iterable<Clip> validClips = StreamSupport.stream(items.spliterator(), false)
                .flatMap(i -> compose(Item.TO_CLIPS, RadioPlayerBroadcastItem.TO_ITEM).apply(i).stream())
                .filter(availableAndUpdatedSince(since)::apply)
                .collect(MoreCollectors.toImmutableList());
        
        Element epgElem = createElement("epg", EPGSCHEDULE);
        EPGDATATYPES.addDeclarationTo(epgElem);
        XSI.addDeclarationTo(epgElem);
        RADIOPLAYER.addDeclarationTo(epgElem);
        epgElem.addAttribute(new Attribute("xsi:schemaLocation", XSI.getUri(), SCHEMALOCATION));
        epgElem.addAttribute(new Attribute("system", "DAB"));
        epgElem.addAttribute(new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en"));

        Element schedule = createElement("schedule", EPGSCHEDULE);
        schedule.addAttribute(new Attribute("originator", ORIGINATOR));
        schedule.addAttribute(new Attribute("version", "1"));
        schedule.addAttribute(new Attribute("creationTime", DATE_TIME_FORMAT.print(new DateTime(DateTimeZones.UTC))));

        schedule.appendChild(scopeElement(getScopeInterval(validClips), spec.getService()));

        for (RadioPlayerBroadcastItem item : validItems) {
            Iterable<Element> clipElements = createClipElements(item, spec.getService(), since);
            for (Element clipElement : clipElements) {
                schedule.appendChild(clipElement);
            }
        }

        epgElem.appendChild(schedule);
        return epgElem;
    }
    
    private static Predicate<RadioPlayerBroadcastItem> hasUpdatedAndAvailableClip(Optional<DateTime> since) {
        return MorePredicates.transformingPredicate(
                TO_CLIPS,
                MorePredicates.anyPredicate(availableAndUpdatedSince(since))
        );
    }
    
    public static Predicate<Clip> availableAndUpdatedSince(final Optional<DateTime> since) {
        if (since.isPresent()) {
            return Predicates.and(AUDIO_AND_AVAILABLE, updatedSince(since.get()));
        } else {
            return AUDIO_AND_AVAILABLE;
        }
    }
    
    private Element scopeElement(Interval scopeInterval, RadioPlayerService id) {
        
        Element scope = createElement("scope", EPGSCHEDULE);
        scope.addAttribute(new Attribute("startTime", DATE_TIME_FORMAT.print(scopeInterval.getStart())));
        scope.addAttribute(new Attribute("stopTime", DATE_TIME_FORMAT.print(scopeInterval.getEnd())));

        Element service = createElement("serviceScope", EPGSCHEDULE);
        service.addAttribute(new Attribute("id", id.getDabServiceId().replaceAll("_", ".")));
        service.addAttribute(new Attribute("radioplayerId", String.valueOf(id.getRadioplayerId())));
        scope.appendChild(service);
        return scope;
    }
    
    private Interval getScopeInterval(Iterable<Clip> validClips) {
        DateTime start = null;
        DateTime end = null;
        
        for (Clip clip : validClips) {
            Set<Version> versions = clip.getVersions();
            for (Version version : versions) {
                Set<Encoding> manifestedAs = version.getManifestedAs();
                for (Encoding encoding : manifestedAs) {
                    Set<Location> availableAt = encoding.getAvailableAt();
                    for (Location location : availableAt) {
                        DateTime availableFrom = location.getPolicy().getActualAvailabilityStart();
                        if (start == null || start.isAfter(availableFrom)) {
                            start = availableFrom;
                        }
                        
                        DateTime availableUntil = location.getPolicy().getAvailabilityEnd();
                        if (end == null || end.isBefore(availableUntil)) {
                            end = availableUntil;
                        }
                    }
                }
            }
        }
        if (end == null) {
            end = MAX_AVAILABLE_TILL;
        } else {
            end = Ordering.natural().min(end, MAX_AVAILABLE_TILL);
        }
        
        return new Interval(start, end);
    }

    private Iterable<Element> createClipElements(RadioPlayerBroadcastItem broadcastItem, RadioPlayerService id, Optional<DateTime> since) {
        
        Iterable<Clip> clips = filter(broadcastItem.getItem().getClips(), availableAndUpdatedSince(since));
        
        Builder<Element> elements = ImmutableList.builder();
        for (Clip clip : clips) {
            for (Version version : clip.getVersions()) {
                Element programme = createElement("programme", EPGSCHEDULE);
                programme.addAttribute(new Attribute("shortId", "0"));
                
                programme.addAttribute(new Attribute("id", createCridFromUri(clip.getCanonicalUri())));
        
                String title = clipTitle(itemOrContainerTitle(broadcastItem), clip);
                programme.appendChild(stringElement("mediumName", EPGDATATYPES, MEDIUM_TITLE.truncatePossibleNull(title)));
                programme.appendChild(stringElement("longName", EPGDATATYPES, LONG_TITLE.truncatePossibleNull(title)));

                programme.appendChild(
                        mediaDescription(
                                stringElement(
                                        "shortDescription",
                                        EPGDATATYPES,
                                        SHORT_DESC.truncatePossibleNull(clip.getDescription())
                                )
                        )
                );
                if (!Strings.isNullOrEmpty(clip.getImage())) {
                    for (ImageDimensions dimensions : imageDimensions) {
                        Element img = createImageDescriptionElem(clip, dimensions);
                        programme.appendChild(mediaDescription(img));
                    }
                }
        
                for (Element genreElement : genreElementCreator.genreElementsFor(broadcastItem.getItem())) {
                    programme.appendChild(genreElement);
                }
                
              //Because outputCountries always contains ALL, international block output is suppressed.
                Multimap<Country, Location> locationsByCountry = ArrayListMultimap.create();
                // bucket locations by country
                for (Encoding encoding : version.getManifestedAs()) {
                    for (Location location : encoding.getAvailableAt()) {
                        for (Country country : representedBy(encoding, location)) {
                            locationsByCountry.put(country, location);
                        }
                    }
                }
                
                for (Country country : locationsByCountry.keySet()) {
                    if (!country.equals(Countries.ALL)) {
                        Collection<Location> countryLocations = locationsByCountry.get(country);
                        programme.appendChild(ondemandElement(clip, version, null, countryLocations, id));
                    }
                }
                elements.add(programme);
            }
        }

        return elements.build();
    }
    
    private String clipTitle(String itemTitle, Clip clip) {
        if (Strings.isNullOrEmpty(itemTitle)) {
            return clip.getTitle();
        }
        if (Strings.isNullOrEmpty(clip.getTitle())) {
            return itemTitle;
        }
        return itemTitle + " : " + clip.getTitle();
    }
    
    private String itemOrContainerTitle(RadioPlayerBroadcastItem broadcastItem) {
        if (broadcastItem.hasContainer()) {
            Container brand = broadcastItem.getContainer();
            if (!Strings.isNullOrEmpty(brand.getTitle())) {
                String brandTitle = brand.getTitle();
                return brandTitle;// + " : " + title;
            }
        }
        if (!Strings.isNullOrEmpty(broadcastItem.getItem().getTitle())) {
            return broadcastItem.getItem().getTitle();
        }
        return "";
    }
    
    private Element mediaDescription(Element childElem) {
        Element descriptionElement = createElement("mediaDescription", EPGDATATYPES);
        descriptionElement.appendChild(childElem);
        return descriptionElement;
    }
    
    private static Predicate<Identified> updatedSince(final DateTime since) {
        return input -> input.getLastUpdated().isAfter(since);
    }
    
    private static final Predicate<Described> AUDIO_MEDIA_TYPE = input -> input.getMediaType().equals(MediaType.AUDIO);
    
    private static final Predicate<Clip> IS_ACTUALLY_AVAILABLE = input -> {
        for (Version version : input.getVersions()) {
            for (Encoding encoding : version.getManifestedAs()) {
                for (Location location : encoding.getAvailableAt()) {
                    boolean startIsAvailable = location.getPolicy().getActualAvailabilityStart() != null
                            && location.getPolicy().getActualAvailabilityStart().isBeforeNow();
                    boolean endIsAvailable = location.getPolicy().getAvailabilityEnd() == null
                            || location.getPolicy()
                            .getAvailabilityEnd()
                            .isAfterNow();
                    if (startIsAvailable && endIsAvailable) {
                        return true;
                    }
                }
            }
        }
        return false;
    };
    
    private static final Predicate<Clip> AUDIO_AND_AVAILABLE = Predicates.and(IS_ACTUALLY_AVAILABLE, AUDIO_MEDIA_TYPE);
    
    private static final Function<RadioPlayerBroadcastItem, List<Clip>> TO_CLIPS = Functions.compose(Item.TO_CLIPS, RadioPlayerBroadcastItem.TO_ITEM);
}
