package com.amool.hexagonal.adapters.in.rest.dtos;

import java.util.Date;
import java.util.List;

public class WorkResponseDto {
    String title;
    String description;
    String cover;
    String banner;
    String state;
    Date publicationDate;
    Double price;
    List<ChapterDto> chapters;
    List<CategoryDto> categories;
}
