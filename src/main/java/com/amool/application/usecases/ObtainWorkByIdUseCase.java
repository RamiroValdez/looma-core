package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;

import java.util.Comparator;
import java.util.Optional;

public class ObtainWorkByIdUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final AwsS3Port awsS3Port;

    public ObtainWorkByIdUseCase(ObtainWorkByIdPort obtainWorkByIdPort, AwsS3Port awsS3Port) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.awsS3Port = awsS3Port;
    }

    public Optional<Work> execute(Long workId) {
        Optional<Work> work = obtainWorkByIdPort.obtainWorkById(workId);

        work.ifPresent(
                it -> {
                    it.setBanner(this.awsS3Port.obtainPublicUrl(it.getBanner()));
                    it.setCover(this.awsS3Port.obtainPublicUrl(it.getCover()));
                    it.getCategories().sort(Comparator.comparing(Category::getName));
                }
        );

        return work;
    }

}
