package org.edupoll.model.dto.request;

import org.edupoll.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidateUserRequest {

	@NotBlank
	private String email;
	
	@NotBlank
	private String password;

	public User toEntity() {
		return new User(email, password);
	}

}
