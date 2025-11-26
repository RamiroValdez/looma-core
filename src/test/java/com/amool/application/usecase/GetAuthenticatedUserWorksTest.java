package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.GetAuthenticatedUserWorks;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GetAuthenticatedUserWorksTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private AwsS3Port awsS3Port;
    private GetAuthenticatedUserWorks useCase;

    private static final Long AUTHENTICATED_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        awsS3Port = Mockito.mock(AwsS3Port.class);
        
        useCase = new GetAuthenticatedUserWorks(
            obtainWorkByIdPort,
            awsS3Port
        );

        when(awsS3Port.obtainPublicUrl(anyString()))
            .thenAnswer(invocation -> "https://s3.url/" + invocation.getArgument(0));
    }

    private void givenAuthenticatedUserHasWorks(Long userId, List<Work> works) {
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(works);
    }

    private void givenAuthenticatedUserHasNoWorks(Long userId) {
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(List.of());
    }

    private List<Work> whenGetWorks(Long userId) {
        return useCase.execute(userId);
    }

    private void whenGetWorksExpectException(Long userId, Class<? extends Throwable> expected) {
        assertThrows(expected, () -> useCase.execute(userId));
    }

    private void thenWorksReturned(List<Work> result, int expectedSize, List<String> expectedTitles) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedTitles.size(); i++) {
            assertEquals(expectedTitles.get(i), result.get(i).getTitle());
        }
    }

    private void thenEmptyWorks(List<Work> result) {
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void thenRepositoryCalled(Long userId) {
        verify(obtainWorkByIdPort).getWorksByUserId(userId);
    }

    private void thenS3UrlsResolved(int timesExpected) {
        verify(awsS3Port, times(timesExpected)).obtainPublicUrl(anyString());
    }

    private void thenNoS3Interactions() {
        verify(awsS3Port, never()).obtainPublicUrl(anyString());
    }

    private void thenNoRepositoryInteractions() {
        verify(obtainWorkByIdPort, never()).getWorksByUserId(anyLong());
    }

    @Test
    public void when_GetWorksWithAuthenticatedUser_ThenReturnUserWorks() {
        Work work1 = createWork(1L, "Work 1", "cover1.jpg", "banner1.jpg");
        Work work2 = createWork(2L, "Work 2", "cover2.jpg", "banner2.jpg");
        givenAuthenticatedUserHasWorks(AUTHENTICATED_USER_ID, Arrays.asList(work1, work2));

        List<Work> result = whenGetWorks(AUTHENTICATED_USER_ID);

        thenWorksReturned(result, 2, List.of("Work 1", "Work 2"));
        thenRepositoryCalled(AUTHENTICATED_USER_ID);
        thenS3UrlsResolved(4); // 2 covers + 2 banners
    }

    @Test
    public void when_UserHasNoWorks_ThenReturnEmptyList() {
        givenAuthenticatedUserHasNoWorks(AUTHENTICATED_USER_ID);

        List<Work> result = whenGetWorks(AUTHENTICATED_USER_ID);

        thenEmptyWorks(result);
        thenRepositoryCalled(AUTHENTICATED_USER_ID);
        thenNoS3Interactions();
    }

    @Test
    public void when_UserIsNotAuthenticated_ThenThrowSecurityException() {
        whenGetWorksExpectException(null, SecurityException.class);
        thenNoRepositoryInteractions();
        thenNoS3Interactions();
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
