package com.camunda.training.delegate;

import com.camunda.training.service.TwitterService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CreateTweetDelegate implements JavaDelegate {
//    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        TwitterService twitter = new TwitterService();


        String content = (String) delegateExecution.getVariable("content");

        if (content.equals("Network error")) {
            throw new RuntimeException("simulated network error");
        }

        twitter.updateStatus(content);
    }
}

