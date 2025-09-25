package com.webapp.Eventified.service;

import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
}
