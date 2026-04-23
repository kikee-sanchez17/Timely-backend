package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.module.business.creator.BusinessCreator;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BusinessServiceImplTest {

    @Mock
    private BusinessCreator businessCreator;

    @InjectMocks
    private BusinessServiceImpl businessService;

    @Test
    void createBusiness_shouldDelegateToCreator() {
        // Arrange
        String email = "test@email.com";
        CreateBusinessRequest request = mock(CreateBusinessRequest.class);

        // Act
        businessService.createBusiness(request, email);

        // Assert
        verify(businessCreator).create(request, email);
    }
}
