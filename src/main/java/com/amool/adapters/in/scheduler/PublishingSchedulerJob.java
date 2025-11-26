package com.amool.adapters.in.scheduler;

import com.amool.application.service.PublishingSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PublishingSchedulerJob {

    private static final Logger log = LoggerFactory.getLogger(PublishingSchedulerJob.class);
    private final PublishingSchedulerService publishingSchedulerService;

    public PublishingSchedulerJob(PublishingSchedulerService publishingSchedulerService) {
        this.publishingSchedulerService = publishingSchedulerService;
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    public void run() {
        int count = publishingSchedulerService.publishDueChapters(100);
        if (count > 0) {
            log.info("Published {} scheduled chapter(s)", count);
        }
    }
}
