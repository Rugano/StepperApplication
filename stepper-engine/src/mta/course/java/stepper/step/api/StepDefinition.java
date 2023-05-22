package mta.course.java.stepper.step.api;

import mta.course.java.stepper.flow.execution.context.StepExecutionContext;

import java.util.List;

public interface StepDefinition extends Cloneable {
    String name();
    void setAliasName(String aliasName);
    String getAliasName();
    boolean isReadonly();
    List<DataDefinitionDeclaration> inputs();
    List<DataDefinitionDeclaration> outputs();
    StepResult invoke(StepExecutionContext context);
    String getSummary();
    StepResult getResult();
    long getDuration();
    int getUsed();
    StepDefinition clone();
}
