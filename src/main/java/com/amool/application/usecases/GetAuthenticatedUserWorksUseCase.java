package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;

import java.util.Comparator;
import java.util.List;

public class GetAuthenticatedUserWorksUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final AwsS3Port awsS3Port;

    public GetAuthenticatedUserWorksUseCase(ObtainWorkByIdPort obtainWorkByIdPort, AwsS3Port awsS3Port) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.awsS3Port = awsS3Port;
    }

    public List<Work> execute(Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        List<Work> work = this.obtainWorkByIdPort.getWorksByUserId(authenticatedUserId);

        work.forEach(it -> {
            it.setBanner(this.awsS3Port.obtainPublicUrl(it.getBanner()));
            it.setCover(this.awsS3Port.obtainPublicUrl(it.getCover()));
            it.getCategories().sort(Comparator.comparing(Category::getName));
        });

        return work;
    }

}
