package com.amool.application.port.out;

import com.amool.domain.model.Format;

import java.util.List;
import java.util.Optional;

public interface FormatPort {

    Optional<Format> getById(Long formatId);

    List<Format> getAll();

}
