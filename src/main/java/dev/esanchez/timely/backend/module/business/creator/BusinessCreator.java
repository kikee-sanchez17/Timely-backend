package dev.esanchez.timely.backend.module.business.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import dev.esanchez.timely.backend.module.categories.Category;
import dev.esanchez.timely.backend.module.categories.CategoryRepository;
import dev.esanchez.timely.backend.module.identity.*;
import dev.esanchez.timely.backend.module.location.CountryTimezone;
import dev.esanchez.timely.backend.module.location.CountryTimezoneId;
import dev.esanchez.timely.backend.module.location.CountryTimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class BusinessCreator {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final CountryTimezoneRepository countryTimezoneRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public void create(CreateBusinessRequest request, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(("User")));

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new NotFoundException(("Category")));

        CountryTimezoneId timezoneId = CountryTimezoneId.builder()
                .countryCode(request.getCountryCode())
                .timezoneId(request.getTimezoneId())
                .build();

        Role role = roleRepository.findByCode("BUSINESS_OWNER").orElseThrow(() -> new NotFoundException("Role"));
        UserRoleId userRoleId = new UserRoleId(user.getUserId(), role.getRoleId());

        CountryTimezone countryTimezone = countryTimezoneRepository.findById(timezoneId)
                .orElseThrow(() -> new NotFoundException("Timezone"));

        if (!userRoleRepository.existsById(userRoleId)) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
        }

        Business business = Business.builder()
                .user(user)
                .name(request.getName())
                .category(category)
                .info(request.getInfo())
                .city(request.getCity())
                .isActive(Boolean.TRUE)
                .countryTimezone(countryTimezone)
                .build();

        businessRepository.save(business);

    }

}
