package org.atlasapi.feeds.interlinking;

import org.joda.time.DateTime;

public class InterlinkEpisode extends InterlinkContent {

    private final String link;
    private final String parentId;
	
	public InterlinkEpisode(String id, Operation operation, Integer index, String link, String parentId) {
		super(id, operation, index);
        this.link = link;
        this.parentId = parentId;
	}
	
	public InterlinkEpisode withTitle(String title) {
		this.title = title;
		return this;
	}

	public InterlinkEpisode withSummary(String summary) {
		this.summary = summary;
		return this;
	}
	
	public InterlinkEpisode withLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
	
	public InterlinkEpisode withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }
	
	public String link() {
	    return link;
  }

	public InterlinkEpisode withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String parentId() {
        return parentId;
    }
}
