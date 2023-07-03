package org.edupoll.model.dto;

import java.util.List;

import org.edupoll.entity.Feed;
import org.edupoll.entity.FeedAttach;
import org.edupoll.entity.User;

import lombok.Data;

@Data
public class FeedWrapper {

	private Long id;

	private UserWrapper writer;

	private String description;

	private Long viewCount;

	private List<FeedAttachWrapper> attaches;

	public FeedWrapper(Feed feed) {
		this.id = feed.getId();
		this.description = feed.getDescription();
		this.viewCount = feed.getViewCount();
		this.writer = new UserWrapper(feed.getWriter());

		this.attaches = feed.getAttaches().stream().map(e -> new FeedAttachWrapper(e)).toList();
	}

}
