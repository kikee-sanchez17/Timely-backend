package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.creator.BusinessCreator;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import dev.esanchez.timely.backend.module.categories.Category;
import dev.esanchez.timely.backend.module.categories.CategoryRepository;
import dev.esanchez.timely.backend.module.identity.*;
import dev.esanchez.timely.backend.module.location.CountryTimezone;
import dev.esanchez.timely.backend.module.location.CountryTimezoneId;
import dev.esanchez.timely.backend.module.location.CountryTimezoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


    @ExtendWith(MockitoExtension.class)
    class BusinessCreatorTest {

        @Mock private UserRepository userRepository;
        @Mock private BusinessRepository businessRepository;
        @Mock private CategoryRepository categoryRepository;
        @Mock private CountryTimezoneRepository countryTimezoneRepository;
        @Mock private UserRoleRepository userRoleRepository;
        @Mock private RoleRepository roleRepository;

        @InjectMocks
        private BusinessCreator businessCreator;

        @Test
        void create_shouldSaveBusiness_whenAllEntitiesExist() {
            String email = "test@email.com";
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );

            User user = User.builder().email(email).userId(1L).build();
            Category category = Category.builder().categoryId(1L).build();
            CountryTimezoneId timezoneId = CountryTimezoneId.builder()
                    .countryCode("ES").timezoneId("Europe/Madrid").build();
            CountryTimezone timezone = CountryTimezone.builder().id(timezoneId).build();
            Role role = Role.builder().roleId(2L).code("BUSINESS_OWNER").build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(countryTimezoneRepository.findById(timezoneId)).thenReturn(Optional.of(timezone));
            when(roleRepository.findByCode("BUSINESS_OWNER")).thenReturn(Optional.of(role));
            when(userRoleRepository.existsById((UserRoleId) any())).thenReturn(false);

            businessCreator.create(request, email);

            verify(userRoleRepository).save(any(UserRole.class));
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);
            verify(businessRepository).save(captor.capture());

            Business savedBusiness = captor.getValue();
            assertEquals("My Business", savedBusiness.getName());
            assertEquals("Barcelona", savedBusiness.getCity());
            assertEquals(user, savedBusiness.getUser());
            assertEquals(category, savedBusiness.getCategory());
        }

        @Test
        void create_shouldNotAssignRole_whenUserAlreadyHasOwnerRole() {
            String email = "test@email.com";
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );

            User user = User.builder().email(email).userId(1L).build();
            Category category = Category.builder().categoryId(1L).build();
            CountryTimezoneId timezoneId = CountryTimezoneId.builder()
                    .countryCode("ES").timezoneId("Europe/Madrid").build();
            CountryTimezone timezone = CountryTimezone.builder().id(timezoneId).build();
            Role role = Role.builder().roleId(2L).code("BUSINESS_OWNER").build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(countryTimezoneRepository.findById(timezoneId)).thenReturn(Optional.of(timezone));
            when(roleRepository.findByCode("BUSINESS_OWNER")).thenReturn(Optional.of(role));
            when(userRoleRepository.existsById((UserRoleId) any())).thenReturn(true); // ya tiene el rol

            businessCreator.create(request, email);

            verify(userRoleRepository, never()).save(any()); // nunca asigna el rol
            verify(businessRepository).save(any(Business.class));
        }

        @Test
        void create_shouldThrow_whenUserNotFound() {
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> businessCreator.create(request, "test@email.com"));
            verify(businessRepository, never()).save(any());
        }

        @Test
        void create_shouldThrow_whenCategoryNotFound() {
            String email = "test@email.com";
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).build()));
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> businessCreator.create(request, email));
            verify(businessRepository, never()).save(any());
        }

        @Test
        void create_shouldThrow_whenTimezoneNotFound() {
            String email = "test@email.com";
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );
            User user = User.builder().email(email).userId(1L).build();
            Role role = Role.builder().roleId(2L).code("BUSINESS_OWNER").build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(Category.builder().categoryId(1L).build()));
            when(roleRepository.findByCode("BUSINESS_OWNER")).thenReturn(Optional.of(role));
            when(countryTimezoneRepository.findById((CountryTimezoneId) any())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> businessCreator.create(request, email));
            verify(businessRepository, never()).save(any());
        }

        @Test
        void create_shouldThrow_whenRoleNotFound() {
            String email = "test@email.com";
            CreateBusinessRequest request = new CreateBusinessRequest(
                    "My Business", 1L, "Some Info", "Barcelona", "ES", "Europe/Madrid"
            );
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).build()));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(Category.builder().categoryId(1L).build()));
            when(roleRepository.findByCode("BUSINESS_OWNER")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> businessCreator.create(request, email));
            verify(businessRepository, never()).save(any());
        }
    }


