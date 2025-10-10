package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.ImagesService;
import com.amool.hexagonal.application.port.in.TagService;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.application.port.out.*;
import com.amool.hexagonal.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class WorkServiceImpl implements WorkService {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final ImagesService imagesService;
    private final LoadUserPort loadUserPort;
    private final FormatPort formatPort;
    private final LoadLanguagePort loadLanguagePort;
    private final CategoryPort categoryPort;
    private final TagService tagService;
    private final WorkPort workPort;

    public WorkServiceImpl(ObtainWorkByIdPort obtainWorkByIdPort,
                           ImagesService imagesService,
                           LoadUserPort loadUserPort,
                           FormatPort formatPort,
                           LoadLanguagePort loadLanguagePort,
                           CategoryPort categoryPort,
                           TagService tagService,
                           WorkPort workPort) {
        this.workPort = workPort;
        this.tagService = tagService;
        this.categoryPort = categoryPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.imagesService = imagesService;
        this.loadUserPort = loadUserPort;
        this.formatPort = formatPort;
        this.loadLanguagePort = loadLanguagePort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Work> obtainWorkById(Long workId) {
        Optional<Work> work = obtainWorkByIdPort.obtainWorkById(workId);

        work.ifPresent(
            it -> {
                it.setBanner(this.imagesService.getBannerImageUrl(it.getBanner()));
                it.setCover(this.imagesService.getCoverImageUrl(it.getCover()));
                it.getCategories().sort(Comparator.comparing(Category::getName));
            }
        );

        return work;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Work> getWorksByUserId(Long userId) {
        return obtainWorkByIdPort.getWorksByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Work> getAuthenticatedUserWorks(Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        List<Work> work = this.obtainWorkByIdPort.getWorksByUserId(authenticatedUserId);

        work.forEach(it -> {
            it.setBanner(this.imagesService.getBannerImageUrl(it.getBanner()));
            it.setCover(this.imagesService.getCoverImageUrl(it.getCover()));
            it.getCategories().sort(Comparator.comparing(Category::getName));
        });

        return work;
    }

    @Override
    public Long createWork(String title, String description, List<Long> categoryIds, Long formatId, Long originalLanguageId, Set<String> tagIds, MultipartFile coverFile, MultipartFile bannerFile, Long userId) throws IOException {

        Work work = this.initializeWork(title, description);

        work.setCreator(this.loadUser(userId));
        work.setFormat(this.loadFormat(formatId));
        work.setOriginalLanguage(this.loadLanguage(originalLanguageId));
        work.setCategories(this.loadCategories(categoryIds));

        Long workId = this.workPort.createWork(work);
        Work createdWork = this.obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new IllegalStateException("Error al crear la obra"));

        createdWork.setTags(tagService.getMatchTags(tagIds));

        this.updateWorkImages(createdWork, coverFile, bannerFile);

        this.workPort.updateWork(createdWork);

        return workId;
    }

    private Work initializeWork(String title, String description) {
        Work work = new Work();
        work.setTitle(title);
        work.setDescription(description);
        work.setCover("none");
        work.setBanner("none");
        work.setState("InProgress");
        work.setPrice(0.0);
        work.setLikes(0);
        work.setPublicationDate(LocalDate.now());
        return work;
    }

    private User loadUser(Long userId) {
        return loadUserPort.getById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private Format loadFormat(Long formatId) {
        return formatPort.getById(formatId)
                .orElseThrow(() -> new IllegalArgumentException("Formato no encontrado"));
    }

    private Language loadLanguage(Long languageId) {
        return loadLanguagePort.loadLanguageById(languageId)
                .orElseThrow(() -> new IllegalArgumentException("Idioma no encontrado"));
    }

    private List<Category> loadCategories(List<Long> categoryIds) {
        List<Category> categories = new ArrayList<>();
        for (Long id : categoryIds) {
            Category category = categoryPort.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
            categories.add(category);
        }
        return categories;
    }

    private void updateWorkImages(Work work, MultipartFile coverFile, MultipartFile bannerFile) throws IOException {
        String coverUrl = imagesService.uploadCoverImage(coverFile, work.getId().toString());
        String bannerUrl = imagesService.uploadBannerImage(bannerFile, work.getId().toString());
        work.setCover(coverUrl);
        work.setBanner(bannerUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCover(Long workId, MultipartFile coverFile, Long authenticatedUserId) throws IOException {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        Work work = this.obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));

        if (work.getCreator() == null || !Objects.equals(work.getCreator().getId(), authenticatedUserId)) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        this.imagesService.deleteImage(work.getCover());

        String newCoverPath = this.imagesService.uploadCoverImage(coverFile, work.getId().toString());
        work.setCover(newCoverPath);
        this.workPort.updateWork(work);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBanner(Long workId, MultipartFile bannerFile, Long authenticatedUserId) throws IOException {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        Work work = this.obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));

        if (work.getCreator() == null || !Objects.equals(work.getCreator().getId(), authenticatedUserId)) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        // Delete previous banner from S3 before uploading the new one
        this.imagesService.deleteImage(work.getBanner());

        String newBannerPath = this.imagesService.uploadBannerImage(bannerFile, work.getId().toString());
        work.setBanner(newBannerPath);
        this.workPort.updateWork(work);
    }
}
