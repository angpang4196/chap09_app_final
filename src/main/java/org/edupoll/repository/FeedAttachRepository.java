package org.edupoll.repository;

import org.edupoll.entity.FeedAttach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedAttachRepository extends JpaRepository<FeedAttach, Long> {

	void deleteByFeedId(String feedId);

}
