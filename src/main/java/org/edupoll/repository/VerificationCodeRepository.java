package org.edupoll.repository;

import java.util.Optional;

import org.edupoll.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{

	public Optional<VerificationCode> findByEmailOrderByCreatedDesc(String email);

	public boolean existsByEmail(String email);

	public void deleteByEmail(String email);

}
