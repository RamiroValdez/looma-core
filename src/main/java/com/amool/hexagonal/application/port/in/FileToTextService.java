package com.amool.hexagonal.application.port.in;

public interface FileToTextService {

    String extractText(byte[] fileBytes, String filename);
}
