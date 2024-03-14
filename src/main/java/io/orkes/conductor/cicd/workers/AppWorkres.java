package io.orkes.conductor.cicd.workers;

import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

public class AppWorkres {

    @WorkerTask("greetings")
    public String greetings(@InputParam("name") String name) {
        return "Hello, " + name;
    }
}
