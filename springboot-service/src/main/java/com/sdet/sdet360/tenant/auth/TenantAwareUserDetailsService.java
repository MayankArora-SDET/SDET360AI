package com.sdet.sdet360.tenant.auth;

import com.sdet.sdet360.config.TenantContextHolder;
import com.sdet.sdet360.tenant.model.User;
import com.sdet.sdet360.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TenantAwareUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException { 
        // Get the current tenant context
        TenantContextHolder.getTenantId();
        
        // Try to find by email first
        List<User> users = userRepository.findByEmail(usernameOrEmail);
        
        // If not found by email, try by username
        if (users.isEmpty()) {
            users = userRepository.findByUsername(usernameOrEmail);
        }
        
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username/email: " + usernameOrEmail);
        }
         
        User user = users.get(0);
        return TenantAwareUserDetails.build(user);
    }
}