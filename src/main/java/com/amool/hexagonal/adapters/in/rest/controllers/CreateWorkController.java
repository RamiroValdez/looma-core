package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.CreateWorkDto;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/create-work")
public class CreateWorkController {

    private final WorkService workService;


    public CreateWorkController(WorkService workService) {
        this.workService = workService;
    }


    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<Long> saveWork(
            @RequestPart("work") CreateWorkDto createWorkDto,
            @RequestPart("cover") MultipartFile coverFile,
            @RequestPart("banner") MultipartFile bannerFile,
            @AuthenticationPrincipal JwtUserPrincipal principal
            ) {
                try {
                    Long result = this.workService.createWork(
                            createWorkDto.title(),
                            createWorkDto.description(),
                            createWorkDto.categoryIds(),
                            createWorkDto.formatId(),
                            createWorkDto.originalLanguageId(),
                            createWorkDto.tagIds(),
                            coverFile,
                            bannerFile,
                            principal.getUserId());

                    return  ResponseEntity.ok(result);

                } catch (Exception e) {
                    return ResponseEntity.badRequest().build();
                }
    }
}
