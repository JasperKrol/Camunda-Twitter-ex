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

//    @Test
//    @Deployment(resources = "ex5-UserTask.bpmn")
//    void happyPathEx5() {
//
//        // Create a HashMap to put in variables for the process instance
//        RuntimeService runtimeService = extension.getRuntimeService();
//
//        // Start process with Java API and variables
//        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("ex5-UserTask.bpmn", variables);
//
//
//        List<Task> taskList = taskService()
//                .createTaskQuery()
//                .taskCandidateGroup("management")
//                .processInstanceId(processInstance.getId())
//                .list();
//        // Make assertions on the process instance
//
//
//        assertThat(taskList).isNotNull();
//        assertThat(taskList).hasSize(1);
//
//        Task task = taskList.get(0);
//
//
//        Map<String, Object> approvedMap = new HashMap<String, Object>();
//        approvedMap.put("approved", true);
//        taskService().complete(task.getId(), approvedMap);
//
//        assertThat(processInstance).isEnded();
//
//    }

//    @Test
//    @Deployment
//    public void extensionUsageExample() {
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        runtimeService.startProcessInstanceByKey("extensionUsage");
//
//        TaskService taskService = processEngine.getTaskService();
//        Task task = taskService.createTaskQuery().singleResult();
//        assertThat(task.getName()).isEqualTo("My Task");
//
//        taskService.complete(task.getId());
//        assertThat(runtimeService.createProcessInstanceQuery().count()).isEqualTo(0);
//    }
}
