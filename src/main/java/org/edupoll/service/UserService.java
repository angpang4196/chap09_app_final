package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Optional;

import org.edupoll.entity.ProfileImage;
import org.edupoll.entity.User;
import org.edupoll.entity.VerificationCode;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.UnauthenticationException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.CreateUserRequest;
import org.edupoll.model.dto.request.ModifyPasswordRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VerifyCodeRequest;
import org.edupoll.model.dto.request.VerifyEmailRequest;
import org.edupoll.model.dto.response.UserResponseData;
import org.edupoll.repository.ProfileImageRepository;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	VerificationCodeRepository verificationCodeRepository;

	@Autowired
	ProfileImageRepository profileImageRepository;

	@Autowired
	JavaMailSender mailSender;

	@Value("${jwt.secret.key}")
	String secretKey;

	@Value("${upload.basedir}")
	String baseDir;

	@Value("${upload.server}")
	String uploadServer;

	@Transactional
	public UserResponseData registerNewUser(CreateUserRequest dto)
			throws ExistUserEmailException, UnauthenticationException, VerifyCodeException {
//		User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new ExistUserEmailException("[Email]" + dto.getEmail() + " 에 해당하는 아이디가 이미 존재합니다.");
		}

		// 이메일을 기준으로 찾을때 인증코드 정보가 없을 때
		VerificationCode found = verificationCodeRepository.findByEmailOrderByCreatedDesc(dto.getEmail())
				.orElseThrow(() -> new VerifyCodeException("이메일 인증코드 오류 발생"));

		if (found.getState() == null) {
			throw new VerifyCodeException("회원가입을 위해선 이메일 인증이 필요합니다.");
		}

		User reqUser = dto.toEntity();
		User saved = userRepository.save(reqUser);

		return new UserResponseData(saved);
	}

	@Transactional
	public UserResponseData validateUser(ValidateUserRequest dto)
			throws NotExistUserException, InvalidPasswordException {
		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new NotExistUserException("계정이 존재하지 않습니다."));

		if (user.getPassword().equals(dto.getPassword())) {
			return new UserResponseData(user);
		} else {
			throw new InvalidPasswordException("[Email]" + dto.getEmail() + "의 비밀번호가 일치하지 않습니다.");
		}
	}

	@Transactional
	public void verifySpecificCode(VerifyCodeRequest dto) throws VerifyCodeException {
		Optional<VerificationCode> result = verificationCodeRepository.findByEmailOrderByCreatedDesc(dto.getEmail());

		VerificationCode found = result.orElseThrow(() -> new VerifyCodeException("발급받은 이메일 인증코드가 없습니다."));
		long elapsed = System.currentTimeMillis() - found.getCreated().getTime();

		// 인증코드 발급 유효시간 : 10분을 넘겼을 때
		if (elapsed > 1000 * 60 * 10) {
			throw new VerifyCodeException("이메일 인증코드 유효시간이 만료되었습니다.");
		}

		// 인증코드 불일치
		if (!found.getCode().equals(dto.getCode())) {
			throw new VerifyCodeException("이메일 인증코드가 일치하지 않습니다.");
		}

		found.setState("Y");

		verificationCodeRepository.save(found);
	}

	@Transactional
	public void modifyPassword(ModifyPasswordRequest dto, String email) throws InvalidPasswordException {
		User found = userRepository.findByEmail(email).get();

		// 원래 비밀번호가 일치하지 않을 떄
		if (!dto.getOldPassword().equals(found.getPassword())) {
			throw new InvalidPasswordException("[Email]" + email + "의 비밀번호가 일치하지 않습니다.");
		}

		found.setPassword(dto.getNewPassword());
		userRepository.save(found);
	}

	@Transactional
	public void deleteSpecificUser(String email, String password)
			throws InvalidPasswordException, NotExistUserException {

		User found = userRepository.findByEmail(email).orElseThrow(() -> new NotExistUserException("계정이 존재하지 않습니다."));

		if (!password.equals(found.getPassword())) {
			throw new InvalidPasswordException("[Email]" + email + "의 비밀번호가 일치하지 않습니다.");
		}

		userRepository.deleteByEmail(email);
		verificationCodeRepository.deleteByEmail(email);
	}

	@Transactional
	public void joinSocial(KakaoAccount account, String token) throws ExistUserEmailException, VerifyCodeException {

		if (userRepository.existsByEmail(account.getEmail())) {
			throw new ExistUserEmailException("[Email]" + account.getEmail() + " 에 해당하는 아이디가 이미 존재합니다.");
		}

		// 이메일을 기준으로 찾을때 인증코드 정보가 없을 때
		VerificationCode found = verificationCodeRepository.findByEmailOrderByCreatedDesc(account.getEmail())
				.orElseThrow(() -> new VerifyCodeException("이메일 인증코드 오류 발생"));

		if (found.getState() == null) {
			throw new VerifyCodeException("회원가입을 위해선 이메일 인증이 필요합니다.");
		}

		CreateUserRequest dto = new CreateUserRequest(account.getEmail(), null, account.getNickname(),
				account.getProfile_image(), "kakao");

		User reqUser = dto.toEntity();

		userRepository.save(reqUser);
	}

	@Transactional
	public void emailAvailableCheck(@Valid VerifyEmailRequest dto) throws ExistUserEmailException {
		boolean rst = userRepository.existsByEmail(dto.getEmail());
		if (rst) {
			throw new ExistUserEmailException();
		}
	}

	public void updateKakaoUser(KakaoAccount account, String accessToken) {
		// 인증코드를 확보한 카카오 유저에 해당하는 정보를 UserRepository에서 찾는데
		Optional<User> _user = userRepository.findByEmail(account.getEmail());
		// 있다면 update - (accessToken)

		if (_user.isPresent()) {
			User saved = _user.get();
			saved.setSocial(accessToken);
			userRepository.save(saved);

			// 없다면 save
		} else {
			User user = new User();
			user.setEmail(account.getEmail());
			user.setName(account.getNickname());
			user.setProfileImage(account.getProfile_image());
			user.setSocial(accessToken);
			userRepository.save(user);
		}

	}

	// 특정 유저 정보 업데이트
	@Transactional
	public void modifySpecificUser(String userEmail, UpdateProfileRequest request)
			throws IOException, NotSupportedException, NotExistUserException {

		log.info("req.name = {}", request.getName());

		User found = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotExistUserException());
		found.setName(request.getName());
		
		if (request.getProfile() != null) {
			log.info("req.profile = {} : {}", request.getProfile().getContentType(),
					request.getProfile().getOriginalFilename());

			MultipartFile multi = request.getProfile();
			if (!multi.getContentType().startsWith("image/")) {
				throw new NotSupportedException("이미지 파일만 설정 가능합니다.");
			}
			// 파일을 옮기는 작업
			// 기본 세이브 경로는 properties 에서 지정
			String emailEncoded = new String(Base64.getEncoder().encode(userEmail.getBytes()));

			File saveDirectory = new File(baseDir + "/profile/" + emailEncoded);
			saveDirectory.mkdirs();

			// 파일 명은 로그인 사용자의 이메일 주소를 활용
			String fileName = System.currentTimeMillis()
					+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));

			log.info("fileName = {}", fileName);

			File dest = new File(saveDirectory, fileName);

			multi.transferTo(dest); // 업로드
			found.setProfileImage(uploadServer + "/resource/profile/" + emailEncoded + "/" + fileName);
		}

		// 파일 정보를 DB에 INSERT
		// ProfileImage entity 를 생성해서 save
//		ProfileImage one = ProfileImage.builder() //
//				.fileAddress(dest.getAbsolutePath()) //
//				.url(uploadServer + "/resource/profile/" + fileName) //
//				.build(); //
//
//		profileImageRepository.save(one);

		userRepository.save(found);
	}

	public Resource loadResource(String url) throws NotExistUserException, MalformedURLException {
		var found = profileImageRepository.findTop1ByUrl(url).orElseThrow(() -> new NotExistUserException());

		return new FileUrlResource(found.getFileAddress());
	}

	public UserWrapper getUserInfo(String email) throws NotExistUserException {
		User found = userRepository.findByEmail(email).orElseThrow(() -> new NotExistUserException());

		return new UserWrapper(found);
	}

}
