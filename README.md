# Conductor Workflow Testing and CI/CD

## Unit and Regression testing workflows

### Unit Tests
Conductor workflows can be unit tested using `POST /workflow/test` endpoint.
The approach is similar to how you unit test using Mock objects in Java or similar languages.

#### Why Unit Test Workflows?
Unit tests allows you to test for the correctness of the workflow definition ensuring:
1. Given a specific input workflow reaches the terminal state in COMPLETED or FAILED state
2. Given a specific input, the workflow executes specific set of tasks. This is useful for testing branching and dynamic forks
3. Task inputs are wired correctly - e.g. if a task receives its input from the output of another task, this can be verified using the unit test.

### Unit Testing Workflows
Java SDK provides the following method that allows testing a workflow definition against mock inputs:
```java
    public abstract Workflow testWorkflow(WorkflowTestRequest testRequest);
```
The actual workflow is executed on a real Conductor server ensuring you are testing the behavior that will match the ACTUAL executon of the server.

### Setting up Conductor server for testing
Tests can be run against a remote server (useful when running integration tests) or local containerized instance.  Recommended approach is to use `testcontainers`.
See [WorkflowTests.java](src/test/java/io/orkes/conductor/cicd/workflows/WorkflowTests.java#L131) for a fully working example of how to run tests locally. 



## Deploying Workflow and Task Metadata