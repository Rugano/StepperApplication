package mta.course.java.stepper.step.api;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;

public interface DataDefinitionDeclaration {
    String getName();
    DataNecessity necessity();
    String userString();
    DataDefinition dataDefinition();
    String getAlias();
    public void setAliasName(String new_name);
    public void addStep(StepUsageDeclaration step);
    public String displaySteps();

}
