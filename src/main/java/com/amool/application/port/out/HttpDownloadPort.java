package com.amool.application.port.out;

import java.io.IOException;

public interface HttpDownloadPort {
    byte[] downloadImage(String url) throws IOException, InterruptedException;
}

