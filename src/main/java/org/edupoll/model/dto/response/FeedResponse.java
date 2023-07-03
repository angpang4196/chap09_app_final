package org.edupoll.model.dto.response;

import java.util.List;

import org.edupoll.model.dto.FeedAttachWrapper;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.UserWrapper;

import lombok.Data;

@Data
public class FeedResponse {

	private Long id;
	private String description;
	private Long viewCount;

	private UserWrapper writer;
	private List<FeedAttachWrapper> attaches;

	public FeedResponse(FeedWrapper wrapper) {
		this.id = wrapper.getId();
		this.description = wrapper.getDescription();
		this.viewCount = wrapper.getViewCount();

		this.writer = wrapper.getWriter();
		this.attaches = wrapper.getAttaches();
	}

}
