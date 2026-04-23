package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import dev.esanchez.timely.backend.module.business.dto.response.CreateBusinessResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessControllerTest {

    @Mock
    private BusinessService businessService;

    @InjectMocks
    private BusinessController businessController;

    @Test
    void createBusiness_shouldReturnOk() {
        // Arrange
        CreateBusinessRequest request = mock(CreateBusinessRequest.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        CreateBusinessResponse mockResponse = mock(CreateBusinessResponse.class);

        when(userDetails.getUsername()).thenReturn("test@email.com");
        when(businessService.createBusiness(request, "test@email.com")).thenReturn(String.valueOf(mockResponse));

        // Act
        ResponseEntity<CreateBusinessResponse> response = businessController.createBusiness(request, userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(businessService).createBusiness(request, "test@email.com");
    }

    @Test
    void createBusiness_shouldDelegateEmailFromUserDetails() {
        // Arrange
        CreateBusinessRequest request = mock(CreateBusinessRequest.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@email.com");

        // Act
        businessController.createBusiness(request, userDetails);

        // Assert
        verify(businessService).createBusiness(any(), eq("test@email.com"));
    }
}
