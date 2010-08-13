package org.atlasapi.feeds.interlinking;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class InterlinkBroadcast extends InterlinkBase {

	private DateTime broadcastStart;
	private Duration duration;
	private String service;

	public InterlinkBroadcast(String id, Operation operation) {
		super(id, operation);
	}

	public InterlinkBroadcast withBroadcastStart(DateTime broadcastStart) {
		this.broadcastStart = broadcastStart;
		return this;
	}
	
	public DateTime broadcastStart() {
		return broadcastStart;
	}

	public InterlinkBroadcast withDuration(Duration duration) {
		this.duration = duration;
		return this;
	}
	
	public Duration duration() {
		return duration;
	}
	
	public InterlinkBroadcast withLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
	
	public String service() {
        return service;
    }
	
	public InterlinkBroadcast withService(String service) {
	    this.service = service;
	    return this;
	}
}
