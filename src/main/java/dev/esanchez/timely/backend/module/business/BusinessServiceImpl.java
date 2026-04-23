package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.module.business.creator.BusinessCreator;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessCreator businessCreator;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public String createBusiness(CreateBusinessRequest createBusinessRequest, String email) {
         businessCreator.create(createBusinessRequest, email);
        UserDetails updateUser = userDetailsService.loadUserByUsername(email);
        return jwtService.generateToken(updateUser);

    }
}
