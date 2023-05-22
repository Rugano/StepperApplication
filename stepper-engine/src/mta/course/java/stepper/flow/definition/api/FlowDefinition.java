package mta.course.java.stepper.flow.definition.api;

import javafx.util.Pair;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.List;
import java.util.Map;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();
    Map<Pair<String, String>,Pair<String, String>> get_custom_mapping();
    Map<Pair<String, String>, String> get_alias_mapping();
    Boolean validateFlowStructure();
    List<DataDefinitionDeclaration> getFlowFreeInputs();
    List<Pair<String, DataDefinitionDeclaration>> getFlowFreeInputsAndNames();
    int getUsed();
    void UsedInc();
    String getSummaryOfXML();
}
