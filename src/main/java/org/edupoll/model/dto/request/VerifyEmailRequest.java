package org.edupoll.model.dto.request;

import java.util.Date;

import org.edupoll.entity.VerificationCode;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class VerifyEmailRequest {

	@Email
	String email;

}
