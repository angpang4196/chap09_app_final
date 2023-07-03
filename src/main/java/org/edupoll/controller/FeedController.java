package org.edupoll.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.request.CreateFeedRequest;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.model.dto.response.FeedResponse;
import org.edupoll.service.FeedService;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

	private final FeedService feedService;

	// 전체 글 목록 제공 해 주는 API (전체 이용가능)
	@GetMapping("/storage")
	public ResponseEntity<?> readAllFeedHandle(@RequestParam(defaultValue = "1") int page) {
		Long total = feedService.totalCount();
		List<FeedWrapper> feeds = feedService.allItems(page);

		FeedListResponse response = new FeedListResponse(total, feeds);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 특정 글 자세히 제공 해 주는 API (전체 이용가능)
	@GetMapping("/storage/{feedId}")
	public ResponseEntity<?> readSpecificFeedHandle(@PathVariable String feedId)
			throws NumberFormatException, NotFoundException {
		FeedResponse response = feedService.getSpecificFeed(feedId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 글 등록 해 주는 API (인증필요)
	@PostMapping("/storage")
	public ResponseEntity<?> createNewFeedHandle(@AuthenticationPrincipal String principal, CreateFeedRequest dto)
			throws NotExistUserException, IllegalStateException, IOException {
		boolean r = feedService.create(principal, dto);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// 특정 글 삭제 해 주는 API (인증필요)
	@DeleteMapping("/storage/{feedId}")
	public ResponseEntity<?> deleteSpecificFeedHandle(@AuthenticationPrincipal String principal,
			@PathVariable String feedId) throws NumberFormatException, NotFoundException {

		feedService.delete(feedId, principal);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
