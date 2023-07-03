package org.edupoll.model.dto.response;

import org.edupoll.entity.User;

public class UserResponseData {

	long id;
	String email;
	String password;
	String name;
	String profileImage;
	String social;

	public UserResponseData() {
	}

	public UserResponseData(User entity) {
		super();
		this.id = entity.getId();
		this.email = entity.getEmail();
		this.password = entity.getPassword();
		this.name = entity.getName();
		this.profileImage = entity.getProfileImage();
		this.social = entity.getSocial();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getSocial() {
		return social;
	}

	public void setSocial(String social) {
		this.social = social;
	}

}
