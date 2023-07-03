package org.edupoll.model.dto.request;

import java.util.List;

import org.edupoll.entity.Feed;
import org.edupoll.entity.User;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateFeedRequest {

	private String description; // 본문 내용
	private List<MultipartFile> attaches; // 전달받은 파일들
	
}
