package com.amool.application.usecases;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.domain.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateWork {

    private final WorkPort workPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final TagPort tagPort;
    private final LoadUserPort loadUserPort;
    private final FormatPort formatPort;
    private final LoadLanguagePort loadLanguagePort;
    private final CategoryPort categoryPort;
    private final ImagesService imagesService;

    public CreateWork(WorkPort workPort,
                      ObtainWorkByIdPort obtainWorkByIdPort,
                      TagPort tagPort,
                      LoadUserPort loadUserPort,
                      FormatPort formatPort,
                      LoadLanguagePort loadLanguagePort,
                      CategoryPort categoryPort,
                      ImagesService imagesService) {
        this.workPort = workPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.tagPort = tagPort;
        this.loadUserPort = loadUserPort;
        this.formatPort = formatPort;
        this.loadLanguagePort = loadLanguagePort;
        this.categoryPort = categoryPort;
        this.imagesService = imagesService;
    }

    public Long execute(String title,
                        String description,
                        List<Long> categoryIds,
                        Long formatId,
                        Long originalLanguageId,
                        BigDecimal price,
                        Set<String> tagIds,
                        String coverIaUrl,
                        MultipartFile coverFile,
                        MultipartFile bannerFile,
                        Long userId) throws IOException, InterruptedException {

        Work work = Work.initialize(title, description, price);

        work.setCreator(this.loadUser(userId));
        work.setFormat(this.loadFormat(formatId));
        work.setOriginalLanguage(this.loadLanguage(originalLanguageId));
        work.setCategories(this.loadCategories(categoryIds));
        Long workId = this.workPort.createWork(work);
        Work createdWork = this.obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new IllegalStateException("Error al crear la obra"));

        createdWork.setTags(getMatchTags(tagIds));

        this.updateWorkImages(createdWork, coverFile, bannerFile, coverIaUrl);

        this.workPort.updateWork(createdWork);

        return workId;
    }

    private Set<Tag> getMatchTags(Set<String> tagNames) {

        Set<Tag> tags = new HashSet<>();

        for (String tagName : tagNames) {
            Tag tag = tagPort.searchTag(tagName).orElseGet(() -> {
                Long tagId = tagPort.createTag(tagName);
                return new Tag(tagId, tagName);
            });
            tags.add(tag);
        }

        return tags;
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

    private void updateWorkImages(Work work, MultipartFile coverFile, MultipartFile bannerFile, String coverIaUrl) throws IOException, InterruptedException {
        String coverUrl;
        if(coverFile == null){
            coverUrl = imagesService.downloadAndUploadCoverImage(coverIaUrl, work.getId().toString());
        } else {
            coverUrl = imagesService.uploadCoverImage(coverFile, work.getId().toString());
        }

        String bannerUrl = imagesService.uploadBannerImage(bannerFile, work.getId().toString());
        work.setCover(coverUrl);
        work.setBanner(bannerUrl);
    }

}
