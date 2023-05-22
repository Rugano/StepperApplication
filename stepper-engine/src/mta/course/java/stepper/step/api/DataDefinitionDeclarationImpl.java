package mta.course.java.stepper.step.api;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;

import java.util.ArrayList;
import java.util.List;

public class DataDefinitionDeclarationImpl implements DataDefinitionDeclaration {

    private final String name;
    private String alias_name;
    private final DataNecessity necessity;
    private final String userString;
    private final DataDefinition dataDefinition;
    private List<StepUsageDeclaration> my_steps;

    public DataDefinitionDeclarationImpl(String name, DataNecessity necessity, String userString, DataDefinition dataDefinition) {
        this.name = name;
        this.alias_name = name;
        this.necessity = necessity;
        this.userString = userString;
        this.dataDefinition = dataDefinition;
        this.my_steps = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }
    public String getAlias(){return alias_name;}

    @Override
    public DataNecessity necessity() {
        return necessity;
    }

    @Override
    public String userString() {
        return userString;
    }

    @Override
    public DataDefinition dataDefinition() {
        return dataDefinition;
    }
    @Override
    public void setAliasName(String new_name){this.alias_name = new_name;}
    public void addStep(StepUsageDeclaration step){
        my_steps.add(step);
    }
    public String displaySteps(){
        StringBuilder sb = new StringBuilder();
        for(StepUsageDeclaration step : my_steps){
            sb.append("\t").append(step.getFinalStepName()).append("\n");
        }
        String result = sb.toString();
        return result;
    }

}
