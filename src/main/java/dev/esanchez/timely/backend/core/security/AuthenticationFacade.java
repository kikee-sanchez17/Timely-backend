package dev.esanchez.timely.backend.core.security;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public Authentication getAuthentication(){
        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    public String getCurrentUserEmail(){
        return getAuthentication().getName();
    }

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new NotFoundException("User"));
    }

    public Business getCurrentBusiness() {
        User user = getCurrentUser();

        return businessRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Business"));
    }
}
