package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.domain.model.Work;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

public class UpdateBannerUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final ImagesService imagesService;
    private final WorkPort workPort;

    public UpdateBannerUseCase(
            ObtainWorkByIdPort obtainWorkByIdPort,
            ImagesService imagesService,
            WorkPort workPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.imagesService = imagesService;
        this.workPort = workPort;
    }

    public void execute(Long workId, MultipartFile bannerFile, Long authenticatedUserId) throws IOException {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        Work work = this.obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));

        if (work.getCreator() == null || !Objects.equals(work.getCreator().getId(), authenticatedUserId)) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        this.imagesService.deleteImage(work.getBanner());

        String newBannerPath = this.imagesService.uploadBannerImage(bannerFile, work.getId().toString());
        work.setBanner(newBannerPath);
        this.workPort.updateWork(work);
    }
}
