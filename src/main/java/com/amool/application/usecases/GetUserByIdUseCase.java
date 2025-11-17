package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

import java.util.Optional;

public class GetUserByIdUseCase {

    private final LoadUserPort loadUserPort;
    private final AwsS3Port awsS3Port;

    public GetUserByIdUseCase(LoadUserPort loadUserPort, AwsS3Port awsS3Port) {
        this.loadUserPort = loadUserPort;
        this.awsS3Port = awsS3Port;
    }

    public Optional<User> execute(Long userId) {
        Optional<User> user = loadUserPort.getById(userId);

        if(user.isPresent()) {
            String photoUrl = awsS3Port.obtainPublicUrl(user.get().getPhoto());
            user.get().setPhoto(photoUrl);
            return user;
        } else {
            return Optional.empty();
        }
    }
}
