package com.amool.hexagonal.config;

import com.amool.hexagonal.application.port.in.ImagesService;
import com.amool.hexagonal.application.service.DowloadImagesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    @Bean
    public DowloadImagesService downloadImagesService(ImagesService imagesService) {
        return new DowloadImagesService(imagesService);
    }

}
