package com.amool.config;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UseCasesConfig {

    private final FilesStoragePort filesStoragePort;
    private final AuthenticateUserPort authPort;
    private final CategoryPort categoryPort;
    private final FormatPort formatPort;
    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final SaveChapterPort saveChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final LoadLanguagePort loadLanguagePort;
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
    private final LikePort likePort;
    private final RatingPort ratingPort;
    private final ReadingProgressPort readingProgressPort;
    private final ImagesService imagesService;
    private final LoadWorkOwnershipPort loadWorkOwnershipPort;
    private final SubscriptionQueryPort subscriptionQueryPort;
    private final PaymentAuditPort paymentAuditPort;
    private final UserBalancePort userBalancePort;
    private final PaymentRecordPort paymentRecordPort;
    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final RestTemplate restTemplate;
    private final NotificationPort notificationPort;
    private final ObtainChapterByIdPort obtainChapterByIdPort;
    private final UserPreferencesPort userPreferencesPort;
    private final DownloadPort downloadPort;
    private final AnalyticsPort analyticsPort;
    private final UserAccountPort userAccountPort;
    private final EmailPort emailPort;
    private final PaymentSessionLinkPort paymentSessionLinkPort;
    private final ChatConversationPort chatConversationPort;
    private final ChatAIPort chatAIPort;

    public UseCasesConfig(
            FilesStoragePort filesStoragePort,
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
            LikePort likePort,
            LoadWorkOwnershipPort loadWorkOwnershipPort,
            SubscriptionQueryPort subscriptionQueryPort,
            PaymentAuditPort paymentAuditPort,
            UserBalancePort userBalancePort,
            PaymentRecordPort paymentRecordPort,
            SubscriptionPersistencePort subscriptionPersistencePort,
            RestTemplate restTemplate,
            RatingPort ratingPort,
            ReadingProgressPort readingProgressPort,
            AnalyticsPort analyticsPort,
            NotificationPort notificationPort,
            DownloadPort downloadPort,
            ObtainChapterByIdPort obtainChapterByIdPort,
            PasswordEncoder passwordEncoder,
            ChatConversationPort chatConversationPort,
            ChatAIPort chatAIPort,
            UserAccountPort userAccountPort,
            EmailPort emailPort,
            PaymentSessionLinkPort paymentSessionLinkPort,
            UserPreferencesPort userPreferencesPort
            ) {
        this.filesStoragePort = filesStoragePort;
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
        this.ratingPort = ratingPort;
        this.saveWorkPort = saveWorkPort;
        this.likePort = likePort;
        this.loadWorkOwnershipPort = loadWorkOwnershipPort;
        this.subscriptionQueryPort = subscriptionQueryPort;
        this.paymentAuditPort = paymentAuditPort;
        this.userBalancePort = userBalancePort;
        this.paymentRecordPort = paymentRecordPort;
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        this.restTemplate = restTemplate;
        this.readingProgressPort = readingProgressPort;
        this.notificationPort = notificationPort;
        this.obtainChapterByIdPort = obtainChapterByIdPort;
        this.downloadPort = downloadPort;
        this.analyticsPort = analyticsPort;
        this.chatConversationPort = chatConversationPort;
        this.chatAIPort = chatAIPort;
        this.userAccountPort = userAccountPort;
        this.emailPort = emailPort;
        this.paymentSessionLinkPort = paymentSessionLinkPort;
        this.userPreferencesPort = userPreferencesPort;
    }

    @Bean
    public ObtainObjectsInPath obtainObjectsInPathUseCase() {
        return new ObtainObjectsInPath(filesStoragePort);
    }

    @Bean
    public ObtainPresignedUrl obtainPresignedUrlUseCase() {
        return new ObtainPresignedUrl(filesStoragePort);
    }

    @Bean
    public ObtainAllCategories obtainAllCategoriesUseCase() {
        return new ObtainAllCategories(categoryPort);
    }

    @Bean
    public ObtainAllFormats obtainAllFormatsUseCase() {
        return  new ObtainAllFormats(formatPort);
    }

    @Bean
    public CreateEmptyChapter createEmptyChapterUseCase(){
        return new CreateEmptyChapter(loadLanguagePort, saveChapterPort, saveChapterContentPort);
    }

    @Bean
    public CancelScheduledPublication cancelScheduledPublicationUseCase() {
        return new CancelScheduledPublication(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public DeleteChapter deleteChapterUseCase() {
        return new DeleteChapter(
                loadChapterPort,
                deleteChapterContentPort,
                deleteChapterPort,
                loadWorkOwnershipPort
        );
    }

    @Bean
    public GetChapterWithContent getChapterWithContentUseCase() {
        return new GetChapterWithContent(loadChapterPort, loadChapterContentPort);
    }

    @Bean
    public GetChapterForEdit getChapterForEditUseCase() {
        return new GetChapterForEdit(
                loadChapterPort,
                loadChapterContentPort,
                loadLanguagePort,
                obtainWorkByIdPort
        );
    }

    @Bean
    public PublishChapter publishChapterUseCase() {
        return new PublishChapter(
                loadChapterPort,
                obtainWorkByIdPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public SchedulePublication schedulePublicationUseCase() {
        return new SchedulePublication(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    @Bean
    public UpdateChapter updateChapterUseCase(){
        return new UpdateChapter(
                loadChapterPort,
                updateChapterPort,
                saveChapterContentPort
        );
    }

    @Bean
    public Login loginUseCase() {
        return new Login(authPort);
    }

    @Bean
    public ExtractTextFromFile extractTextToFileUseCase() {
        return new ExtractTextFromFile();
    }

    @Bean
    public GenerateImageUrl generateImageUrlUseCase() {
        return new GenerateImageUrl(openAIImagePort);
    }

    @Bean
    public GetAllLanguages getAllLanguagesUseCase() {
        return new GetAllLanguages(loadLanguagePort);
    }

    @Bean
    public GetMatchTags getMatchTagsUseCase() {
        return new GetMatchTags(tagPort);
    }

    @Bean
    public SuggestTags suggestTagsUseCase() {
        return new SuggestTags(tagSuggestionPort);
    }

    @Bean
    public CreateLanguageVersion createLanguageVersionUseCase() {
        return new CreateLanguageVersion(openAIPort, googleTranslatePort);
    }

    @Bean
    public GetUserById getUserByIdUseCase() {
        return new GetUserById(loadUserPort, filesStoragePort);
    }

    @Bean
    public ObtainWorkById obtainWorkByIdUseCase() {
        return new ObtainWorkById(
                obtainWorkByIdPort,
                filesStoragePort,
                likePort);
    }

    @Bean
    public GetWorksByUserId getWorksByUserIdUseCase() {
        return new GetWorksByUserId(obtainWorkByIdPort);
    }

    @Bean
    public GetAuthenticatedUserWorks getAuthenticatedUserWorksUseCase() {
        return new GetAuthenticatedUserWorks(
                obtainWorkByIdPort,
                filesStoragePort);
    }

    @Bean
    public UpdateCover updateCoverUseCase() {
        return new UpdateCover(
                obtainWorkByIdPort,
                imagesService,
                workPort);
    }

    @Bean
    public CreateWork createWorkUseCase() {
        return new CreateWork(
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
    public UpdateBanner updateBannerUseCase() {
        return new UpdateBanner(
                obtainWorkByIdPort,
                imagesService,
                workPort);
    }

    @Bean
    public SearchAndFiltrate searchAndFiltrateUseCase() {
        return new SearchAndFiltrate(workPort, filesStoragePort);
    }

    @Bean
    public IsWorkSaved isWorkSavedUseCase() {
        return new IsWorkSaved(saveWorkPort);
    }

     @Bean
    public GetSavedWorks getSavedWorksUseCase() {
    return new GetSavedWorks(saveWorkPort, filesStoragePort);
}

    @Bean
    public ToggleSaveWork toggleSaveWorkUseCase() {
        return new ToggleSaveWork(saveWorkPort);
    }

    @Bean
    public GetAllWorks getAllWorksUseCase() {
        return new GetAllWorks(workPort);
    }

    @Bean
    public RateWork rateWorkUseCase() {
        return new RateWork(ratingPort);
    }

    @Bean
    public GetUserRating getUserRatingUseCase() {
        return new GetUserRating(ratingPort);
    }

    @Bean
    public GetWorkRatings getWorkRatingsUseCase() {
        return new GetWorkRatings(ratingPort);
    }

    @Bean
    public ToggleWorkLike toggleWorkLikeUseCase() {
        return new ToggleWorkLike(likePort);
    }

    @Bean
    public ToggleChapterLike toggleChapterLikeUseCase() {
        return new ToggleChapterLike(likePort);
    }

    @Bean
    public UpdateChapterContent updateChapterContentUseCase() {
        return new UpdateChapterContent(
                saveChapterContentPort,
                loadWorkOwnershipPort);
    }

    @Bean
    public ValidateChapterAccess validateChapterAccessUseCase() {
        return new ValidateChapterAccess(
                loadChapterPort,
                obtainWorkByIdPort,
                subscriptionQueryPort
        );
    }

    @Bean
    public GetWorkPermissions getWorkPermissionsUseCase() {
        return new GetWorkPermissions(subscriptionQueryPort);
    }

    @Bean
    public ExtractPaymentIdFromWebhook extractPaymentIdFromWebhookUseCase() {
        return new ExtractPaymentIdFromWebhook();
    }

    @Bean
    public SubscribeUser subscribeUserUseCase() {
        return new SubscribeUser(subscriptionPersistencePort);
    }

    @Bean
    public ProcessMercadoPagoWebhook processMercadoPagoWebhookUseCase() {
        return new ProcessMercadoPagoWebhook(
                restTemplate,
                paymentAuditPort,
                userBalancePort,
                paymentRecordPort,
                obtainWorkByIdPort,
                loadChapterPort,
                subscribeUserUseCase(),
                paymentSessionLinkPort,
                loadUserPort
        );
    }

    @Bean
    public ObtainWorkList obtainWorkListUseCase() {
        return new ObtainWorkList(workPort, filesStoragePort);
    }

    @Bean
    public UpdateReadingProgress updateReadingProgressUseCase() {
        return new UpdateReadingProgress(readingProgressPort);
    }

    @Bean
    public CreateSubscriptionNotification saveNotificationUseCase() {
        return new CreateSubscriptionNotification(notificationPort, obtainWorkByIdPort, obtainChapterByIdPort, loadUserPort);
    }

    @Bean
    public CreateWorkNotification createWorkNotification() {
        return new CreateWorkNotification(loadUserPort, notificationPort, obtainWorkByIdPort, emailPort);
    }

    @Bean
    public CreateAuthorNotification createAuthorNotification() {
        return new CreateAuthorNotification(notificationPort, loadUserPort, obtainWorkByIdPort, emailPort);
    }
    @Bean
    public ObtainNotifications obtainNotificationsUseCase() {
        return new ObtainNotifications(notificationPort);
    }

    @Bean
    public UpdateNotificationRead updateNotificationReadUseCase() {
        return new UpdateNotificationRead(notificationPort);
    }

    @Bean
    public SetUserPreferences setUserPreferencesUseCase() {
        return new SetUserPreferences(userPreferencesPort);
    }

    @Bean
    public UpdateUser updateUserUseCase() {
        return new UpdateUser(loadUserPort, imagesService);
    }



    @Bean
    public GetLikesPerWork getLikesPerWorkUseCase() {
        return new GetLikesPerWork(analyticsPort);
    }

    @Bean
    public GetLikesPerChapter getLikesPerChapterUseCase() {
        return new GetLikesPerChapter(analyticsPort);
    }

    @Bean
    public GetRatingsPerWork getRatingsPerWorkUseCase() {
        return new GetRatingsPerWork(analyticsPort);
    }

    @Bean
    public GetSavesPerWork getSavesPerWorkUseCase() {
        return new GetSavesPerWork(analyticsPort);
    }
    @Bean
    public ProcessChatMessage processChatMessageUseCase() {
        return new ProcessChatMessage(chatConversationPort, chatAIPort);
    }

    @Bean
    public GetChatConversation getChatConversationUseCase() {
        return new GetChatConversation(chatConversationPort);
    }

    @Bean
    public StartSubscriptionFlow startSubscriptionFlowUseCase(java.util.List<PaymentProviderPort> paymentProviders) {
        return new StartSubscriptionFlow(
                obtainWorkByIdPort,
                loadChapterPort,
                subscribeUserUseCase(),
                paymentProviders,
                loadUserPort
        );
    }


    @Bean
    public GetSuscribersPerAuthor getSuscribersPerAuthorUseCase() {
        return new GetSuscribersPerAuthor(analyticsPort);
    }
    @Bean
    public ExportEpub exportEpubUseCase() {
        return new ExportEpub(
                obtainWorkByIdPort,
                loadChapterContentPort,
                filesStoragePort,
                downloadPort,
                workPort,
                subscriptionQueryPort
        );
    }


    @Bean
    public ExportPdf exportPdfUseCase() {
        return new ExportPdf(
                obtainWorkByIdPort,
                loadChapterContentPort,
                filesStoragePort,
                downloadPort,
                workPort,
                subscriptionQueryPort
        );
    }

    @Bean
    public UpdateWork updateWorkUseCase() {
        return new UpdateWork(
                workPort,
                obtainWorkByIdPort,
                tagPort,
                categoryPort
        );
    }

    @Bean
    public GetSuscribersPerWork getSuscribersPerWorkUseCase() {
        return new GetSuscribersPerWork(analyticsPort);
    }

    @Bean
    public GetTotalPerAuthor getTotalPerAuthorUseCase() {
        return new GetTotalPerAuthor(analyticsPort);
    }

    @Bean
    public GetTotalPerWork getTotalPerWorkUseCase() {
        return new GetTotalPerWork(analyticsPort);
    }

    @Bean
    public GetTotalSuscribers getTotalSuscribersUseCase() {
        return new GetTotalSuscribers(analyticsPort, obtainWorkByIdPort);
    }
    @Bean
    public StartRegistration startRegistrationUseCase() {
        return new StartRegistration(userAccountPort, emailPort);
    }

    @Bean
    public VerifyRegistration verifyRegistrationUseCase() {
        return new VerifyRegistration(userAccountPort);
    }

    @Bean
    public GetUserPhoto getUserPhoto() {
        return new GetUserPhoto(filesStoragePort, loadUserPort);
    }

    @Bean
    public GetTotalRetention getTotalRetention(){
        return new GetTotalRetention(analyticsPort);
    }

    @Bean
    public ObtainReadingHistory obtainReadingHistory(){
        return new ObtainReadingHistory(analyticsPort);
    }

    @Bean
    public GetSubscriptions getSubscriptions() {
        return new GetSubscriptions(subscriptionPersistencePort, filesStoragePort);
    }
}
