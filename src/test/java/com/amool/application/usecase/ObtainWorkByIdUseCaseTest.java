package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LikePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObtainWorkByIdUseCaseTest {

    private static final Long TEST_USER_ID = 123L;

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;
    
    @Mock
    private AwsS3Port awsS3Port;

    @Mock
    LikePort likePort;
    
    private ObtainWorkByIdUseCase obtainWorkByIdUseCase;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return new TestingAuthenticationToken(TEST_USER_ID.toString(), null);
            }
            
            @Override
            public void setAuthentication(Authentication authentication) {
            }
        });
        
        obtainWorkByIdUseCase = new ObtainWorkByIdUseCase(
                obtainWorkByIdPort, 
                awsS3Port,
                likePort);
    }
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void when_WorkExists_ThenReturnWorkWithUpdatedUrlsAndSortedCategories() {
        Long workId = 1L;
        Long userId = 1L;
        Work work = new Work();
        work.setId(workId);
        work.setBanner("banner.jpg");
        work.setCover("cover.jpg");
        
        Category category1 = new Category();
        category1.setName("Z Category");
        Category category2 = new Category();
        category2.setName("A Category");
        work.setCategories(Arrays.asList(category1, category2));

        String expectedBannerUrl = "https://example.com/banner.jpg";
        String expectedCoverUrl = "https://example.com/cover.jpg";

        when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));
        
        when(awsS3Port.obtainPublicUrl("banner.jpg"))
                .thenReturn(expectedBannerUrl);
                
        when(awsS3Port.obtainPublicUrl("cover.jpg"))
                .thenReturn(expectedCoverUrl);

        Optional<Work> result = obtainWorkByIdUseCase.execute(workId, 1L);

        assertTrue(result.isPresent(), "El resultado debería contener un trabajo");
        assertEquals(workId, result.get().getId(), "El ID del trabajo no coincide");
        assertEquals(expectedBannerUrl, result.get().getBanner(), "La URL del banner no coincide");
        assertEquals(expectedCoverUrl, result.get().getCover(), "La URL de la portada no coincide");
        
        assertEquals("A Category", result.get().getCategories().get(0).getName(), 
            "La primera categoría debería estar ordenada alfabéticamente");
        assertEquals("Z Category", result.get().getCategories().get(1).getName(),
            "La segunda categoría debería estar ordenada alfabéticamente");
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verify(awsS3Port).obtainPublicUrl("banner.jpg");
        verify(awsS3Port).obtainPublicUrl("cover.jpg");
    }

    @Test
    public void when_WorkDoesNotExist_ThenReturnEmptyOptional() {
        Long workId = 999L;
        Long userId = 1L;

        when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.empty());

        Optional<Work> result = obtainWorkByIdUseCase.execute(workId,1L);

        assertFalse(result.isPresent(), "El resultado debería estar vacío cuando el trabajo no existe");
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verifyNoInteractions(awsS3Port);
    }

}
