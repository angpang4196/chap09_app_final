package org.edupoll.controller;

import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.VerifyCodeException;

import java.util.Base64;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.UnauthenticationException;
import org.edupoll.model.dto.request.CreateUserRequest;
import org.edupoll.model.dto.request.ModifyPasswordRequest;
import org.edupoll.model.dto.request.VerifyEmailRequest;

import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VerifyCodeRequest;
import org.edupoll.model.dto.response.UserResponseData;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.model.dto.response.VerifyEmailResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.MailService;
import org.edupoll.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor // final 로 선언된 값을 주입
public class UserController {

	private final UserService userService;

	private final MailService mailService;

	private final JWTService jwtService;

	@PostMapping("/join")
	public ResponseEntity<UserResponseData> joinUserHandle(@Valid CreateUserRequest dto)
			throws ExistUserEmailException, UnauthenticationException, VerifyCodeException {
		UserResponseData data = userService.registerNewUser(dto);

		return new ResponseEntity<>(data, HttpStatus.CREATED); // 201
	}

	// 이메일 사용 가능한지 아닌지 확인 해 주는 API
	@GetMapping("/available")
	public ResponseEntity<Void> availableHandle(@Valid VerifyEmailRequest dto)
			throws ExistUserEmailException, UnauthenticationException, VerifyCodeException {

		userService.emailAvailableCheck(dto);

		return new ResponseEntity<>(HttpStatus.OK); // 201
	}

	// 관리중인 유저인지 확인해서 토큰을 발급 해 주는 API
	@PostMapping("/validate")
	public ResponseEntity<ValidateUserResponse> validateUserHandle(ValidateUserRequest dto)
			throws NotExistUserException, InvalidPasswordException {
		UserResponseData data = userService.validateUser(dto);

		String token = jwtService.createToken(dto.getEmail());
		var resp = new ValidateUserResponse(200, token, dto.getEmail());
		log.info("token : " + token);

		return new ResponseEntity<>(resp, HttpStatus.OK); // 200
	}

//	@PostMapping("/mail-test")
//	public ResponseEntity<Void> mailTestHandle(MailTestRequest req) throws MessagingException{
//		mailService.sendTestHtmlMail(req);
//		
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

	@PostMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> verifyEmailHandle(VerifyEmailRequest dto)
			throws MessagingException, AlreadyVerifiedException {
		mailService.sendVerificationCode(dto);

		VerifyEmailResponse response = new VerifyEmailResponse(200, "이메일 인증코드가 정상 발급되었습니다.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/verify-email")
	public ResponseEntity<Void> verifySpecificCodeHandle(@Valid VerifyCodeRequest dto) throws VerifyCodeException {
		userService.verifySpecificCode(dto);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
