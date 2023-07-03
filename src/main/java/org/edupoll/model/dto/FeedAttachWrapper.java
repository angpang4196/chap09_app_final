package org.edupoll.model.dto;

import java.util.List;

import org.edupoll.entity.Feed;
import org.edupoll.entity.FeedAttach;
import org.edupoll.entity.User;

import lombok.Data;

@Data
public class FeedAttachWrapper {

	private Long id;

	private String type;

	private String mediaUrl;

	public FeedAttachWrapper(FeedAttach feedAttach) {
		this.id = feedAttach.getId();
		this.type = feedAttach.getType();
		this.mediaUrl = feedAttach.getMediaUrl();
	}

}
