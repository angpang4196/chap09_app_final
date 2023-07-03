package org.edupoll.service;

import java.net.URI;

import org.edupoll.entity.User;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.KakaoAccessTokenWrapper;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
 * Spring Framework 에서 REST API 를 호출하는 걸 도와주기 위해서
 * 
 * RestTemplate - 동기 (blocking IO) / WebClient - 비동기 (Non-blocking IO)
 */

@Slf4j
@Service
public class KakaoAPIService {

	@Value("${kakao.restapi.key}")
	String kakaoRestAPIKey;

	@Value("${kakao.redirect.url}")
	String redirectURL;
	
	@Autowired
	UserRepository userRepository;

	// 콜백으로 받은 인증코드를 이용해서 카카오에서 유저정보를 받아와야 한다.
	public KakaoAccessTokenWrapper getAccessToken(String code) {
		var tokenURL = "https://kauth.kakao.com/oauth/token";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		var body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoRestAPIKey);
		body.add("redirect_uri", redirectURL);
		body.add("code", code);

		HttpEntity<MultiValueMap> entity = new HttpEntity<>(body, headers);

		RestTemplate template = new RestTemplate();
		ResponseEntity<KakaoAccessTokenWrapper> result = template.postForEntity(tokenURL, entity,
				KakaoAccessTokenWrapper.class);

		log.info("getToken = {} : {} ", result.getStatusCode(), result.getBody());
		log.info("body.accessToken = {}", result.getBody().getAccessToken());

		return result.getBody();
	}

	// accessToken 을 가지고 실제 카카오 사용자의 정보를 얻어와야 한다.
	public KakaoAccount getUserInfo(String accessToken) throws JsonMappingException, JsonProcessingException {

		var targetURL = "https://kapi.kakao.com/v2/user/me";

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RestTemplate template = new RestTemplate();

		RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(targetURL));
		
		ResponseEntity<String> response = template.exchange(request, String.class);

		log.info("response.statusCode = {}", response.getStatusCode());
		log.info("response.body = {}", response.getBody());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());

		String nickname = node.get("properties").get("nickname").asText();

		String profileImage = node.get("kakao_account").get("profile").get("profile_image_url").asText();

		String email = node.get("id").asText() + "@kakao.user";

		return new KakaoAccount(nickname, profileImage, email);
	}

	// accessToken 을 통해서 kakao에 unlink api 호출
	public String deleteSpecificUser(String email) throws JsonMappingException, JsonProcessingException, NotExistUserException {
		var targetURL = "https://kapi.kakao.com/v1/user/unlink";
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new NotExistUserException("계정이 존재하지 않습니다."));
		String accessToken = user.getSocial();
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RestTemplate template = new RestTemplate();

		RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.POST, URI.create(targetURL));
		
		ResponseEntity<String> response = template.exchange(request, String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());

		String id = node.get("id").asText();
		
		userRepository.delete(user);
		
		return id;
	}

}
