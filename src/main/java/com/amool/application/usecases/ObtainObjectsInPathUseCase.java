package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class ObtainObjectsInPathUseCase {

    private final AwsS3Port awsS3Port;

    public ObtainObjectsInPathUseCase(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
    }

    public List<S3Object> execute(String path) {
        return this.awsS3Port.obtainObjectsInPath(path);
    }

}
