/*
 * Copyright 2023 Orkes, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.orkes.conductor.cicd.workflows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;

import io.orkes.conductor.client.model.WorkflowTestRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates how to test workflows that contain sub-workflows
 */
public class SubWorkflowTest extends AbstractWorkflowTests {


    @Test
    public void verifySubWorkflowExecutions() throws IOException {
        WorkflowDef def = getWorkflowDef("/workflows/kitchensink.json");
        assertNotNull(def);

        WorkflowDef subWorkflowDef = getWorkflowDef("/workflows/PopulationMinMax.json");
        metadataClient.registerWorkflowDef(subWorkflowDef);


        WorkflowTestRequest testRequest = getWorkflowTestRequest(def);

        //The following are the dynamic tasks which are not present in the workflow definition but are created by dynamic fork
        testRequest.getTaskRefToMockOutput().put("_x_test_worker_0_0", List.of(new WorkflowTestRequest.TaskMock()));
        testRequest.getTaskRefToMockOutput().put("_x_test_worker_0_1", List.of(new WorkflowTestRequest.TaskMock()));
        testRequest.getTaskRefToMockOutput().put("_x_test_worker_0_2", List.of(new WorkflowTestRequest.TaskMock()));
        testRequest.getTaskRefToMockOutput().put("simple_task_1__1", List.of(new WorkflowTestRequest.TaskMock()));
        testRequest.getTaskRefToMockOutput().put("simple_task_5", List.of(new WorkflowTestRequest.TaskMock()));


        Workflow execution = workflowClient.testWorkflow(testRequest);
        assertNotNull(execution);


        //That the workflow executes a wait task
        assertTrue(execution.getTasks().stream().anyMatch(t -> t.getReferenceTaskName().equals("wait")));

        //That the call_made variable was set to True
        assertEquals(true, execution.getVariables().get("call_made"));


    }


}
