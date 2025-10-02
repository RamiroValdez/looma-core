package com.amool.hexagonal.adapters.out.googlecloudapi;

import com.amool.hexagonal.application.port.out.GoogleTranslatePort;
import com.google.cloud.translate.*;
import org.springframework.stereotype.Component;

@Component
public class GoogleTranslationAdapter implements GoogleTranslatePort {

    private final Translate translate;

    public GoogleTranslationAdapter() {
        this.translate = TranslateOptions.getDefaultInstance().getService();
    }

    @Override
    public String translateText(String text, String targetLanguage) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage),
                Translate.TranslateOption.model("nmt"));
        return translation.getTranslatedText();
    }
}
