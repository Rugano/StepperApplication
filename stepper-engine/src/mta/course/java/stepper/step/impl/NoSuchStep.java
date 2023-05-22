package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.StepResult;

public class NoSuchStep extends AbstractStepDefinition {

    public NoSuchStep() {
        super("NoSuchStep",true);
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        return null;
    }
}
