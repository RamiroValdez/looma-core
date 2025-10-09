package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.FormatService;
import com.amool.hexagonal.application.port.out.FormatPort;
import com.amool.hexagonal.domain.model.Format;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FormatServiceImpl implements FormatService {

    private FormatPort formatPort;

    public FormatServiceImpl(FormatPort formatPort) {
        this.formatPort = formatPort;
    }

    @Override
    public List<Format> getAllFormats() {

        List<Format> formats = this.formatPort.getAll();

        List<Format> mutableFormats = new ArrayList<>(formats);

        mutableFormats.sort(Comparator.comparing(Format::getName));

        return formats;
    }
}
