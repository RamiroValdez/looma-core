package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;

import java.util.Comparator;
import java.util.List;

public class GetAuthenticatedUserWorks {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final FilesStoragePort filesStoragePort;

    public GetAuthenticatedUserWorks(ObtainWorkByIdPort obtainWorkByIdPort, FilesStoragePort filesStoragePort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.filesStoragePort = filesStoragePort;
    }

    public List<Work> execute(Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        List<Work> work = this.obtainWorkByIdPort.getWorksByUserId(authenticatedUserId);

        work.forEach(it -> {
            it.setBanner(this.filesStoragePort.obtainPublicUrl(it.getBanner()));
            it.setCover(this.filesStoragePort.obtainPublicUrl(it.getCover()));
            it.getCategories().sort(Comparator.comparing(Category::getName));
        });

        return work;
    }

}
