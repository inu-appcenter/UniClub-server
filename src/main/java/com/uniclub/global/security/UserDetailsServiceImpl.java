package com.uniclub.global.security;

import com.uniclub.domain.user.entity.User;
import com.uniclub.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        User user = userRepository.findByStudentNumber(studentId).orElseThrow(() -> new UsernameNotFoundException(studentId));
        return UserDetailsImpl.of(user);
    }
}
