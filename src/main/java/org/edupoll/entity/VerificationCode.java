package org.edupoll.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "verificationCodes")
public class VerificationCode {

	@Id
	@GeneratedValue
	private Long id;

	private String code; // 인증 코드
	private String email; // 인증 코드를 발급시킨 이메일

	private Date created; // 발급된 날짜

	private String state; // 통과 여부

	public VerificationCode(String email, String code, String state, Date created) {
		this.email = email;
		this.code = code;
		this.state = state;
		this.created = created;
	}

}
