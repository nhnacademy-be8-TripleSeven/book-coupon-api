package com.nhnacademy.bookapi.elasticsearch.scheduler;

import com.nhnacademy.bookapi.elasticsearch.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularityScheduler {

    private final BookSearchService bookSearchService;

    @Scheduled(cron = "0 0 12 * * *")
    public void popularityScheduler() {
        log.info("Popularity Scheduler started");
        bookSearchService.bookPopularityDbUpdate();
        log.info("Popularity Scheduler finished");
    }
}
