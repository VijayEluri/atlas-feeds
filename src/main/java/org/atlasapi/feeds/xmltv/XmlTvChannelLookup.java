package org.atlasapi.feeds.xmltv;

import static com.google.common.base.Preconditions.checkNotNull;

import org.atlasapi.media.entity.Channel;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;

public class XmlTvChannelLookup {
    
    private XmlTvChannelLookup(){};
    
    public static class XmlTvChannel {
        
        private final Channel channel;
        private final String title;

        public XmlTvChannel(Channel channel, String title) {
            this.channel = checkNotNull(channel);
            this.title = checkNotNull(title);
        }
        
        public Channel channel() {
            return channel;
        }

        public String title() {
            return title;
        }
        
        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that instanceof XmlTvChannel) {
                XmlTvChannel other = (XmlTvChannel) that;
                return channel.equals(other.channel) && title.equals(other.title);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(channel, title);
        }

        @Override
        public String toString() {
            return String.format("XmlTv Channel: %s (%s)", channel.key(), title);
        }
    }
    
    public static XmlTvChannel channelFrom(Channel channel) {
        return new XmlTvChannel(channel, channel.title());
    }
    
    public static XmlTvChannel channelFrom(Channel channel, String title) {
        return new XmlTvChannel(channel, title);
    }

    public static final Channel IGNORED = new Channel("Ignored","http://ignored","ignored");
    
    public static final ImmutableBiMap<Integer, XmlTvChannel> MAP = ImmutableBiMap.<Integer, XmlTvChannel> builder()
            .put(22, channelFrom(Channel.NAT_GEO_WILD))
            .put(24, channelFrom(Channel.ITV1_ANGLIA))
            .put(25, channelFrom(Channel.ITV1_BORDER_SOUTH))
            .put(26, channelFrom(Channel.ITV1_LONDON))
            .put(27, channelFrom(Channel.ITV1_CARLTON_CENTRAL))
            .put(28, channelFrom(Channel.ITV1_CHANNEL))
            .put(29, channelFrom(Channel.ITV1_GRANADA))
            .put(30, channelFrom(Channel.ITV1_MERIDIAN))
            .put(31, channelFrom(Channel.ITV1_TYNE_TEES))
            .put(32, channelFrom(Channel.YTV, "YTV"))
            .put(33, channelFrom(Channel.ITV1_CARLTON_WESTCOUNTRY))
            .put(35, channelFrom(Channel.ITV1_WALES))
            .put(36, channelFrom(Channel.ITV1_WEST))
            .put(37, channelFrom(Channel.STV_CENTRAL))
            .put(38, channelFrom(Channel.ULSTER))
            .put(39, channelFrom(Channel.ANIMAL_PLANET))
            .put(40, channelFrom(Channel.SKY_ARTS_1))
            .put(43, channelFrom(Channel.B4U_MOVIES))
            .put(45, channelFrom(Channel.BBC_THREE, "BBC3"))
            .put(47, channelFrom(Channel.BBC_FOUR, "BBC4"))
            .put(48, channelFrom(Channel.BBC_NEWS))
            .put(49, channelFrom(Channel.BBC_PARLIMENT))
            .put(50, channelFrom(Channel.BBC_ENTERTAINMENT))
            .put(92, channelFrom(Channel.BBC_ONE, "BBC1"))
            .put(93, channelFrom(Channel.BBC_ONE_EAST, "BBC1 East"))
            .put(94, channelFrom(Channel.BBC_ONE, "BBC1 London"))
            .put(95, channelFrom(Channel.BBC_ONE_EAST_MIDLANDS, "BBC1 Midlands"))
            .put(96, channelFrom(Channel.BBC_ONE_NORTH_EAST, "BBC1 North"))
            .put(97, channelFrom(Channel.BBC_ONE_NORTH_EAST, "BBC1 North East"))
            .put(98, channelFrom(Channel.BBC_ONE_NORTH_WEST, "BBC1 North West"))
            .put(99, channelFrom(Channel.BBC_ONE_NORTHERN_IRELAND, "BBC1 Northern Ireland"))
            .put(100, channelFrom(Channel.BBC_ONE_WALES, "BBC1 Wales"))
            .put(101, channelFrom(Channel.BBC_ONE_SCOTLAND, "BBC1 Scotland"))
            .put(102, channelFrom(Channel.BBC_ONE_SOUTH, "BBC1 South"))
            .put(103, channelFrom(Channel.BBC_ONE_SOUTH_WEST, "BBC1 South West"))
            .put(104, channelFrom(Channel.BBC_ONE_WEST, "BBC1 West"))
            .put(105, channelFrom(Channel.BBC_TWO, "BBC2"))
            .put(106, channelFrom(Channel.BBC_TWO, "BBC2 East"))
            .put(107, channelFrom(Channel.BBC_TWO, "BBC2 London"))
            .put(108, channelFrom(Channel.BBC_TWO, "BBC2 Midlands"))
            .put(109, channelFrom(Channel.BBC_TWO, "BBC2 North West"))
            .put(110, channelFrom(Channel.BBC_TWO, "BBC2 North"))
            .put(111, channelFrom(Channel.BBC_TWO, "BBC2 North East"))
            .put(112, channelFrom(Channel.BBC_TWO_NORTHERN_IRELAND, "BBC2 Northern Ireland"))
            .put(113, channelFrom(Channel.BBC_TWO_SCOTLAND, "BBC2 Scotland"))
            .put(114, channelFrom(Channel.BBC_TWO_WALES, "BBC2 Wales"))
            .put(115, channelFrom(Channel.BBC_TWO, "BBC2 South"))
            .put(116, channelFrom(Channel.BBC_TWO, "BBC2 South West"))
            .put(117, channelFrom(Channel.BBC_TWO, "BBC2 West"))
            .put(119, channelFrom(Channel.BIO, "Bio"))
            .put(120, channelFrom(Channel.BLOOMBERG_TV))
            .put(122, channelFrom(Channel.BRAVO))
            .put(123, channelFrom(Channel.EUROSPORT, "Eurosport"))
            .put(125, channelFrom(Channel.CNBC))
            .put(126, channelFrom(Channel.CNN))
            .put(128, channelFrom(Channel.CARTOON_NETWORK))
            .put(129, channelFrom(Channel.BOOMERANG_PLUS1))
            .put(131, channelFrom(Channel.CHALLENGE))
            .put(132, channelFrom(Channel.CHANNEL_FOUR))
            .put(134, channelFrom(Channel.FIVE, "Channel 5"))
            .put(137, channelFrom(Channel.GOD_CHANNEL,"GOD Channel"))
            .put(147, channelFrom(Channel.DISCOVERY,"Discovery Channel"))
            .put(148, channelFrom(Channel.DISCOVERY_KNOWLEDGE))
            .put(149, channelFrom(Channel.DISCOVERY_HOME_AND_HEALTH,"Discovery Home & Health"))
            .put(150, channelFrom(Channel.DISCOVERY_REAL_TIME))
            .put(152, channelFrom(Channel.DISCOVERY_PLUS1, "Discovery Channel +1"))
            .put(153, channelFrom(Channel.DISCOVERY_SCIENCE))
            .put(154, channelFrom(Channel.DISCOVERY_TRAVEL_AND_LIVING, "Discovery Travel & Living"))
            .put(155, channelFrom(Channel.DISCOVERY_TURBO))
            .put(156, channelFrom(Channel.THE_DISNEY_CHANNEL))
            .put(157, channelFrom(Channel.THE_DISNEY_CHANNEL_PLUS1))
            .put(158, channelFrom(Channel.E_FOUR))
            .put(159, channelFrom(Channel.EURONEWS, "EuroNews"))
            .put(160, channelFrom(Channel.FILM_4, "Film4"))
            .put(165, channelFrom(Channel.DISNEY_XD, "Disney XD"))
            .put(166, channelFrom(Channel.DISNEY_XD_PLUS1, "Disney XD +1"))
            .put(180, channelFrom(Channel.UNIVERSAL))
            .put(182, channelFrom(Channel.HISTORY))
            .put(183, channelFrom(Channel.HISTORY_PLUS1))
            .put(185, channelFrom(Channel.ITV2))
            .put(191, channelFrom(Channel.KERRANG, "Kerrang!"))
            .put(197, channelFrom(Channel.SKY_LIVING))
            .put(202, channelFrom(Channel.MTV))
            .put(203, channelFrom(Channel.MTV_BASE))
            .put(204, channelFrom(Channel.MTV_HITS))
            .put(205, channelFrom(Channel.MTV_ROCKS))
            .put(206, channelFrom(Channel.MUTV))
            .put(213, channelFrom(Channel.NATIONAL_GEOGRAPHIC))
            .put(214, channelFrom(Channel.NATIONAL_GEOGRAPHIC_PLUS1))
            .put(215, channelFrom(Channel.NICK_JR))
            .put(216, channelFrom(Channel.NICKELODEON_REPLAY))
            .put(217, channelFrom(Channel.NICKELODEON))
            .put(228, channelFrom(Channel.QVC))
            .put(231, channelFrom(Channel.RTE1, "RTE1"))
            .put(246, channelFrom(IGNORED, "Screenshop"))
            .put(248, channelFrom(Channel.SKY1,"Sky1"))
            .put(249, channelFrom(Channel.SKY_MOVIES_PREMIERE))
            .put(250, channelFrom(Channel.SKY_MOVIES_PREMIERE_PLUS1))
            .put(253, channelFrom(Channel.SKY_MOVIES_INDIE))
            .put(256, channelFrom(Channel.SKY_NEWS))
            .put(257, channelFrom(Channel.SKY_MOVIES_COMEDY))
            .put(258, channelFrom(Channel.SKY_MOVIES_FAMILY))
            .put(259, channelFrom(Channel.SKY_MOVIES_CLASSICS))
            .put(260, channelFrom(Channel.SKY_MOVIES_MODERN_GREATS))
            .put(262, channelFrom(Channel.SKY_SPORTS_1))
            .put(264, channelFrom(Channel.SKY_SPORTS_2))
            .put(265, channelFrom(Channel.SKY_SPORTS_3))
            .put(266, channelFrom(IGNORED,"Sky Real Lives"))
            .put(267, channelFrom(Channel.SONY_ENTERTAINMENT_TV_ASIA))
            .put(271, channelFrom(Channel.TCM))
            .put(273, channelFrom(Channel.TG4,"TG4"))
            .put(276, channelFrom(Channel.TV5))
            .put(281, channelFrom(Channel.THE_BOX))
            .put(287, channelFrom(Channel.LIVING_PLUS2))
            .put(288, channelFrom(Channel.GOLD))
            .put(292, channelFrom(Channel.ALIBI))
            .put(293, channelFrom(Channel.VH1))
            .put(294, channelFrom(Channel.MTV_CLASSIC))
            .put(300, channelFrom(Channel.SKY_SPORTS_NEWS))
            .put(421, channelFrom(Channel.PLAYHOUSE_DISNEY, "Disney Junior"))//AKA Disney Junior
            .put(461, channelFrom(Channel.ITV1_BORDER_NORTH))
            .put(482, channelFrom(Channel.CBBC))
            .put(483, channelFrom(Channel.CBEEBIES))
            .put(581, channelFrom(Channel.ZEE_TV))
            .put(582, channelFrom(Channel.EXTREME_SPORTS))
            .put(588, channelFrom(Channel.MAGIC, "Magic"))
            .put(590, channelFrom(Channel.STAR_NEWS))
            .put(591, channelFrom(Channel.STAR_PLUS))
            .put(592, channelFrom(Channel.SMASH_HITS, "Smash Hits!"))
            .put(594, channelFrom(Channel.BID_TV))
            .put(609, channelFrom(Channel.KISS,"Kiss"))
            .put(610, channelFrom(Channel.MTV_DANCE))
            .put(625, channelFrom(Channel.EDEN_PLUS1))
            .put(661, channelFrom(Channel.ATTHERACES,"Attheraces"))
            .put(664, channelFrom(Channel.NICKTOONS_TV))
            .put(665, channelFrom(Channel.GOLD_PLUS1))
            .put(721, channelFrom(Channel.S4C))
            .put(742, channelFrom(Channel.CN_TOO))
            .put(801, channelFrom(Channel.YESTERDAY,"Yesterday"))
            .put(841, channelFrom(Channel.LIVING_PLUS1))
            .put(922, channelFrom(Channel.SKY2,"Sky2"))
            .put(941, channelFrom(Channel.TV3_SPANISH,"TV3 (Spanish)"))
            .put(1061, channelFrom(Channel.COMEDY_CENTRAL))
            .put(1143, channelFrom(Channel.SCUZZ,"Scuzz"))
            .put(1144, channelFrom(Channel.BLISS,"Bliss"))
            .put(1161, channelFrom(Channel.E4_PLUS1))
            .put(1201, channelFrom(Channel.COMEDY_CENTRAL_EXTRA))
            .put(1221, channelFrom(Channel.BRAVO_PLUS1))
            .put(1261, channelFrom(Channel.SKY_BOX_OFFICE_DIGITAL, "Sky Movies Box Office"))//AKA Sky Movies Box Office
            .put(1421, channelFrom(Channel.E_EXLAMATION, "E! Entertainment"))//AKA E! Entertainment
            .put(1461, channelFrom(Channel.FX))
            .put(1462, channelFrom(Channel.TRAVELCHANNEL, "travelchannel"))
            .put(1521, channelFrom(Channel.YESTERDAY_PLUS1))
            .put(1542, channelFrom(Channel.THE_COMMUNITY_CHANNEL))
            .put(1543, channelFrom(Channel.ESPN_AMERICA, "ESPN America"))
            .put(1544, channelFrom(Channel.FOUR_MUSIC,"4Music"))
            .put(1601, channelFrom(Channel.EDEN,"Eden"))
            .put(1602, channelFrom(Channel.BLIGHTY,"Blighty"))
            .put(1661, channelFrom(Channel.BOOMERANG))
            .put(1662, channelFrom(IGNORED,"Sky Real Lives 2"))
            .put(1741, channelFrom(Channel.CHELSEA_TV))
            .put(1761, channelFrom(Channel.ANIMAL_PLANET_PLUS1))
            .put(1764, channelFrom(Channel.DISCOVERY_REAL_TIME_PLUS1))
            .put(1802, channelFrom(IGNORED,"Ideal World"))
            .put(1804, channelFrom(Channel.TELEG,"TeleG"))
            .put(1855, channelFrom(Channel.TRAVELCHANNEL_PLUS1,"travelchannel +1"))
            .put(1859, channelFrom(Channel.ITV3))
            .put(1862, channelFrom(Channel.BBC_TWO,"BBC2 South East"))
            .put(1865, channelFrom(Channel.TVE_INTERNACIONAL))
            .put(1869, channelFrom(Channel.BBC_ONE_SOUTH_EAST,"BBC1 South East"))
            .put(1870, channelFrom(Channel.RTE2,"RTE2"))
            .put(1872, channelFrom(Channel.CHALLENGE_PLUS1))
            .put(1876, channelFrom(Channel.EUROSPORT_2))
            .put(1882, channelFrom(Channel.REALLY,"Really"))
            .put(1944, channelFrom(IGNORED,"Channel M"))
            .put(1949, channelFrom(Channel.RACING_UK,"Racing UK"))
            .put(1953, channelFrom(Channel.DISCOVERY_HOME_AND_HEALTH_PLUS1,"Discovery Home & Health +1"))
            .put(1956, channelFrom(IGNORED,"Teachers TV"))
            .put(1958, channelFrom(Channel.MOTORS_TV))
            .put(1959, channelFrom(Channel.MORE_FOUR,"More4"))
            .put(1961, channelFrom(Channel.ITV4))
            .put(1963, channelFrom(Channel.SKY3))//Pick TV
            .put(1969, channelFrom(IGNORED,"Fashion TV"))
            .put(1971, channelFrom(Channel.FX_PLUS))
            .put(1972, channelFrom(Channel.MORE4_PLUS1))
            .put(1981, channelFrom(Channel.CITV,"CITV"))
            .put(1983, channelFrom(Channel.CARTOONITO,"Cartoonito"))
            .put(1984, channelFrom(Channel.DISNEY_CINEMAGIC))
            .put(1985, channelFrom(Channel.DISNEY_CINEMAGIC_PLUS1))
            .put(1990, channelFrom(Channel.ITV2_PLUS1))
            .put(1993, channelFrom(Channel.ALIBI_PLUS1))
            .put(1994, channelFrom(Channel.BBC_HD))
            .put(2008, channelFrom(Channel.FIVE_USA,"5USA"))
            .put(2010, channelFrom(Channel.BRAVO_2))
            .put(2011, channelFrom(Channel.ESPN_CLASSIC,"ESPN Classic"))
            .put(2013, channelFrom(Channel.COMEDY_CENTRAL_PLUS1))
            .put(2014, channelFrom(Channel.PROPELLER_TV))
            .put(2016, channelFrom(Channel.TCM2))
            .put(2021, channelFrom(Channel.FILM4_PLUS1,"Film4 +1"))
            .put(2040, channelFrom(Channel.BBC_SPORT_INTERACTIVE_FREEVIEW, "BBC Sport Interactive: (Freeview)"))
            .put(2047, channelFrom(Channel.Channel_4_PLUS1))
            .put(2049, channelFrom(Channel.Channel_ONE))
            .put(2050, channelFrom(Channel.DAVE,"Dave"))
            .put(2052, channelFrom(Channel.DAVE_JA_VU,"Dave ja vu"))
            .put(2055, channelFrom(Channel.AL_JAZEERA_ENGLISH))
            .put(2056, channelFrom(Channel.Channel_4_HD))
            .put(2057, channelFrom(Channel.DISCOVERY_SHED))
            .put(2058, channelFrom(Channel.MOVIES4MEN,"Movies4Men"))
            .put(2059, channelFrom(Channel.TRUE_MOVIES,"True Movies 1"))
            .put(2062, channelFrom(Channel.FIVER,"5*"))
            .put(2098, channelFrom(Channel.BBC_ALBA))
            .put(2115, channelFrom(Channel.WATCH))
            .put(2116, channelFrom(Channel.WATCH_PLUS1))
            .put(2118, channelFrom(Channel.ITV1_HD, "ITV1 London HD"))
            .put(2122, channelFrom(Channel.SKY_ARTS_2))
            .put(2134, channelFrom(Channel.HOME,"Home"))
            .put(2135, channelFrom(Channel.HOME_PLUS1))
            .put(2136, channelFrom(Channel.GOOD_FOOD))
            .put(2137, channelFrom(Channel.GOOD_FOOD_PLUS1))
            .put(2139, channelFrom(Channel.SKY1_HD,"Sky1 HD"))
            .put(2142, channelFrom(Channel.ESPN))
            .put(2143, channelFrom(Channel.BIO_HD))
            .put(2144, channelFrom(Channel.CRIME_AND_INVESTIGATION_HD,"Crime & Investigation HD"))
            .put(2145, channelFrom(Channel.DISCOVERY_HD))
            .put(2146, channelFrom(Channel.DISNEY_CINEMAGIC_HD))
            .put(2147, channelFrom(Channel.ESPN_HD))
            .put(2148, channelFrom(Channel.EUROSPORT_HD))
            .put(2149, channelFrom(Channel.FX_HD,"FX HD"))
            .put(2150, channelFrom(Channel.MTVN_HD,"MTVN HD"))
            .put(2151, channelFrom(Channel.NATIONAL_GEOGRAPHIC_HD))
            .put(2152, channelFrom(Channel.NAT_GEO_WILD_HD))
            .put(2154, channelFrom(Channel.SKY_ARTS_1_HD))
            .put(2155, channelFrom(Channel.SKY_ARTS_2_HD))
            .put(2157, channelFrom(Channel.SKY_MOVIES_COMEDY_HD))
            .put(2159, channelFrom(Channel.SKY_MOVIES_FAMILY_HD))
            .put(2160, channelFrom(Channel.SKY_MOVIES_MODERN_GREATS_HD))
            .put(2161, channelFrom(Channel.SKY_MOVIES_SCIFI_HORROR_HD, "Sky Movies Sci-Fi/Horror HD"))
            .put(2162, channelFrom(IGNORED,"Sky Real Lives HD"))
            .put(2164, channelFrom(Channel.BBC_WORLD_NEWS))
            .put(2165, channelFrom(Channel.CRIME_AND_INVESTIGATION, "Crime & Investigation"))
            .put(2168, channelFrom(Channel.SKY_MOVIES_SCIFI_HORROR, "Sky Movies Sci-Fi/Horror"))
            .put(2169, channelFrom(Channel.Channel_ONE_PLUS1))
            .put(2173, channelFrom(Channel.RUSH_HD))
            .put(2174, channelFrom(Channel.SKY_SPORTS_1_HD))
            .put(2175, channelFrom(Channel.SKY_SPORTS_2_HD))
            .put(2176, channelFrom(Channel.SKY_SPORTS_3_HD))
            .put(2177, channelFrom(Channel.CBS_DRAMA))
            .put(2178, channelFrom(Channel.CBS_ACTION))
            .put(2179, channelFrom(Channel.QUEST, "Quest"))
            .put(2181, channelFrom(Channel.CINEMOI,"Cinemoi"))
            .put(2184, channelFrom(Channel.VIVA,"Viva"))
            .put(2185, channelFrom(Channel.FOOD_NETWORK))
            .put(2186, channelFrom(Channel.FOOD_NETWORK_PLUS1))
            .put(2189, channelFrom(Channel.SKY_LIVINGIT))
            .put(2190, channelFrom(Channel.LIVINGIT_PLUS1))
            .put(2192, channelFrom(Channel.TRUE_MOVIES_2))
            .put(2195, channelFrom(Channel.E4_HD))
            .put(2196, channelFrom(Channel.MGM))
            .put(2197, channelFrom(Channel.CHRISTMAS24, "Christmas 24"))
            .put(2200, channelFrom(Channel.SKY_SPORTS_4))
            .put(2203, channelFrom(Channel.STV_NORTH, "STV North"))
            .put(2204, channelFrom(Channel.SKY_MOVIES_ACTION_AND_ADVENTURE, "Sky Movies Action & Adventure"))
            .put(2205, channelFrom(Channel.SKY_MOVIES_ACTION_AND_ADVENTURE_HD,"Sky Movies Action & Adventure HD"))
            .put(2206, channelFrom(Channel.SKY_MOVIES_CRIME_AND_THRILLER, "Sky Movies Crime & Thriller"))
            .put(2207, channelFrom(Channel.SKY_MOVIES_CRIME_AND_THRILLER_HD, "Sky Movies Crime & Thriller HD"))
            .put(2208, channelFrom(Channel.SKY_MOVIES_DRAMA_AND_ROMANCE, "Sky Movies Drama & Romance"))
            .put(2209, channelFrom(Channel.SKY_MOVIES_DRAMA_AND_ROMANCE_HD, "Sky Movies Drama & Romance HD"))
            .put(2210, channelFrom(Channel.SKY_MOVIES_CHRISTMAS_CHANNEL))
            .put(2211, channelFrom(Channel.SKY_MOVIES_CHRISTMAS_CHANNEL_HD))
            .put(2212, channelFrom(Channel.SYFY,"Syfy"))
            .put(2213, channelFrom(Channel.SYFY_PLUS1))
            .put(2214, channelFrom(Channel.SYFY_HD,"Syfy HD"))
            .put(2217, channelFrom(Channel.SKY_NEWS_HD))
            .put(2219, channelFrom(Channel.STV_HD,"STV HD"))
            .put(2240, channelFrom(Channel.BBC_ONE_EAST_MIDLANDS,"BBC1 East Midlands"))
            .put(2241, channelFrom(Channel.BBC_TWO,"BBC2 East Midlands"))
            .put(2244, channelFrom(Channel.BET_INTERNATIONAL))
            .put(2246, channelFrom(Channel.PHOENIX_CNE, "Phoenix CNE"))
            .put(2249, channelFrom(IGNORED, "Rai Uno"))
            .put(2250, channelFrom(IGNORED, "Health"))
            .put(2251, channelFrom(Channel.SIMPLY_SHOPPING))
            .put(2252, channelFrom(IGNORED,"Music Choice Blues"))
            .put(2253, channelFrom(IGNORED,"Music Choice Classical"))
            .put(2254, channelFrom(IGNORED,"Music Choice Country"))
            .put(2255, channelFrom(IGNORED,"Music Choice Dance"))
            .put(2256, channelFrom(IGNORED,"Music Choice Easy Listening"))
            .put(2257, channelFrom(IGNORED,"Music Choice Gold"))
            .put(2258, channelFrom(IGNORED,"Music Choice Hits"))
            .put(2259, channelFrom(IGNORED,"Music Choice Jazz"))
            .put(2260, channelFrom(IGNORED,"Music Choice Love"))
            .put(2261, channelFrom(IGNORED,"Music Choice Rock"))
            .put(2264, channelFrom(IGNORED,"Sat 1"))
            .put(2331, channelFrom(Channel.GMTV_DIGITAL))
            .put(2421, channelFrom(IGNORED,"Asia 1"))
            .put(2422, channelFrom(IGNORED,"The Pakistani Channel"))
            .put(2427, channelFrom(Channel.S4C2))
            .put(2429, channelFrom(Channel.CHANNEL_9))
            .put(2439, channelFrom(Channel.CHART_SHOW_TV))
            .put(2457, channelFrom(Channel.FLAUNT,"Flaunt"))
            .put(2476, channelFrom(Channel.THE_HORROR_CHANNEL))
            .put(2479, channelFrom(Channel.SETANTA_IRELAND))
            .put(2487, channelFrom(Channel.M95_TV_MARBELLA))
            .put(2492, channelFrom(IGNORED,"City Channel"))
            .put(2502, channelFrom(Channel.THREE_E,"3e"))
            .put(2505, channelFrom(Channel.FILMFLEX,"FilmFlex"))
            .put(2506, channelFrom(IGNORED, "Sky Movies Box Office HD2"))
            .put(2507, channelFrom(IGNORED, "Sky Movies Box Office HD1"))
            .put(2511, channelFrom(Channel.ITV3_PLUS1))
            .put(2514, channelFrom(Channel.ITV1_THAMES_VALLEY_NORTH))
            .put(2515, channelFrom(Channel.ITV1_THAMES_VALLEY_SOUTH))
            .put(2518, channelFrom(Channel.E_EUROPE,"E! Europe"))
            .put(2526, channelFrom(Channel.HISTORY_HD))
            .put(2527, channelFrom(Channel.BEST_DIRECT))
            .put(2528, channelFrom(Channel.GEMS_TV))
            .put(2529, channelFrom(Channel.GEM_COLLECTOR))
            .put(2530, channelFrom(Channel.DEUTSCHE_WELLE))
            .put(2531, channelFrom(IGNORED, "Channel S"))
            .put(2533, channelFrom(Channel.SETANTA_SPORTS_1_IRELAND))
            .put(2537, channelFrom(Channel.DIVA))
            .put(2542, channelFrom(Channel.PLAYHOUSE_DISNEY_PLUS,"Playhouse Disney+"))
            .put(2543, channelFrom(Channel.TINY_POP))
            .put(2544, channelFrom(Channel.POP, "Pop"))
            .put(2545, channelFrom(Channel.DMAX))
            .put(2547, channelFrom(Channel.MTV_PLUS1))
            .put(2549, channelFrom(Channel.HORSE_AND_COUNTRY,"Horse & Country"))
            .put(2550, channelFrom(Channel.Channel_7))
            .put(2551, channelFrom(Channel.FLAVA))
            .put(2552, channelFrom(Channel.SKY_MOVIES_PREMIERE_HD))
            .put(2553, channelFrom(IGNORED,"ABC News Now"))
            .put(2557, channelFrom(Channel.NATIONAL_GEOGRAPHIC_HD_PAN_EUROPEAN))
            .put(2558, channelFrom(Channel.MOVIES4MEN2,"Movies4Men2"))
            .put(2559, channelFrom(Channel.MILITARY_HISTORY))
            .put(2561, channelFrom(Channel.THE_HORROR_CHANNEL_PLUS1))
            .put(2563, channelFrom(Channel.THE_STYLE_NETWORK))
            .put(2566, channelFrom(Channel.WEDDING_TV))
            .put(2568, channelFrom(Channel.ITV4_PLUS1))
            .put(2569, channelFrom(Channel.SUPER_CASINO))
            .put(2570, channelFrom(Channel.INVESTIGATION_DISCOVERY))
            .put(2572, channelFrom(IGNORED,"Rocks & Co"))
            .put(2574, channelFrom(Channel.DISCOVERY_TRAVEL_AND_LIVING_PLUS1,"Discovery Travel & Living +1"))
            .put(2575, channelFrom(IGNORED,"Sumo TV"))
            .put(2577, channelFrom(Channel.FIVE_USA_PLUS1,"5USA +1"))
            .put(2578, channelFrom(Channel.FIVER_PLUS1, "5* +1"))
            .put(2583, channelFrom(Channel.CRIME_AND_INVESTIGATION_PLUS1, "Crime & Investigation +1"))
            .put(2585, channelFrom(Channel.LIVING_HD,"Sky Living HD")) //AKA Sky Living HD
            .put(2586, channelFrom(Channel.CBS_REALITY))
            .put(2587, channelFrom(Channel.SKY_MOVIES_INDIE_HD))
            .put(2588, channelFrom(Channel.DISCOVERY_QUEST_PLUS1))
            .put(2589, channelFrom(Channel.DIVA_PLUS1))
            .put(2590, channelFrom(Channel.CHRISTMAS24_PLUS, "Christmas 24+"))
            .put(2591, channelFrom(Channel.UNIVERSAL_PLUS1))
            .put(2592, channelFrom(Channel.MTV_SHOWS))
            .put(2593, channelFrom(Channel.NICK_JR_2))
            .put(2594, channelFrom(Channel.NHK_WORLD))
            .put(2595, channelFrom(Channel.DMAX_PLUS1))
            .put(2596, channelFrom(Channel.COMEDY_CENTRAL_EXTRA_PLUS1))
            .put(2597, channelFrom(Channel.BET_PLUS1))
            .put(2598, channelFrom(Channel.MOVIES4MEN_PLUS1,"Movies4Men +1"))
            .put(2599, channelFrom(Channel.MOVIES4MEN2_PLUS1,"Movies4Men2 +1"))
            .put(2600, channelFrom(Channel.DISCOVERY_SCIENCE_PLUS1))
            .put(2601, channelFrom(IGNORED,"Discovery Knowledge +1"))
            .put(2602, channelFrom(Channel.NICKTOONS_REPLAY))
            .put(2603, channelFrom(Channel.TRUEENT,"TrueEnt"))
            .put(2604, channelFrom(Channel.BODY_IN_BALANCE, "Body in Balance"))
            .put(2605, channelFrom(Channel.THE_ACTIVE_CHANNEL))
            .put(2606, channelFrom(Channel.SKY_3D))
            .put(2607, channelFrom(Channel.SKY_SPORTS_4_HD))
            .put(2610, channelFrom(Channel.BBC_SPORT_INTERACTIVE_BBC_TWO,"BBC Sport Interactive: BBC2"))
            .put(2612, channelFrom(Channel.BBC_SPORT_INTERACTIVE_BBC_ONE,"BBC Sport Interactive: BBC1"))
            .put(2613, channelFrom(Channel.BBC_SPORT_INTERACTIVE_BBC_THREE,"BBC Sport Interactive: BBC3"))
            .put(2615, channelFrom(Channel.FIVE_HD,"Channel 5 HD"))
            .put(2617, channelFrom(Channel.FILM4_HD,"Film4 HD"))
            .put(2618, channelFrom(IGNORED,"Blighty +1"))
            .put(2619, channelFrom(Channel.DISCOVERY_PLUS1_POINT5,"Discovery +1.5"))
            .put(2622, channelFrom(IGNORED,"Showcase"))
            .put(2624, channelFrom(Channel.S4C_CLIRLUN))
            .put(2625, channelFrom(IGNORED,"Asianet"))
            .put(2636, channelFrom(IGNORED,"Multi Channel"))
            .put(2637, channelFrom(IGNORED,"Music Choice Europe"))
            .put(2638, channelFrom(Channel.FITNESS_TV))
            .put(2639, channelFrom(Channel.Q))
            .put(2640, channelFrom(Channel.PRICE_DROP_TV,"Price-drop.tv"))
            .put(2642, channelFrom(Channel.TV3,"TV3"))
            .put(2645, channelFrom(Channel.MGM_HD,"MGM HD"))
            .put(2646, channelFrom(Channel.COMEDY_CENTRAL_HD))
            .put(2647, channelFrom(Channel.SKY_3_PLUS1))//Pick TV +1
            .put(2661, channelFrom(Channel.ITV2_HD))
            .put(2662, channelFrom(Channel.EDEN_HD))
            .put(2663, channelFrom(Channel.QUEST_FREEVIEW,"Quest (Freeview)"))
            .put(2667, channelFrom(Channel.BBC_ONE_HD,"BBC1 HD"))
            .put(2668, channelFrom(Channel.DISCOVERY_HISTORY))
            .put(2672, channelFrom(Channel.DISCOVERY_HISTORY_PLUS_1))
            .put(2676, channelFrom(Channel.ITV1_GRANADA_PLUS1))
            .put(2677, channelFrom(Channel.ITV1_UTV_PLUS1))
            .put(2678, channelFrom(Channel.ITV1_CENTRAL_PLUS1))
            .put(2679, channelFrom(Channel.ITV1_WEST_PLUS1))
            .put(2680, channelFrom(Channel.ITV1_SOUTH_EAST_PLUS1))
            .put(2681, channelFrom(IGNORED, "ITV1 STV +1"))
            .put(2682, channelFrom(Channel.ITV1_LONDON_PLUS1))
            .put(2683, channelFrom(Channel.ITV1_YORKSHIRE_TYNE_TEES_PLUS1))
            .put(2684, channelFrom(Channel.SKY_ATLANTIC_HD))
            .put(2685, channelFrom(Channel.SKY_ATLANTIC))
            .put(2686, channelFrom(Channel.SKY_LIVING_LOVES))
            .put(2696, channelFrom(IGNORED, "Sony Entertainment TV (Plus 1)"))
            .put(2697, channelFrom(IGNORED, "Sony Entertainment Television"))
        .build();

}
