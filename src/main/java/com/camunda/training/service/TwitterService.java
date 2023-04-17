package com.camunda.training.service;

import com.camunda.training.delegate.CreateTweetDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TwitterService {

    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());

    public void updateStatus(String content) {
        LOGGER.info("Tweet: " + content);
    }
}
