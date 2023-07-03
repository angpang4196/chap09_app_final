package org.edupoll.controller;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.KakaoAccessTokenWrapper;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.request.KakaoAuthorizedCallbackRequest;
import org.edupoll.model.dto.request.ValidateKakaoRequest;
import org.edupoll.model.dto.response.OAuthSignResponse;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.hibernate.annotations.CollectionTypeRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@CrossOrigin
public class OAuthController {

	private final KakaoAPIService kakaoAPIService;
	private final JWTService jwtService;
	private final UserService userService;

	@Value("${kakao.restapi.key}")
	String kakaoRestAPIKey;

	@Value("${kakao.redirect.url}")
	String redirectURL;

	// 카카오 인증 요청시 인증해야될 주소 알려주는 API
	@GetMapping("/kakao")
	public ResponseEntity<OAuthSignResponse> oauthKakaoHandle() {
		var response = new OAuthSignResponse(200,
				"https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoRestAPIKey
						+ "&redirect_uri=" + redirectURL);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 카카오 인증 코드로 사용자 정보를 얻어내는 API
	@PostMapping("/kakao")
	public ResponseEntity<ValidateUserResponse> oauthKakaoPostHandle(ValidateKakaoRequest req)
			throws JsonMappingException, JsonProcessingException, ExistUserEmailException, VerifyCodeException {

		KakaoAccessTokenWrapper wrapper = kakaoAPIService.getAccessToken(req.getCode());

		KakaoAccount account = kakaoAPIService.getUserInfo(wrapper.getAccessToken());
		userService.updateKakaoUser(account, wrapper.getAccessToken());

		log.info("kakao account = {}", account.toString());

		String token = jwtService.createToken(account.getEmail());

//		userService.joinSocial(account, token);

		ValidateUserResponse response = new ValidateUserResponse(200, token, account.getEmail());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 백에선 필요 없음.
	// 카카오 로그인 후 코드를 받는 곳 (보통 프론트에서 받지만 프론트 앱이 없어서 백에서 일단 받음)
	// 받은 코드 값을 백으로 전달 해 주는 방식으로 바뀌게 됨.
	@GetMapping("/kakao/callback")
	public ResponseEntity<Void> oauthKakaoCallbackHandle(KakaoAuthorizedCallbackRequest req) {
		log.info("code = {}", req.getCode());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
