package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.step.api.StepDefinition;


public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private final StepDefinition stepDefinition;
    private final boolean skipIfFail;
    private final String stepName; //Alias Name of Step

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
    }


    @Override
    public String getFinalStepName() {
        return stepName;
    }

    @Override
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    @Override
    public boolean skipIfFail() {
        return skipIfFail;
    }

    @Override
    public String toString(){
        String name_string;
        String is_read_only_string;
        String original_name = getStepDefinition().name();
        if (!original_name.equals(getFinalStepName()))
            name_string = "\n   Step name is: " +original_name +" alias name is: " +getFinalStepName();
        else
            name_string = "\n   Step name is: " +original_name;
        if (getStepDefinition().isReadonly())
            is_read_only_string = "\n   Step is read only";
        else
            is_read_only_string = "\n   Step is NOT read only";


        return name_string +is_read_only_string;

    }
}
