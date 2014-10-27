package org.atlasapi.feeds.tvanytime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import tva.metadata._2010.FlagType;


public class TVAnytimeElementFactory {
    
    private final DatatypeFactory datatypeFactory;

    public TVAnytimeElementFactory() throws DatatypeConfigurationException {
        this.datatypeFactory = DatatypeFactory.newInstance();
    }
    
    public FlagType flag(boolean value) {
        FlagType flag = new FlagType();
        flag.setValue(value);
        return flag;
    }
    
    public XMLGregorianCalendar gregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
    }
    
    public javax.xml.datatype.Duration durationFrom(Duration duration) {
        return datatypeFactory.newDurationDayTime(duration.getMillis());
    }
}
