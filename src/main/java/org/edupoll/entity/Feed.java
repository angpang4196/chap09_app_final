package org.edupoll.entity;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FEEDS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id; // 기본 키

	@ManyToOne
	@JoinColumn(name = "writerId")
	private User writer; // 작성자

	private String description; // 본문

	private Long viewCount;

	@OneToMany(mappedBy = "feed")
	private List<FeedAttach> attaches;

	public Feed(Long writerId, String description) {
		this.description = description;
		this.viewCount = (long) 0;
	}

}
