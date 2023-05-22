package mta.course.java.stepper.step.api;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStepDefinition implements StepDefinition {

    private final String stepName;
    public String aliasName;
    private final boolean readonly;
    private List<DataDefinitionDeclaration> inputs;
    private List<DataDefinitionDeclaration> outputs;
    protected long duration;
    protected StepResult result;
    protected String summary;
    protected int used = 0;

    public AbstractStepDefinition(String stepName, boolean readonly) {
        this.stepName = stepName;
        this.readonly = readonly;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }



    protected void addInput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        inputs.add(dataDefinitionDeclaration);
    }

    protected void addOutput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        outputs.add(dataDefinitionDeclaration);
    }
    public int getUsed(){return used;}

    @Override
    public String name() {
        return stepName;
    }

    @Override
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public String getAliasName(){
        return aliasName;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public List<DataDefinitionDeclaration> inputs() {
        return inputs;
    }

    @Override
    public List<DataDefinitionDeclaration> outputs() {
        return outputs;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public StepResult getResult() {
        return result;
    }

    public void setResult(StepResult result) {
        this.result = result;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public AbstractStepDefinition clone() {
        try {
            AbstractStepDefinition clone = (AbstractStepDefinition) super.clone();
            clone.inputs = new ArrayList<>(inputs);
            clone.outputs = new ArrayList<>(outputs);
            clone.duration = duration;
            clone.result = result;
            clone.summary = summary;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();  // should not happen
        }
    }
}
