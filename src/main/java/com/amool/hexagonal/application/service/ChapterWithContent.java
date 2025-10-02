package com.amool.hexagonal.application.service;

import com.amool.hexagonal.domain.model.Chapter;

public record ChapterWithContent(Chapter chapter, String content) {}
