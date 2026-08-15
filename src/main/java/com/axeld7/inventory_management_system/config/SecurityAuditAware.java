package com.axeld7.inventory_management_system.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.axeld7.inventory_management_system.user.User;

@Component("securityAuditAware")
public class SecurityAuditAware implements AuditorAware<Long>{

    @Override
    public Optional<Long> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal()) ) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof User user) {
            return Optional.of(user.getId());
        }

        return Optional.empty();

    }

}
