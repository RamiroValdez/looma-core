package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LikePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Category;
import com.amool.domain.model.Work;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Comparator;
import java.util.Optional;

public class ObtainWorkByIdUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final AwsS3Port awsS3Port;
    private final LikePort likePort;

    public ObtainWorkByIdUseCase(ObtainWorkByIdPort obtainWorkByIdPort, 
                               AwsS3Port awsS3Port,
                                 LikePort likePort) {
        this.likePort = likePort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.awsS3Port = awsS3Port;
    }

    public Optional<Work> execute(Long workId, Long userId) {
        return obtainWorkByIdPort.obtainWorkById(workId)
            .map(work -> {
                work.setBanner(this.awsS3Port.obtainPublicUrl(work.getBanner()));
                work.setCover(this.awsS3Port.obtainPublicUrl(work.getCover()));
                work.getCategories().sort(Comparator.comparing(Category::getName));
                work.setLikedByUser(likePort.isWorkLikedByUser(work.getId(), userId));

                work.getChapters().forEach(chapter -> {
                    chapter.setLikedByUser(likePort.isChapterLikedByUser(chapter.getId(), userId));
                });

                return work;
            });
    }
}
