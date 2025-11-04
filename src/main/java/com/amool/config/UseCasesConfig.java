package com.amool.config;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.application.usecases.CreateLanguageVersionUseCase;
import com.amool.application.usecases.GetMatchTagsUseCase;
import com.amool.application.usecases.SuggestTagsUseCase;
import com.amool.application.usecases.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    private final AwsS3Port awsS3Port;
    private final AuthenticateUserPort authPort;
    private final CategoryPort categoryPort;
    private final FormatPort formatPort;
    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final LoadLanguagePort loadLanguagePort;
    private final SaveChapterPort saveChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final DeleteChapterContentPort deleteChapterContentPort;
    private final DeleteChapterPort deleteChapterPort;
    private final UpdateChapterStatusPort updateChapterStatusPort;
    private final UpdateChapterPort updateChapterPort;
    private final OpenAIImagePort openAIImagePort;
    private final TagPort tagPort;
    private final TagSuggestionPort tagSuggestionPort;
    private final OpenAIPort openAIPort;
    private final GoogleTranslatePort googleTranslatePort;
    private final LoadUserPort loadUserPort;
    private final WorkPort workPort;
    private final SaveWorkPort saveWorkPort;
    private final ReadingProgressPort readingProgressPort;

    private final ImagesService imagesService;

    public UseCasesConfig(
            AwsS3Port awsS3Port,
            AuthenticateUserPort authPort,
            CategoryPort categoryPort,
            FormatPort formatPort,
            LoadChapterPort loadChapterPort,
            LoadChapterContentPort loadChapterContentPort,
            SaveChapterPort saveChapterPort,
            SaveChapterContentPort saveChapterContentPort,
            LoadLanguagePort loadLanguagePort,
            ObtainWorkByIdPort obtainWorkByIdPort,
            DeleteChapterContentPort deleteChapterContentPort,
            DeleteChapterPort deleteChapterPort,
            UpdateChapterStatusPort updateChapterStatusPort,
            UpdateChapterPort updateChapterPort,
            OpenAIImagePort openAIImagePort,
            TagPort tagPort,
            TagSuggestionPort tagSuggestionPort,
            OpenAIPort openAIPort,
            GoogleTranslatePort googleTranslatePort,
            LoadUserPort loadUserPort,
            ImagesService imagesService,
            WorkPort workPort,
            SaveWorkPort saveWorkPort,
            ReadingProgressPort readingProgressPort
            ) {
        this.awsS3Port = awsS3Port;
        this.authPort = authPort;
        this.categoryPort = categoryPort;
        this.formatPort = formatPort;
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.saveChapterPort = saveChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
        this.loadLanguagePort = loadLanguagePort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.deleteChapterPort = deleteChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.updateChapterStatusPort = updateChapterStatusPort;
        this.updateChapterPort = updateChapterPort;
        this.openAIImagePort = openAIImagePort;
        this.tagPort = tagPort;
        this.tagSuggestionPort = tagSuggestionPort;
        this.openAIPort = openAIPort;
        this.googleTranslatePort = googleTranslatePort;
        this.loadUserPort = loadUserPort;
        this.imagesService = imagesService;
        this.workPort = workPort;
        this.saveWorkPort = saveWorkPort;
        this.readingProgressPort = readingProgressPort;
    }

    @Bean
    public ObtainObjectsInPathUseCase obtainObjectsInPathUseCase() {
        return new ObtainObjectsInPathUseCase(awsS3Port);
    }

    @Bean
    public ObtainPresignedUrlUseCase obtainPresignedUrlUseCase() {
        return new ObtainPresignedUrlUseCase(awsS3Port);
    }

    @Bean
    public ObtainAllCategoriesUseCase obtainAllCategoriesUseCase() {
        return new ObtainAllCategoriesUseCase(categoryPort);
    }

    @Bean
    public ObtainAllFormatsUseCase obtainAllFormatsUseCase() {
        return  new ObtainAllFormatsUseCase(formatPort);
    }

    @Bean
    public CreateEmptyChapterUseCase createEmptyChapterUseCase(){
        return new CreateEmptyChapterUseCase(loadLanguagePort, saveChapterPort, saveChapterContentPort);
    }

    @Bean
    public CancelScheduledPublicationUseCase cancelScheduledPublicationUseCase() {
        return new CancelScheduledPublicationUseCase(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public DeleteChapterUseCase deleteChapterUseCase() {
        return new DeleteChapterUseCase(
                loadChapterPort,
                deleteChapterContentPort,
                deleteChapterPort
        );
    }

    @Bean
    public GetChapterWithContentUseCase getChapterWithContentUseCase() {
        return new GetChapterWithContentUseCase(loadChapterPort, loadChapterContentPort);
    }

    @Bean
    public GetChapterForEditUseCase getChapterForEditUseCase() {
        return new GetChapterForEditUseCase(
                loadChapterPort,
                loadChapterContentPort,
                loadLanguagePort,
                obtainWorkByIdPort
        );
    }

    @Bean
    public PublishChapterUseCase publishChapterUseCase() {
        return new PublishChapterUseCase(
                loadChapterPort,
                obtainWorkByIdPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public SchedulePublicationUseCase schedulePublicationUseCase() {
        return new SchedulePublicationUseCase(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public UpdateChapterUseCase updateChapterUseCase(){
        return new UpdateChapterUseCase(
                loadChapterPort,
                updateChapterPort,
                saveChapterContentPort
        );
    }

    @Bean
    public LoginUseCase loginUseCase() {
        return new LoginUseCase(authPort);
    }

    @Bean
    public ExtractTextFromFileUseCase extractTextToFileUseCase() {
        return new ExtractTextFromFileUseCase();
    }

    @Bean
    public GenerateImageUrlUseCase generateImageUrlUseCase() {
        return new GenerateImageUrlUseCase(openAIImagePort);
    }

    @Bean
    public GetAllLanguagesUseCase getAllLanguagesUseCase() {
        return new GetAllLanguagesUseCase(loadLanguagePort);
    }

    @Bean
    public GetMatchTagsUseCase getMatchTagsUseCase() {
        return new GetMatchTagsUseCase(tagPort);
    }

    @Bean
    public SuggestTagsUseCase suggestTagsUseCase() {
        return new SuggestTagsUseCase(tagSuggestionPort);
    }

    @Bean
    public CreateLanguageVersionUseCase createLanguageVersionUseCase() {
        return new CreateLanguageVersionUseCase(openAIPort, googleTranslatePort);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase() {
        return new GetUserByIdUseCase(loadUserPort);
    }

    @Bean
    public ObtainWorkByIdUseCase obtainWorkByIdUseCase() {
        return new ObtainWorkByIdUseCase(
                obtainWorkByIdPort,
                awsS3Port);
    }

    @Bean
    public GetWorksByUserIdUseCase getWorksByUserIdUseCase() {
        return new GetWorksByUserIdUseCase(obtainWorkByIdPort);
    }

    @Bean
    public GetAuthenticatedUserWorksUseCase getAuthenticatedUserWorksUseCase() {
        return new GetAuthenticatedUserWorksUseCase(
                obtainWorkByIdPort,
                awsS3Port);
    }

    @Bean
    public UpdateCoverUseCase updateCoverUseCase() {
        return new UpdateCoverUseCase(
                obtainWorkByIdPort,
                imagesService,
                workPort);
    }

    @Bean
    public CreateWorkUseCase createWorkUseCase() {
        return new CreateWorkUseCase(
                workPort,
                obtainWorkByIdPort,
                tagPort,
                loadUserPort,
                formatPort,
                loadLanguagePort,
                categoryPort,
                imagesService);
    }

    @Bean
    public UpdateBannerUseCase updateBannerUseCase() {
        return new UpdateBannerUseCase(
                obtainWorkByIdPort,
                imagesService,
                workPort);
    }

    @Bean
    public SearchAndFiltrateUseCase searchAndFiltrateUseCase() {
        return new SearchAndFiltrateUseCase(workPort, awsS3Port);
    }

    @Bean
    public IsWorkSavedUseCase isWorkSavedUseCase() {
        return new IsWorkSavedUseCase(saveWorkPort);
    }
    
    @Bean
    public GetSavedWorksUseCase getSavedWorksUseCase() {
        return new GetSavedWorksUseCase(saveWorkPort);
    }
    
    @Bean
    public ToggleSaveWorkUseCase toggleSaveWorkUseCase() {
        return new ToggleSaveWorkUseCase(saveWorkPort);
    }

    @Bean
    public GetAllWorksUseCase getAllWorksUseCase() {
        return new GetAllWorksUseCase(workPort);
    }

    @Bean
    public ObtainWorkListUseCase obtainWorkListUseCase() {
        return new ObtainWorkListUseCase(workPort);
    }

    @Bean
    public UpdateReadingProgressUseCase updateReadingProgressUseCase() {
        return new UpdateReadingProgressUseCase(readingProgressPort);
    }
}
