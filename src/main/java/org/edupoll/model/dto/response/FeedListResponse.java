package org.edupoll.model.dto.response;

import java.util.List;

import org.edupoll.model.dto.FeedWrapper;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FeedListResponse {

	private long total;
	private List<FeedWrapper> feeds;

	public FeedListResponse(long total, List<FeedWrapper> feeds) {
		this.feeds = feeds;
		this.total = total;
	}

}
