package org.edupoll.controller;

import java.io.IOException;

import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.ModifyPasswordRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.response.LogonUserInfoResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/user/private")
@RequiredArgsConstructor
@CrossOrigin
public class PrivateController {

	private final JWTService jwtService;

	private final UserService userService;

	private final KakaoAPIService kakaoAPIService;

	@GetMapping
	public ResponseEntity<LogonUserInfoResponse> getUserInfo(Authentication authentication)
			throws NotExistUserException {
		log.info("authentication : {}, {}", authentication, authentication.getPrincipal());
		String principal = (String) authentication.getPrincipal();

		UserWrapper data = userService.getUserInfo(principal);
		LogonUserInfoResponse response = new LogonUserInfoResponse(200, data);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 비밀번호 변경
	@PatchMapping("/password")
	public ResponseEntity<Void> modifyPassword(@AuthenticationPrincipal String principal, ModifyPasswordRequest dto)
			throws InvalidPasswordException {

		log.info("email : {}" , principal);
		userService.modifyPassword(dto, principal);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 회원탈퇴
	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal String principal, @RequestParam String password)
			throws InvalidPasswordException, NotExistUserException, JsonMappingException, JsonProcessingException {

		String targetId = "";
		if (password.equals("undefined")) {
			// 소셜로 가입한 유저 삭제하기
			targetId = kakaoAPIService.deleteSpecificUser(principal);
		} else {
			// 자체 관리중인 유저 삭제하기
			userService.deleteSpecificUser(principal, password);
		}

		log.info("삭제된 아이디 : " + targetId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 사용자 상태(프로필 이미지 / 이름) 업데이트 처리할 API
	// 파일 업로드는 컨텐츠 타입이 multipart/form-data 로 들어옴
	// (file 과 text 유형이 섞여있음)
	@PostMapping("/info")
	public ResponseEntity<?> updateProfileHandle(@AuthenticationPrincipal String principal, UpdateProfileRequest request)
			throws IllegalStateException, IOException, NotSupportedException, NotExistUserException {

		userService.modifySpecificUser(principal, request);
		var wrapper = userService.getUserInfo(principal);

		var response = new LogonUserInfoResponse(200, wrapper);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
