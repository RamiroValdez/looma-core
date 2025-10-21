package com.amool.application.usecases;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchAndFiltrateUseCase {

    private final WorkPort workPort;
    private final AwsS3Port awsS3Port;

    public SearchAndFiltrateUseCase(WorkPort workPort, AwsS3Port awsS3Port) {
        this.workPort = workPort;
        this.awsS3Port = awsS3Port;
    }

  public Page<Work> execute(WorkSearchFilter filter, Pageable pageable) {
      Page<Work> page = workPort.findByFilters(filter, pageable);
      return page.map(this::modifyCoverAndBannerToUrl);
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
          String publicUrl = awsS3Port.obtainPublicUrl(key);
          field.set(work, publicUrl);
      }
  }

}
