package com.amool.application.usecases;

import java.util.Optional;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

public class GetUserPhoto {

    private final AwsS3Port awsS3Port;
    private final LoadUserPort loadUserPort;

    public GetUserPhoto(AwsS3Port awsS3Port, LoadUserPort loadUserPort) {
        this.awsS3Port = awsS3Port;
        this.loadUserPort = loadUserPort;
    }

    public String execute(Long userId) {
        Optional<User> user = loadUserPort.getById(userId);
        return awsS3Port.obtainPublicUrl(user.get().getPhoto());
    }
    
}
