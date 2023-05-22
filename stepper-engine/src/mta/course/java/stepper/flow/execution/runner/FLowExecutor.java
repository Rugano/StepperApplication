package mta.course.java.stepper.flow.execution.runner;

import javafx.util.Pair;
import mta.course.java.stepper.db.database;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FLowExecutor {

    public void executeFlow(FlowExecution flowExecution, database.Information my_info, database.Statistics stats) {
        Instant start = Instant.now();

        //Make information Object and throughout this populate it with the needed info...

        LocalTime now = LocalTime.now(); // get the current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // create a formatter for the desired format
        String formattedTime = now.format(formatter); // format the current time using the formatter

        my_info.setStartTime(formattedTime);

        StepExecutionContext context = new StepExecutionContextImpl(); // actual object goes here...
        int i;
        boolean all_mandatory_inputs_applied;
        List<DataDefinitionDeclaration> free_inputs = flowExecution.getFlowDefinition().getFlowFreeInputs();
        List<Pair<String, DataDefinitionDeclaration>> step_name_to_free_input = flowExecution.getFlowDefinition().getFlowFreeInputsAndNames();
        Scanner scanner = new Scanner(System.in);
        List<Boolean> mandatory_places = new ArrayList<>();

        context.insertMap(flowExecution.getAliasMapping(), flowExecution.getCustom_mapping());

        for (i = 0; i < free_inputs.size(); i++) {
            if (free_inputs.get(i).necessity() == DataNecessity.MANDATORY)
                mandatory_places.add(i, false);
            else
                mandatory_places.add(i, true);
        }
        while (true) {
            System.out.println(mandatory_places);
            all_mandatory_inputs_applied = true;
            for (i = 0; i < free_inputs.size(); i++) {
                if (!mandatory_places.get(i))
                    all_mandatory_inputs_applied = false;
            }
            System.out.println("Please select the values you want to enter (enter 0 to exit)");
            for (i = 0; i < free_inputs.size(); i++) {
                DataDefinitionDeclaration dd = free_inputs.get(i);
                System.out.println((i + 1) + ". Enter: " + dd.userString() + " " + dd.necessity());
            }
            System.out.println((i + 1) + ". " + "Go Back to Main Manu");
            if (all_mandatory_inputs_applied)
                System.out.println((i + 2) + ". " + "Start Execution of flow");

            int answer_index;
            if (scanner.hasNext()) {
                if (!scanner.hasNextInt()) {
                    System.out.println("Please enter a number");
                    scanner.nextLine();
                    continue;
                }
            }
            answer_index = scanner.nextInt();
            scanner.nextLine();
            if (answer_index <= free_inputs.size() && answer_index >= 1) {
                //Maybe need to split (if expect a num and user enters string..?)
                mandatory_places.set(answer_index - 1, true);
                Class<?> input_type = free_inputs.get(answer_index - 1).dataDefinition().getType();
                System.out.println("please enter the " + free_inputs.get(answer_index - 1).userString() + ": ");
                Object answer_value;
                /*
                if (input_type.getName().equals(Integer.class.getName()))
                        answer_value = scanner.nextInt();
                    else if (input_type.getName().equals(Double.class.getName()))
                        answer_value = scanner.nextDouble();
                    else if (input_type.getName().equals(String.class.getName()))
                        answer_value = scanner.next();
                    else {
                        answer_value = null;
                        System.out.println("input type is: " + input_type);
                        System.out.println("WTF is going on... go to debugger");
                        continue;
                    }
                    */

                if (input_type.getName().equals(String.class.getName())) {
                    while (!scanner.hasNextLine()) {

                    }
                    answer_value = scanner.nextLine();
                } else if (input_type.getName().equals(Integer.class.getName())) {
                    while (!scanner.hasNextInt()) {
                        String input = scanner.next();
                        if (!input.matches("-?\\d+")) {
                            System.out.println("Invalid input. Please enter an integer.");
                        }
                    }
                    answer_value = scanner.nextInt();
                } else if (input_type.getName().equals(Double.class.getName())) {
                    while (!scanner.hasNextDouble()) {
                        String input = scanner.next();
                        if (!input.matches("-?\\d+(\\.\\d+)?")) {
                            System.out.println("Invalid input. Please enter a double.");
                        }
                    }
                    answer_value = scanner.nextDouble();
                } else {
                    answer_value = null;
                    System.out.println("input type is: " + input_type);
                    System.out.println("WTF is going on... go to debugger");
                    continue;
                }
                context.storeDataValue(step_name_to_free_input.get(answer_index - 1).getKey(), answer_value);
                my_info.addFreeInput(step_name_to_free_input.get(answer_index - 1).getKey(),free_inputs.get(answer_index -1),  answer_value);
//
            } else if (answer_index == free_inputs.size() + 1) {
                System.out.println("Going back to main menu");
                return; //Might change later
            } else if (all_mandatory_inputs_applied && (answer_index == free_inputs.size() + 2)) { //start execution of flow
                System.out.println("STARTING EXE!");
                break;
            } else {
                System.out.println("Please enter a valid index");
                continue;
            }
        }

        // (typically stored on top of the flow execution object)
        boolean warning = false;
        boolean stopped = false;
        // start actual execution
        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");
        for (i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context); //PROBLEM
            my_info.addStep(stepUsageDeclaration.getStepDefinition());
            stats.addStepToStat(stepUsageDeclaration.getStepDefinition());
            //populating number 7 in command 4
            System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
            if (!stepUsageDeclaration.skipIfFail() && stepResult.equals(StepResult.FAILURE)) {
                //STOP EXECUTION!
                stopped = true;
                System.out.println("Step: " +stepUsageDeclaration.getFinalStepName() +" failed...");
                break;
            } else if (stepResult.equals(StepResult.WARNING)) {
                warning = true;
            }
        }
        if (stopped) {
            flowExecution.setResult(FlowExecutionResult.FAILURE);
            return;
        } else if (warning) {
            flowExecution.setResult(FlowExecutionResult.WARNING);
        } else
            flowExecution.setResult(FlowExecutionResult.SUCCESS);

        System.out.println("End execution of flow. \n1. Name of flow: " + flowExecution.getFlowDefinition().getName() + "\n2. ID: " + flowExecution.getUniqueId() + "\n3. Status: " + flowExecution.getFlowExecutionResult());

        //Printing all of the outputs
        System.out.println("4. Flow Outputs are: \n");
        for (StepUsageDeclaration step : flowExecution.getFlowDefinition().getFlowSteps()) {
//            System.out.println("STEP: " + step.getFinalStepName() + ", outputs are: ");
            for (DataDefinitionDeclaration dd : step.getStepDefinition().outputs()) {
                String dd_alias_name = context.getAliasName(step.getFinalStepName(), dd.getName());
//                System.out.println("Name: " + dd.getName() + ", Alias: " + dd_alias_name + ", Content: ");
//                System.out.println(context.getDataValue(dd_alias_name, dd.dataDefinition().getType()));
                for(String name_of_flow_output : flowExecution.getFlowDefinition().getFlowFormalOutputs()){
                    if (name_of_flow_output.equals(dd_alias_name)){
//                        Object outputz = context.getDataValue(dd_alias_name, dd.dataDefinition().getType());
//                        System.out.println(dd.userString() +": " +outputz);
//                        System.out.println(dd.userString() +": " +outputz.toString());
//                        System.out.println(outputz.getClass().toString());
                        System.out.println(dd.userString() +": " +context.getDataValue(dd_alias_name, dd.dataDefinition().getType()).toString());
                    }
                }
                Object value = context.getDataValue(dd_alias_name, dd.dataDefinition().getType());
                my_info.addOutput(dd_alias_name, dd, value);
            }
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        my_info.setTimeStamp(duration.toMillis());
        flowExecution.totalTime = duration;
    }
}