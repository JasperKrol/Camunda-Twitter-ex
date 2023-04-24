package com.camunda.training.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static java.lang.String.*;

@Service
public class TwitterService {

    private final Logger LOGGER = LoggerFactory.getLogger(TwitterService.class.getName());

    public void updateStatus(String content) {
        LOGGER.info("Tweet: {}", content);
    }
}
