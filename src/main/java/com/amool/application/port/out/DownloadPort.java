package com.amool.application.port.out;

import java.io.IOException;

public interface DownloadPort {
    byte[] downloadImage(String url) throws IOException, InterruptedException;
}

