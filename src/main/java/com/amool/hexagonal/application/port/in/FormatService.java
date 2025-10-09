package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Format;

import java.util.List;

public interface FormatService {

    List<Format> getAllFormats();

}
