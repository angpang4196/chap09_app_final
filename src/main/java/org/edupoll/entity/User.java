package org.edupoll.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String email;
	String password;
	String name;
	String profileImage;
	String social;

	public User() {
	}

	public User(Long id, String email, String password, String name, String profileImage, String social) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.profileImage = profileImage;
		this.social = social;
	}

	public User(@Email @NotBlank String email, @NotBlank String password, @NotBlank String name, String profileImage,
			@NotBlank String social) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.profileImage = profileImage;
		this.social = social;
	}

	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
