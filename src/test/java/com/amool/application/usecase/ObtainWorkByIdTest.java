package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LikePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.ObtainWorkById;
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
public class ObtainWorkByIdTest {

    private static final Long TEST_USER_ID = 123L;
    private static final Long EXISTING_WORK_ID = 1L;
    private static final Long NON_EXISTENT_WORK_ID = 999L;
    private static final Long REQUEST_USER_ID = 1L;
    private static final String BANNER_KEY = "banner.jpg";
    private static final String COVER_KEY = "cover.jpg";
    private static final String BANNER_URL = "https://example.com/banner.jpg";
    private static final String COVER_URL = "https://example.com/cover.jpg";

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;
    
    @Mock
    private AwsS3Port awsS3Port;

    @Mock
    LikePort likePort;
    
    private ObtainWorkById obtainWorkById;

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
        
        obtainWorkById = new ObtainWorkById(
                obtainWorkByIdPort, 
                awsS3Port,
                likePort);
    }
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void shouldReturnWorkWithUpdatedUrlsAndSortedCategories() {
        Work storedWork = buildWork(EXISTING_WORK_ID, BANNER_KEY, COVER_KEY, "Z Category", "A Category");
        givenWorkExists(storedWork);
        givenPublicUrl(BANNER_KEY, BANNER_URL);
        givenPublicUrl(COVER_KEY, COVER_URL);

        Optional<Work> result = whenObtainingWork(EXISTING_WORK_ID, REQUEST_USER_ID);

        Work enrichedWork = thenWorkIsPresent(result, EXISTING_WORK_ID);
        thenWorkHasUpdatedAssets(enrichedWork, BANNER_URL, COVER_URL);
        thenCategoriesAreSorted(enrichedWork, "A Category", "Z Category");
        thenWorkDependenciesVerified(EXISTING_WORK_ID, BANNER_KEY, COVER_KEY);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenWorkDoesNotExist() {
        givenWorkDoesNotExist(NON_EXISTENT_WORK_ID);

        Optional<Work> result = whenObtainingWork(NON_EXISTENT_WORK_ID, REQUEST_USER_ID);

        thenWorkIsAbsent(result);
        thenNoAssetLookupOccurs();
    }

    private void givenWorkExists(Work work) {
        when(obtainWorkByIdPort.obtainWorkById(work.getId())).thenReturn(Optional.of(work));
    }

    private void givenPublicUrl(String assetKey, String url) {
        when(awsS3Port.obtainPublicUrl(assetKey)).thenReturn(url);
    }

    private void givenWorkDoesNotExist(Long workId) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private Optional<Work> whenObtainingWork(Long workId, Long userId) {
        return obtainWorkById.execute(workId, userId);
    }

    private Work thenWorkIsPresent(Optional<Work> result, Long expectedId) {
        assertTrue(result.isPresent(), "El resultado debería contener un trabajo");
        Work work = result.get();
        assertEquals(expectedId, work.getId(), "El ID del trabajo no coincide");
        return work;
    }

    private void thenWorkHasUpdatedAssets(Work work, String expectedBannerUrl, String expectedCoverUrl) {
        assertEquals(expectedBannerUrl, work.getBanner(), "La URL del banner no coincide");
        assertEquals(expectedCoverUrl, work.getCover(), "La URL de la portada no coincide");
    }

    private void thenCategoriesAreSorted(Work work, String... expectedOrder) {
        assertEquals(expectedOrder.length, work.getCategories().size(), "Cantidad de categorías inesperada");
        for (int i = 0; i < expectedOrder.length; i++) {
            assertEquals(expectedOrder[i], work.getCategories().get(i).getName(), "Las categorías deben estar ordenadas alfabéticamente");
        }
    }

    private void thenWorkDependenciesVerified(Long workId, String... assetKeys) {
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        for (String assetKey : assetKeys) {
            verify(awsS3Port).obtainPublicUrl(assetKey);
        }
    }

    private void thenWorkIsAbsent(Optional<Work> result) {
        assertFalse(result.isPresent(), "El resultado debería estar vacío cuando el trabajo no existe");
        verify(obtainWorkByIdPort).obtainWorkById(NON_EXISTENT_WORK_ID);
    }

    private void thenNoAssetLookupOccurs() {
        verifyNoInteractions(awsS3Port);
    }

    private Work buildWork(Long workId, String bannerKey, String coverKey, String... categoryNames) {
        Work work = new Work();
        work.setId(workId);
        work.setBanner(bannerKey);
        work.setCover(coverKey);
        work.setCategories(Arrays.stream(categoryNames).map(name -> {
            Category category = new Category();
            category.setName(name);
            return category;
        }).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)));
        return work;
    }
}
