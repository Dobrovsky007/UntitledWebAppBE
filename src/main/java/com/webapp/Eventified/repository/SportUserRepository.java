package com.webapp.Eventified.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.id.SportUserId;

public interface SportUserRepository extends JpaRepository<SportUser, SportUserId> {

    Optional<SportUser> findByUserIdAndSport(UUID id, Integer sport);
    List<SportUser> findUserBySportAndSkillLevel(Integer sport, Integer skillLevel);
}
