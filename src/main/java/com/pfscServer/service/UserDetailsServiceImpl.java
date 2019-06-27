package com.pfscServer.service;

import com.pfscServer.domain.ApplicationUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.pfscServer.repo.ApplicationUserRepository;

import static java.util.Collections.emptyList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
    
    public ApplicationUser getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return applicationUserRepository.findByUsername(username);
    }
}
