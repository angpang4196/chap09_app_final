package org.edupoll.controller;

import java.net.MalformedURLException;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// @RestController
// @RequestMapping("/resource")
@Slf4j
@RequiredArgsConstructor
public class ResourceController {

	private final UserService userService;

	// 특정경로로 왔을때 이미지를 보내주는
	@GetMapping("/profile/{fileName}")
	public ResponseEntity<?> getResourceHandle(HttpServletRequest request)
			throws NotExistUserException, MalformedURLException {
		
		Resource resource = userService.loadResource(request.getRequestURL().toString());
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("content-type", "image/png");
		
		ResponseEntity<Resource> response = new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);

		return response;
	}

}
