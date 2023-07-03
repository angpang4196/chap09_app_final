package org.edupoll.service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.edupoll.entity.VerificationCode;
import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.model.dto.request.VerifyEmailRequest;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final UserRepository userRepository;

	private final JavaMailSender mailSender;

	private final VerificationCodeRepository verificationCodeRepository;

//	public void sendTestSimpleMail(MailTestRequest dto) {
//		SimpleMailMessage message = new SimpleMailMessage();
//
//		message.setFrom("edupoll@gmail.com"); // 보내는 쪽
//		message.setTo(dto.getEmail()); // 받는 쪽
//		message.setSubject("메일 테스트"); // 메일 제목
//		message.setText("메일 테스트 중입니다.\n불편을 드려 죄송합니다."); // 본문
//
//		mailSender.send(message);
//	}
//
//	public void sendTestHtmlMail(MailTestRequest dto) throws MessagingException {
//
//		Random random = new Random();
//		int randNum = random.nextInt(1_000_000);
//		String code = String.format("%06d", randNum);
//
//		String htmlText = """
//					<div>
//						<h1>메일테스트중</h1>
//						<p style="color : orange">
//							html 메세지도 <b>전송 가능</b>하다.
//						</p>
//						<a href="http://192.168.4.93:8080/api/v1/user/email/auth" target="_blank">인증하기</a>
//						<p>
//							쿠폰번호 : <i>#code</i>
//						</p>
//					</div>
//				""".replaceAll(("#code"), code);
//
//		MimeMessage message = mailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message);
//
//		helper.setTo(dto.getEmail());
////		helper.setFrom("edupoll@gmail.com");
//		helper.setSubject("메일 테스트-2");
//		helper.setText(htmlText, true);
//
//		mailSender.send(message);
//	}

	@Transactional
	public void sendVerificationCode(VerifyEmailRequest dto) throws MessagingException, AlreadyVerifiedException {
		// 이미 이메일 인증을 받은 유저인지 확인하는 절차
		Optional<VerificationCode> found = verificationCodeRepository.findByEmailOrderByCreatedDesc(dto.getEmail());
		if (found.isPresent() && found.get().getState() != null) {
			throw new AlreadyVerifiedException("이미 이메일 인증받은 기록이 있습니다.");
		}

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		// 인증코드 생성
		Random random = new Random();
		int randNum = random.nextInt(1_000_000);
		String code = String.format("%06d", randNum);

		String email = dto.getEmail();

		var one = new VerificationCode();
		one.setCode(code);
		one.setEmail(email);
		one.setCreated(new Date());

		verificationCodeRepository.save(one);

		String htmlText = """
					<div>
						<h1>회원가을 위한 인증코드 발급</h1>
						<p>
							인증코드 : <i>#code</i>
						</p>
					</div>
				""".replaceAll(("#code"), code);

		helper.setTo(dto.getEmail());
		helper.setFrom("edupoll@gmail.com");
		helper.setSubject("메일 테스트-2");
		helper.setText(htmlText, true);

		mailSender.send(message);
	}

}
