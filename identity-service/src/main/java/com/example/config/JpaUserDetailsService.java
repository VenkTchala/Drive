package com.example.config;

import com.example.repository.DriveUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class JpaUserDetailsService implements UserDetailsService
{
    DriveUserRepository userRepository;
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.getDriveUserByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserName :" + email + " not Found"));
    }
}

