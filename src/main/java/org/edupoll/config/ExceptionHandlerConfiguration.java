package org.edupoll.config;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.UnauthenticationException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;

@ControllerAdvice
public class ExceptionHandlerConfiguration {

	// @Valid 설정했는데 유효성 검사에서 걸렸을 때
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Void> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {

		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
	}

	// 로그인시 해당하는 유저 이메일이 없을 때(상태코드 400)
	@ExceptionHandler(NotExistUserException.class)
	public ResponseEntity<ErrorResponse> notExistUserExceptionHandle(NotExistUserException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
	}

	// (상태코드 400)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> runtimeExceptionHandle(RuntimeException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
	}

	// 회원 가입시 이미 중복된 데이터가 존재(상태코드 409)
	@ExceptionHandler(ExistUserEmailException.class)
	public ResponseEntity<ErrorResponse> existUserEmailExceptionHandle(ExistUserEmailException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.CONFLICT);
	}

	// 유효하지 않은 데이터(상태코드 401)
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResponse> invalidDataExceptionHandle(InvalidPasswordException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.UNAUTHORIZED);
	}

	// 회원가입시 이메일 인증이 안 되어있을 때(상태코드 401)
	@ExceptionHandler(UnauthenticationException.class)
	public ResponseEntity<ErrorResponse> UnauthenticationExceptionHandle(UnauthenticationException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.UNAUTHORIZED);
	}

	// 이메일 인증시 오류가 발생했을 때(상태코드 400)
	@ExceptionHandler(VerifyCodeException.class)
	public ResponseEntity<ErrorResponse> verifyCodeExceptionHandle(VerifyCodeException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
	}

	// 이미 인증을 받았을 때는 인증코드를 못 받게 하기 위해서(상태코드 400)
	@ExceptionHandler(AlreadyVerifiedException.class)
	public ResponseEntity<ErrorResponse> alreadyVerifiedExceptionHandle(AlreadyVerifiedException e) {
		ErrorResponse er = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
	}

	// JWT Token 이 만료되었거나 변조되었을 때(상태코드 401)
	@ExceptionHandler({ JWTDecodeException.class, TokenExpiredException.class })
	public ResponseEntity<ErrorResponse> jwtTokenExceptionHanler(Exception e) {
		ErrorResponse er = new ErrorResponse("인증토큰이 만료되었거나 손상되었습니다.", System.currentTimeMillis());

		return new ResponseEntity<ErrorResponse>(er, HttpStatus.UNAUTHORIZED);
	}

}
