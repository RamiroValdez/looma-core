package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;

public class ObtainPresignedUrl {

    private final AwsS3Port awsS3Port;

    public ObtainPresignedUrl(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
    }

    public String execute(String fileName) {
        return this.awsS3Port.obtainFilePresignedUrl(fileName);
    }

}
