package mta.course.java.stepper.flow.execution;

import javafx.util.Pair;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    public Duration totalTime;
    private FlowExecutionResult flowExecutionResult;
    private Map<Pair<String, String>, String> mapping;
    private Map<Pair<String, String> , Pair<String, String>> custom_mapping;

    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        this.mapping = new HashMap<>();
        this.mapping = this.getFlowDefinition().get_alias_mapping();
        this.custom_mapping = new HashMap<>();
        this.custom_mapping = this.getFlowDefinition().get_custom_mapping();
    }

    public String getUniqueId() {
        return uniqueId;
    }


    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
    public Map<Pair<String, String>, String> getAliasMapping(){return this.mapping;}
    public Map<Pair<String, String>, Pair<String, String>> getCustom_mapping(){return this.custom_mapping;}
    public void setResult(FlowExecutionResult result){flowExecutionResult = result;}
}
