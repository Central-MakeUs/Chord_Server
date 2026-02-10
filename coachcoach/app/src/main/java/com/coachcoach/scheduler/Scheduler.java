package com.coachcoach.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Scheduler {

    @Scheduled(cron = "0 0 22 * * Sun")
    public void scheduler() {

    }
}
