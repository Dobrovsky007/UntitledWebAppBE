package com.webapp.Eventified.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.id.SportUserId;

public interface SportUserRepository extends JpaRepository<SportUser, SportUserId> {
}
