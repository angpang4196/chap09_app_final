package org.edupoll.model.dto;

import org.edupoll.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {

	private Long id;
	private String name;
	private String email;
	private String profileImage;

	public UserWrapper(User entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.email = entity.getEmail();
		this.profileImage = entity.getProfileImage();
	}

}
