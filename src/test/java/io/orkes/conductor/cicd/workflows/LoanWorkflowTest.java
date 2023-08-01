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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;

import io.orkes.conductor.client.model.WorkflowTestRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test a workflow with inputs read from a file.
 */
public class LoanWorkflowTest extends AbstractWorkflowTests {

    /**
     * Uses mock inputs to verify the workflow execution and input/outputs of the tasks
     */
    @Test
    public void verifyWorkflowExecutionWithMockInputs() throws IOException {
        WorkflowDef def = getWorkflowDef("/workflows/calculate_loan_workflow.json");
        assertNotNull(def);
        Map<String, List<WorkflowTestRequest.TaskMock>> testInputs = getTestInputs("/test_data/loan_workflow_input.json");
        assertNotNull(testInputs);

        WorkflowTestRequest testRequest = new WorkflowTestRequest();
        testRequest.setWorkflowDef(def);


        LoanWorkflowInput workflowInput = new LoanWorkflowInput();
        workflowInput.setUserEmail("user@example.com");
        workflowInput.setLoanAmount(new BigDecimal(11_000));
        testRequest.setInput(objectMapper.convertValue(workflowInput, Map.class));

        testRequest.setTaskRefToMockOutput(testInputs);
        testRequest.setName(def.getName());
        testRequest.setVersion(def.getVersion());

        Workflow execution = workflowClient.testWorkflow(testRequest);
        assertNotNull(execution);

        //Assert that the workflow completed successfully
        assertEquals(Workflow.WorkflowStatus.COMPLETED, execution.getStatus());

        //Ensure the inputs were captured correctly
        assertEquals(workflowInput.getLoanAmount().toString(), String.valueOf(execution.getInput().get("loanAmount")));
        assertEquals(workflowInput.getUserEmail(), execution.getInput().get("userEmail"));

        //A total of 7 tasks were executed
        assertEquals(7, execution.getTasks().size());

        Task fetchUserDetails = execution.getTasks().get(0);
        Task getCreditScore = execution.getTasks().get(1);
        Task calculateLoanAmount = execution.getTasks().get(2);
        Task phoneNumberValidAttempt1 = execution.getTasks().get(4);
        Task phoneNumberValidAttempt2 = execution.getTasks().get(5);
        Task phoneNumberValidAttempt3 = execution.getTasks().get(6);

        //fetch user details received the correct input from the workflow
        assertEquals(workflowInput.getUserEmail(), fetchUserDetails.getInputData().get("userEmail"));

        //And that the task produced the right output
        long userAccountNo = 12345L;
        assertEquals(userAccountNo, fetchUserDetails.getOutputData().get("userAccount"));

        //get credit score received the right account number from the output of the fetch user details
        assertEquals(userAccountNo, getCreditScore.getInputData().get("userAccountNumber"));
        long expectedCreditRating = 750;

        //The task produced the right output
        assertEquals(expectedCreditRating, getCreditScore.getOutputData().get("creditRating"));

        //Calculate loan amount gets the right loan amount from workflow input
        assertEquals(workflowInput.getLoanAmount().toString(), String.valueOf(calculateLoanAmount.getInputData().get("loanAmount")));

        //Calculate loan amount gets the right credit rating from the previous task
        assertEquals(expectedCreditRating, calculateLoanAmount.getInputData().get("creditRating"));

        long authorizedLoanAmount = 10_000;
        assertEquals(authorizedLoanAmount, calculateLoanAmount.getOutputData().get("authorizedLoanAmount"));

        assertEquals(false, phoneNumberValidAttempt1.getOutputData().get("valid"));
        assertEquals(false, phoneNumberValidAttempt2.getOutputData().get("valid"));
        assertEquals(true, phoneNumberValidAttempt3.getOutputData().get("valid"));

        //Finally, lets verify the workflow outputs
        assertEquals(userAccountNo, execution.getOutput().get("accountNumber"));
        assertEquals(expectedCreditRating, execution.getOutput().get("creditRating"));
        assertEquals(authorizedLoanAmount, execution.getOutput().get("authorizedLoanAmount"));
        // Workflow output takes the latest iteration output of a loopOver task.
        assertEquals(true, execution.getOutput().get("phoneNumberValid"));

        System.out.println(execution);
    }


}
