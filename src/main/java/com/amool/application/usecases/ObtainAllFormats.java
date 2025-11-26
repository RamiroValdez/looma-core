package com.amool.application.usecases;

import com.amool.application.port.out.FormatPort;
import com.amool.domain.model.Format;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ObtainAllFormats {

    private final FormatPort formatPort;

    public ObtainAllFormats(FormatPort formatPort) {
        this.formatPort = formatPort;
    }

    public List<Format> execute() {

        List<Format> formats = this.formatPort.getAll();

        List<Format> mutableFormats = new ArrayList<>(formats);

        mutableFormats.sort(Comparator.comparing(Format::getName));

        return mutableFormats;
    }

}
