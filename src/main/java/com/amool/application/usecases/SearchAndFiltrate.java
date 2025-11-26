package com.amool.application.usecases;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchAndFiltrate {

    private final WorkPort workPort;
    private final FilesStoragePort filesStoragePort;

    public SearchAndFiltrate(WorkPort workPort, FilesStoragePort filesStoragePort) {
        this.workPort = workPort;
        this.filesStoragePort = filesStoragePort;
    }

  public Page<Work> execute(WorkSearchFilter filter, Pageable pageable) {
      Page<Work> page = workPort.findByFilters(filter, pageable);

      List<Work> filtered = page.getContent().stream()
              .filter(Objects::nonNull)
              .filter(this::hasPublishedChapter)
              .map(this::modifyCoverAndBannerToUrl)
              .collect(Collectors.toList());

      return new PageImpl<>(filtered, pageable, filtered.size());
  }

  private Work modifyCoverAndBannerToUrl(Work work) {
      try {
          modifyFieldToUrl(work, "cover");
          modifyFieldToUrl(work, "banner");
      } catch (NoSuchFieldException | IllegalAccessException e) {}
      return work;
  }

  private void modifyFieldToUrl(Work work, String fieldName) throws NoSuchFieldException, IllegalAccessException {
      java.lang.reflect.Field field = work.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      Object value = field.get(work);
      if (value instanceof String) {
          String key = (String) value;
          String publicUrl = filesStoragePort.obtainPublicUrl(key);
          field.set(work, publicUrl);
      }
  }

  
  private boolean hasPublishedChapter(Work work) {
      if (work == null || work.getChapters() == null || work.getChapters().isEmpty()) return false;
      return work.getChapters().stream()
              .filter(Objects::nonNull)
              .map(ch -> ch.getPublicationStatus())
              .filter(Objects::nonNull)
              .anyMatch(status -> "PUBLISHED".equalsIgnoreCase(status));
  }

}
