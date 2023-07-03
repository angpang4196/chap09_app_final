package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyCodeRequest {
	
	@Email
	String email;
	
	@NotBlank
	String code;

}
