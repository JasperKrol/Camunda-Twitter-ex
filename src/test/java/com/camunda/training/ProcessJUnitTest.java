package com.camunda.training;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@ExtendWith(ProcessEngineCoverageExtension.class)
class ProcessJUnitTest {

    // provide a field where the process engine gets injected
    ProcessEngine processEngine;

    @Test
    @Deployment(resources = "ex9-externalservice.bpmn")
    void testHappyPath() {

        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("content", "Exercise 4 test - YOUR NAME HERE");

        // Start process with Java API and variables

        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("ex9-form", variables);
        assertThat(processInstance).isWaitingAt("review-tweet");

        List<Task> taskList = taskService()
                .createTaskQuery()
                .taskCandidateGroup("management")
                .processInstanceId(processInstance.getId())
                .list();

        Task task = taskList.get(0);
//        assertThat(taskList).isNotNull();
//        assertThat(taskList).hasSize(1);

        // Make assertions on the process instance
        Map<String, Object> approvedMap = new HashMap<>();
        approvedMap.put("approved", true);
        taskService().complete(task.getId(), approvedMap);

        assertThat(processInstance).isWaitingAt("Send_tweet");
        List<Job> jobList = jobQuery()
                .processInstanceId(processInstance.getId())
                .list();
//        assertThat(jobList).hasSize(1);
        Job job = jobList.get(0);
        execute(job);

    }

    @Test
    @Deployment(resources = "ex9-externalservice.bpmn")
    void testTweetRejected() {

        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);
        variables.put("content", "Exercise 8 test - YOUR NAME HERE");


        // Start process with Java API and variables

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("ex9-form")
                .setVariables(variables)
                .startAfterActivity(findId("Review Tweet"))
                .execute();

        assertThat(processInstance)
                .isWaitingAt(findId("Notify user of rejection"))
                .externalTask()
                .hasTopicName("notification");
        complete(externalTask());

        // Make assertions on the process instance
        assertThat(processInstance).isEnded().hasPassed(findId("Tweet declined"));
    }

    @Test
    @Deployment(resources = "ex10-message.bpmn")
    void super_User_TwitterPost() {

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("content", "This is the 11th exercise to test");
//        In your new method start a process by creating a message and correlating it with an event. Be sure to add a content variable. Given that superuserTweet is correlated with a start event, it will simply start the process.

        ProcessInstance processInstance = runtimeService()
                .createMessageCorrelation("superUserTweet")
                .setVariable("content", "My Exercise 11 Tweet (ADD YOUR NAME HERE)- " + System.currentTimeMillis())
                .correlateWithResult()
                .getProcessInstance();

        assertThat(processInstance).isStarted();
//        runtimeService()
//                .createMessageCorrelation("tweetWithdrawn")
//                .correlateWithResult();

        // get the job
        List<Job> jobList = jobQuery()
                .processInstanceId(processInstance.getId())
                .list();

        // execute the job
        Job job = jobList.get(0);
        execute(job);

        assertThat(processInstance).isEnded();

    }

    @Test
    @Deployment(resources = "ex10-message.bpmn")
    public void tests_if_tweet_hasBeen_withdrawn() {
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("content", "Test tweetWithdrawn message");
            ProcessInstance processInstance = runtimeService()
                    .startProcessInstanceByKey("ex11", varMap);
            assertThat(processInstance).isStarted().isWaitingAt(findId("Review Tweet"));
            runtimeService()
                    .createMessageCorrelation("tweetWithdrawn")
                    .processInstanceVariableEquals("content", "Test tweetWithdrawn message")
                    .correlateWithResult();
            assertThat(processInstance).isEnded();
    }
}
