package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.GetAuthenticatedUserWorksUseCase;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GetAuthenticatedUserWorksUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private AwsS3Port awsS3Port;
    private GetAuthenticatedUserWorksUseCase useCase;

    private static final Long AUTHENTICATED_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        awsS3Port = Mockito.mock(AwsS3Port.class);
        
        useCase = new GetAuthenticatedUserWorksUseCase(
            obtainWorkByIdPort,
            awsS3Port
        );

        when(awsS3Port.obtainPublicUrl(anyString()))
            .thenAnswer(invocation -> "https://s3.url/" + invocation.getArgument(0));
    }

    @Test
    public void when_GetWorksWithAuthenticatedUser_ThenReturnUserWorks() {
        Work work1 = createWork(1L, "Work 1", "cover1.jpg", "banner1.jpg");
        Work work2 = createWork(2L, "Work 2", "cover2.jpg", "banner2.jpg");
        List<Work> expectedWorks = Arrays.asList(work1, work2);

        when(obtainWorkByIdPort.getWorksByUserId(AUTHENTICATED_USER_ID))
            .thenReturn(expectedWorks);

        List<Work> result = useCase.execute(AUTHENTICATED_USER_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Work 1", result.get(0).getTitle());
        assertEquals("Work 2", result.get(1).getTitle());
        
        verify(obtainWorkByIdPort).getWorksByUserId(AUTHENTICATED_USER_ID);
        verify(awsS3Port, times(4)).obtainPublicUrl(anyString());
    }

    @Test
    public void when_UserHasNoWorks_ThenReturnEmptyList() {
        when(obtainWorkByIdPort.getWorksByUserId(AUTHENTICATED_USER_ID))
            .thenReturn(List.of());

        List<Work> result = useCase.execute(AUTHENTICATED_USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainWorkByIdPort).getWorksByUserId(AUTHENTICATED_USER_ID);
        verify(awsS3Port, never()).obtainPublicUrl(anyString());
    }

    @Test
    public void when_UserIsNotAuthenticated_ThenThrowSecurityException() {
        assertThrows(SecurityException.class, () -> useCase.execute(null));
        verify(obtainWorkByIdPort, never()).getWorksByUserId(anyLong());
        verify(awsS3Port, never()).obtainPublicUrl(anyString());
    }


    private Work createWork(Long id, String title, String cover, String banner) {
        Work work = new Work();
        work.setId(id);
        work.setTitle(title);
        work.setCover(cover);
        work.setBanner(banner);
        work.setCreator(createUser(AUTHENTICATED_USER_ID, "test@example.com"));
        return work;
    }

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
