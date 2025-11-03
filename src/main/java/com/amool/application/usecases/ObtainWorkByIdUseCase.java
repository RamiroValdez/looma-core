package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
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
    private final CheckWorkLikesUseCase checkWorkLikesUseCase;

    public ObtainWorkByIdUseCase(ObtainWorkByIdPort obtainWorkByIdPort, 
                               AwsS3Port awsS3Port,
                               CheckWorkLikesUseCase checkWorkLikesUseCase) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.awsS3Port = awsS3Port;
        this.checkWorkLikesUseCase = checkWorkLikesUseCase;
    }

    public Optional<Work> execute(Long workId) {
        return obtainWorkByIdPort.obtainWorkById(workId)
            .map(work -> {
                work.setBanner(this.awsS3Port.obtainPublicUrl(work.getBanner()));
                work.setCover(this.awsS3Port.obtainPublicUrl(work.getCover()));
                work.getCategories().sort(Comparator.comparing(Category::getName));
                
                Long currentUserId = getCurrentUserId();
                checkWorkLikesUseCase.execute(work, currentUserId);
                
                return work;
            });
    }
    
    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                return Long.parseLong(((UserDetails) principal).getUsername());
            }
            if (principal instanceof String) {
                return Long.parseLong((String) principal);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el ID del usuario actual: " + e.getMessage());
        }
        return null;
    }
}
