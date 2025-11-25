package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.domain.model.Work;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

public class UpdateCover {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final ImagesService imagesService;
    private final WorkPort workPort;

    public UpdateCover(
            ObtainWorkByIdPort obtainWorkByIdPort,
            ImagesService imagesService,
            WorkPort workPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.imagesService = imagesService;
        this.workPort = workPort;
    }


    public void execute(Long workId, MultipartFile coverFile, Long authenticatedUserId, String coverIaUrl) throws IOException, InterruptedException {
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

        String newCoverPath;

        if(coverFile == null){
            newCoverPath = imagesService.downloadAndUploadCoverImage(coverIaUrl, work.getId().toString());
        } else {
            newCoverPath = imagesService.uploadCoverImage(coverFile, work.getId().toString());
        }

        work.setCover(newCoverPath);
        this.workPort.updateWork(work);
    }

}
