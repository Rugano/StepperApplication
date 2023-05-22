package mta.course.java.stepper.flow.definition.api;

import javafx.util.Pair;
import mta.course.java.stepper.generated.STCustomMapping;
import mta.course.java.stepper.generated.STFlow;
import mta.course.java.stepper.generated.STFlowLevelAlias;
import mta.course.java.stepper.generated.STStepInFlow;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataNecessity;

import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {
    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<StepUsageDeclaration> steps;
    private Map<Pair<String, String>, String> mapping_of_alias;//<Stepname, source> alias of source
    private Map<Pair<String, String>, Pair<String, String>> custom_mapping;
//    private Map<String, DataDefinitionDeclaration> free_inputs;
    private List<Pair<String, DataDefinitionDeclaration>> free_inputs;//<step, DDD>
    private Map<Pair<String, String>, DataDefinitionDeclaration>  all_of_the_outputs;
    //<Step name ,DD final name >,acutal DD >
    private Map<Pair<String, String>, DataDefinitionDeclaration>  all_of_the_inputs;
    private Map<String, DataDefinitionDeclaration> all_of_the_steps_outputs;
    private int used = 0;
    public String summary_of_XML;

    public FlowDefinitionImpl(STFlow st_flow) {
        this.name = st_flow.getName();
        this.description = st_flow.getSTFlowDescription();
        this.flowOutputs = new ArrayList<>(Arrays.asList(st_flow.getSTFlowOutput().split(",")));
        this. all_of_the_outputs = new HashMap<>();
        this.all_of_the_steps_outputs = new HashMap<>();
        this.all_of_the_inputs = new HashMap<>();
        this.steps = new ArrayList<>();
        this.mapping_of_alias = new HashMap<>();
        this.custom_mapping = new HashMap<>();
        this.summary_of_XML = "";
        this.free_inputs = new ArrayList<>();
        //Creation of steps
        for (STStepInFlow step_inf : st_flow.getSTStepsInFlow().getSTStepInFlow()) {
            String step_inf_name = step_inf.getName();
            String step_inf_alias_name = step_inf.getName();
            boolean continue_if_failing = false;
            if (step_inf.getAlias() != null)
                step_inf_alias_name = step_inf.getAlias();
            if (step_inf.isContinueIfFailing() != null)
                continue_if_failing = step_inf.isContinueIfFailing();
            this.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.fromIdentifier(step_inf_name, step_inf_alias_name), continue_if_failing, step_inf_alias_name));
        }

        //Flow Level Aliasing
        if (st_flow.getSTFlowLevelAliasing() != null) {
            for (STFlowLevelAlias alias : st_flow.getSTFlowLevelAliasing().getSTFlowLevelAlias()) {
                Pair<String, String> pair = new Pair<>(alias.getStep(), alias.getSourceDataName());
                mapping_of_alias.put(pair, alias.getAlias());
            }
        }


            for (StepUsageDeclaration step : steps) {
                for (DataDefinitionDeclaration dd1 : step.getStepDefinition().outputs()) {
                        Pair<String, String> pair = new Pair<>(step.getFinalStepName(), getActualName(step.getFinalStepName(), dd1.getName()));
                        all_of_the_outputs.put(pair, dd1);
                }
                for (DataDefinitionDeclaration dd2 : step.getStepDefinition().inputs()){
                    Pair<String, String> pair = new Pair<>(step.getFinalStepName(), getActualName(step.getFinalStepName(), dd2.getName()));
                    all_of_the_inputs.put(pair, dd2);
                }
            }

       //Custom Mapping
        List<STCustomMapping> list_cm = null;
        if (st_flow.getSTCustomMappings() != null) {
            list_cm = st_flow.getSTCustomMappings().getSTCustomMapping();
        }
        if (list_cm != null) {
            for (STCustomMapping custom_map : list_cm) {
                Pair<String, String> target = new Pair<>(custom_map.getTargetStep(), custom_map.getTargetData());
                Pair<String, String> source = new Pair<>(custom_map.getSourceStep(), custom_map.getSourceData());
                custom_mapping.put(target, source); //<TargetStep, TargetDD>, <SourceStep, SourceDD>
            }
        }

        //Automatic Mapping validation?
        boolean need_to_put;
        boolean free_input = true; //if not then it's free input
        List<StepUsageDeclaration> steps = getFlowSteps();
        for (int i = steps.size() - 1; i >= 0; i--) {
            StepUsageDeclaration step = steps.get(i);
            List<DataDefinitionDeclaration> currInputs = steps.get(i).getStepDefinition().inputs();
            for (int k = currInputs.size() - 1; k >= 0; k--) {
                free_input = true;
                DataDefinitionDeclaration currInput = currInputs.get(k);
                currInput.addStep(step);
                for (int j = i - 1; j >= 0; j--) {
                    List<DataDefinitionDeclaration> currOutputs = steps.get(j).getStepDefinition().outputs();
                    for (int m = currOutputs.size() - 1; m >= 0; m--) {
                        DataDefinitionDeclaration currOutput = currOutputs.get(m);
                        Pair<String, String> target = new Pair<>(steps.get(i).getFinalStepName(), getActualName(step.getFinalStepName(), currInput.getAlias()));
                        Pair<String, String> source = new Pair<>(steps.get(j).getFinalStepName(), getActualName(steps.get(j).getFinalStepName(), currOutput.getAlias()));
                        Pair<String, String> answer = custom_mapping.get(target);
                        if (source.equals(answer))
                            free_input = false;
                        else if (getActualName(steps.get(j).getFinalStepName(), currOutput.getAlias()).equals(getActualName(step.getFinalStepName(), currInput.getAlias()))
                                && currOutput.dataDefinition().getType().equals(currInput.dataDefinition().getType())) { //if the type and the name are equal.
                            //HERE I CHECK CUSTOM MAPPING!!!
                            //In context I APPLY!
                            free_input = false;
                        }
                    }
                }
                need_to_put = true;
//                if (free_input && !currInput.dataDefinition().isUserFriendly()) {
//                    System.out.println("There's a free input that's not user friendly..");
//                    System.out.println("Free inputs that's not user friendly is: " + getActualName(step.getFinalStepName(), currInput.getAlias()));
//                    System.out.println("The step that produced this input is: " + step.getFinalStepName());
//                }
            /*else*/if (free_input) {
                    List<Pair<String, DataDefinitionDeclaration>> list = getFlowFreeInputsAndNames();
                    for (Pair<String, DataDefinitionDeclaration> pair : list) {
                        if (getActualName(pair.getKey(), pair.getValue().getName()).equals(getActualName(step.getFinalStepName(), currInput.getName())) &&
                                pair.getValue().dataDefinition().getType().equals(currInput.dataDefinition().getType()))
                            need_to_put = false; //means its already exist.
                    }
//                    for (DataDefinitionDeclaration dd : free_inputs.values()) {
//                        //Checking if the currInput is already in free_inputs..
//                        if (dd.getAlias().equals(currInput.getAlias()) && dd.dataDefinition().getType().equals(currInput.dataDefinition().getType())) {
//                            need_to_put = false; //means its already exist.
//                        }
//                    }
                    if (need_to_put) {
                        Pair p = new Pair<>(step.getFinalStepName(), currInput);
                        free_inputs.add(p);
                    }
                }
            }
        }

        //Putting outputs for command number 2 in word
        for (StepUsageDeclaration step : steps) {
            for (DataDefinitionDeclaration dd : step.getStepDefinition().outputs()) {
                all_of_the_steps_outputs.put(step.getFinalStepName(), dd);
            }
        }
    }


    public void addFlowOutput(String outputName) {
        flowOutputs.add(outputName);
    }

    @Override
    public Boolean validateFlowStructure() {
        //3.
        for (StepUsageDeclaration step : steps) {
            if (!step.getStepDefinition().name().equals("Spend Some Time") && !step.getStepDefinition().name().equals("Collect Files In Folder")
                    && !step.getStepDefinition().name().equals("Files Deleter") && !step.getStepDefinition().name().equals("Files Renamer")
                    && !step.getStepDefinition().name().equals("Files Content Extractor") && !step.getStepDefinition().name().equals("CSV Exporter")
                    && !step.getStepDefinition().name().equals("Properties Exporter") && !step.getStepDefinition().name().equals("File Dumper")) {
                this.summary_of_XML = "3: There is a step that doesn't exist...";
                return false;
            }
        }
        //4.1
        for (int i = 0; i <steps.size(); i++) {
            StepUsageDeclaration step1 = steps.get(i);
            List<DataDefinitionDeclaration> outputs1 = steps.get(i).getStepDefinition().outputs();
            for (int k = 0; k <outputs1.size(); k++) {
                DataDefinitionDeclaration output1 = outputs1.get(k);
                String name1 = getActualName(step1.getFinalStepName(), output1.getName());
                for (int j = i + 1; j < steps.size(); j++) {
                    StepUsageDeclaration step2 = steps.get(j);
                    List<DataDefinitionDeclaration> outputs2 = steps.get(j).getStepDefinition().outputs();
                    for (int m = 0; m < outputs2.size() ; m++) {
                        DataDefinitionDeclaration output2 = outputs2.get(m);
                        String name2 = getActualName(step2.getFinalStepName(), output2.getName());
                        if (name1.equals(name2)){
                            this.summary_of_XML = "4.1: There are two outputs with the same name.. \n" +
                                    "and they are " +step1.getFinalStepName() +": " +name1 +"\n" +
                        "and " +step2.getFinalStepName() +": " +name2;
                            return false;
                            }
                        }
                    }
                }
        }

        //4.2
        for (Pair<String, DataDefinitionDeclaration> pair : free_inputs){
            if (!pair.getValue().dataDefinition().isUserFriendly() && pair.getValue().necessity().equals(DataNecessity.MANDATORY)){
                this.summary_of_XML = "4.2: There is a mandatory input that is not user friendly..";
                return false;
            }
        }


        //4.3
        for (Map.Entry<Pair<String, String>, Pair<String, String>> targetI_sourceO : custom_mapping.entrySet()){
            Pair<String, String> targetI = targetI_sourceO.getKey();
            Pair<String, String> sourceO = targetI_sourceO.getValue();
            boolean target_good = false;
            boolean source_good = false;
            int source_number = -1;
            int target_number = -1;
            for(int i = 0; i <steps.size(); i++){//just to make sure it's going over in the right order
                if(targetI.getKey().equals(steps.get(i).getFinalStepName())) {
                    target_good = true;
                    target_number = i;
                }
                if(sourceO.getKey().equals(steps.get(i).getFinalStepName())) {
                    source_good = true;
                    source_number = i;
                }
            }
            if (!target_good || !source_good){
                this.summary_of_XML = "4.3: customMappings has a reference to a step that doesn't exist";
                return false;
            }
            else if(source_number > target_number){
                this.summary_of_XML = "4.3: customMappings has a reference from a step to an earlier one";
                return false;
            }
            else{
                    Pair<String, String> target_pair = new Pair<>(steps.get(target_number).getFinalStepName(), targetI.getValue());
                    Pair<String, String> source_pair = new Pair<>(steps.get(source_number).getFinalStepName(), sourceO.getValue());
                    if (all_of_the_inputs.get(target_pair).dataDefinition().getType() !=
                            all_of_the_outputs.get(source_pair).dataDefinition().getType()) {
                        this.summary_of_XML = "4.3: customMappings trying to connect different types of datas";
                        return false;
                    }
            }
        }

        //<Stepname, source> alias of source
        //4.4
        boolean good5 = true;
        for (Map.Entry<Pair<String, String>, String> map : mapping_of_alias.entrySet()){
            boolean good6 = false;
            for (StepUsageDeclaration step : steps){
                if (step.getFinalStepName().equals(map.getKey().getKey()))
                    good6 = true;
            }
            if (!good6)
                good5=good6;
        }
        if (!good5){
            this.summary_of_XML = "4.4: FlowLevelAliasing to a step that doesn't exist..";
            return false;
        }


        //4.5
        boolean exist = true;
        for (String formal_output_name : getFlowFormalOutputs()){
            boolean good = false;
            for (Map.Entry<Pair<String, String>, DataDefinitionDeclaration> pair : all_of_the_outputs.entrySet()) {
                if (formal_output_name.equals(pair.getKey().getValue()))
                    good = true;
            }
            if (!good)
                exist = good;
        }
        if (!exist){
            this.summary_of_XML = "4.5: Flow output problem";
            return false;
        }

        //4.6
        for ( int i= 0; i < free_inputs.size(); i++){
            String name1 = free_inputs.get(i).getKey();
            DataDefinitionDeclaration dd1 = free_inputs.get(i).getValue();
            String actualname1 = getActualName(name1, dd1.getName());
            for(int j = i + 1; j < free_inputs.size(); j++){
                String name2 = free_inputs.get(j).getKey();
                DataDefinitionDeclaration dd2 = free_inputs.get(j).getValue();
                String actualname2 = getActualName(name2, dd2.getName());
                if (actualname1.equals(actualname2) && dd1.dataDefinition().getType().equals(dd2.dataDefinition().getType())
                && dd1.necessity() == DataNecessity.MANDATORY && dd2.necessity() == DataNecessity.MANDATORY){
                    this.summary_of_XML = "4.6: There are two mandatory free inputs with the same name" +
                    "\nThey are: \nStep1: " +name1 +" DD: "+getActualName(name1, dd1.getName()) +
                    "\nAnd Step2: " +name2 +" DD: " +getActualName(name2, dd2.getName());
                    return false;
                }
            }
        }
        return true;
    }
    public int getUsed(){return used;}
    public void UsedInc(){used++;}

    @Override
    public String getSummaryOfXML() {
        return this.summary_of_XML;
    }

    @Override
    public List<DataDefinitionDeclaration> getFlowFreeInputs() {
        List<DataDefinitionDeclaration> newList = new ArrayList();
        for (Pair<String, DataDefinitionDeclaration> pair : free_inputs)
            newList.add(pair.getValue());
        return newList;
    }
    //Returns the Alias and DDD of free_inputs
    public List<Pair<String, DataDefinitionDeclaration>> getFlowFreeInputsAndNames(){
        List<Pair<String, DataDefinitionDeclaration>> list = new ArrayList<>();
        for (Pair<String, DataDefinitionDeclaration> entry : free_inputs) {
            list.add(new Pair<>(getActualName(entry.getKey(), entry.getValue().getName()), entry.getValue()));
        }
        return list;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }

    @Override
    public Map<Pair<String, String>, String> get_alias_mapping() {
        return this.mapping_of_alias;
    }
    @Override
    public Map<Pair<String, String>,Pair<String, String>> get_custom_mapping(){
        return this.custom_mapping;
    }

    @Override
    public String toString() {
        boolean is_flow_read_only = true;
        for (StepUsageDeclaration step : getFlowSteps()) {
            if (!step.getStepDefinition().isReadonly())
                is_flow_read_only = false;
        }
        String flow_read_only_string;
        if (is_flow_read_only)
            flow_read_only_string = "\n4. Flow is read only";
        else
            flow_read_only_string = "\n4. Flow is NOT read only";
        return "Name of Flow: " + getName() +
                "\nFlow description: " + getDescription() +
                "\nFlow formal outputs: " + getFlowFormalOutputs()+//Can keep it like this [Because it's list]
                flow_read_only_string +
                "\n ---------------------------------------------------------------\n" + "the steps are:\n" +
                displayFlowSteps() +
                "\n ---------------------------------------------------------------\n" +
                "The free inputs are:" +
                displayFlowFreeInputs() +
                "\nThe outputs of all of the steps in this flow are: \n" +
                displayAllOfTheStepsOutputs();
    }

    public String displayFlowSteps() {
        StringBuilder sb = new StringBuilder();
        for (StepUsageDeclaration step : steps) {
            sb.append(step).append("\n");
        }
        return sb.toString();
    }

    public String displayFlowFreeInputs() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, DataDefinitionDeclaration> map : free_inputs) {
            String step = map.getKey();
            DataDefinitionDeclaration dd = map.getValue();
            String necessity_string;
            if (dd.necessity() == DataNecessity.MANDATORY)
                necessity_string = "MANDATORY";
            else
                necessity_string = "OPTIONAL";
           sb.append("    Final name is: ").append(getActualName(step, dd.getName())).
                   append("\n type is: ").append(dd.dataDefinition().getType().getSimpleName()).
                   append("\n the steps that are connected to me in all of the flows are\n{ \n").append(dd.displaySteps()).
                   append("}\n").append("   This input is: ").append(necessity_string).
                   append("\n ---------------------------------------------------------------\n");
        }
        return sb.toString();
    }

    public String displayAllOfTheStepsOutputs() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Pair<String, String>, DataDefinitionDeclaration> pair : all_of_the_outputs.entrySet()){
            DataDefinitionDeclaration dd = pair.getValue();
            String step_name = pair.getKey().getKey();
            sb.append("Name of the output: ").append(pair.getKey().getValue()).append("\n");
            sb.append("Type of output: ").append(dd.dataDefinition().getType()).append("\n");
            sb.append("Name of step that produced this output: ").append(step_name).append("\n\n");
        }
        return sb.toString();
    }

    public Map<Pair<String, String>, String> getMapping_of_alias() {
        return mapping_of_alias;
    }

    public String getActualName(String stepName, String sourceName){
        String actual_name = sourceName;
        Pair<String, String> step_and_actual_name= new Pair<>(stepName, sourceName);
        for (Map.Entry<Pair<String,String>, String> entry : mapping_of_alias.entrySet()) {
            if (entry.getKey().equals(step_and_actual_name)) {
                actual_name = entry.getValue();
            }
        }
        return actual_name;
    }
}