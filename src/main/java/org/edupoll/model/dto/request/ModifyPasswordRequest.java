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
public class ModifyPasswordRequest {

	@NotBlank
	private String oldPassword;

	@NotBlank
	private String newPassword;

}
