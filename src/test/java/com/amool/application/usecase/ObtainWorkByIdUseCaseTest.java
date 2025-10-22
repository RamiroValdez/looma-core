package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ObtainWorkByIdUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private AwsS3Port awsS3Port;
    private ObtainWorkByIdUseCase useCase;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        awsS3Port = Mockito.mock(AwsS3Port.class);
        useCase = new ObtainWorkByIdUseCase(obtainWorkByIdPort, awsS3Port);
    }

    @Test
    public void when_WorkExists_ThenReturnWorkWithUpdatedUrlsAndSortedCategories() {
        Long workId = 1L;
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

        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));
        
        Mockito.when(awsS3Port.obtainPublicUrl("banner.jpg"))
                .thenReturn(expectedBannerUrl);
                
        Mockito.when(awsS3Port.obtainPublicUrl("cover.jpg"))
                .thenReturn(expectedCoverUrl);

        Optional<Work> result = useCase.execute(workId);

        assertTrue(result.isPresent());
        assertEquals(workId, result.get().getId());
        assertEquals(expectedBannerUrl, result.get().getBanner());
        assertEquals(expectedCoverUrl, result.get().getCover());
        
        assertEquals("A Category", result.get().getCategories().get(0).getName());
        assertEquals("Z Category", result.get().getCategories().get(1).getName());
        
        Mockito.verify(obtainWorkByIdPort).obtainWorkById(workId);
        Mockito.verify(awsS3Port).obtainPublicUrl("banner.jpg");
        Mockito.verify(awsS3Port).obtainPublicUrl("cover.jpg");
    }

    @Test
    public void when_WorkDoesNotExist_ThenReturnEmptyOptional() {
        Long workId = 999L;

        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.empty());

        Optional<Work> result = useCase.execute(workId);

        assertFalse(result.isPresent());
        Mockito.verify(obtainWorkByIdPort).obtainWorkById(workId);
        Mockito.verifyNoInteractions(awsS3Port);
    }

}
