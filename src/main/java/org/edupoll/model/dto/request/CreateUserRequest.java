package org.edupoll.model.dto.request;

import org.edupoll.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateUserRequest {

	@NotBlank
	private String email;
	
	private String password;
	
	@NotBlank
	private String name;
	
	private String profileImage;
	private String social;

	public User toEntity() {
		return new User(email, password, name, profileImage, social);
	}

}
