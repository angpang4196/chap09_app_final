package org.edupoll.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoAccount {
	
	private String nickname;
	private String profile_image;
	private String email;

}
