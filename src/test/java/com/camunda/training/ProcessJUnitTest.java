package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

@ExtendWith(ProcessEngineExtension.class)
class ProcessJUnitTest {

    // TODO: 17-04-2023 fails because the task is still usertask-> can be set as manualtask so it will skipp

    @Test
    @Deployment(resources = "diagram_1.bpmn")
    void testHappyPath() {

        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);

        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("twitter-exercise2b", variables);

        // Make assertions on the process instance
        assertThat(processInstance).isEnded();
    }

}
