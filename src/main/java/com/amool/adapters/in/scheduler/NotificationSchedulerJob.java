package com.amool.adapters.in.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amool.application.usecases.SaveNotificationUseCase;

@Component
public class NotificationSchedulerJob {
    private static final Logger log = LoggerFactory.getLogger(NotificationSchedulerJob.class);
    private final SaveNotificationUseCase saveNotificationUseCase;

    public NotificationSchedulerJob(SaveNotificationUseCase saveNotificationUseCase) {
        this.saveNotificationUseCase = saveNotificationUseCase;
        log.info("Iniciando NotificationSchedulerJob");
    }

    @Scheduled(fixedRate = 15000, initialDelay = 5000)
    public void run() {
            int count = saveNotificationUseCase.publishNotification(100);
            
            if (count > 0) {
            log.info("Published {} scheduled notification(s)", count);
        }
    }
    
}
