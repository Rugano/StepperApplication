package mta.course.java.stepper.db;

import javafx.util.Pair;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.api.StepResult;

import java.sql.Array;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class database {

    public List<Pair<UUID, Information>> list;
    public Statistics statistics;

    public database(){
        this.list = new ArrayList<>();
        this.statistics = new Statistics();
    }

    public void printStats(){
        int i = 1;
        boolean print = false;
        System.out.println("FLOWS LIST: ");
        for(Map.Entry<FlowDefinition, Long> pair : statistics.stats_flows.entrySet()){
            print = true;
            System.out.println(i++ +". " +pair.getKey().getName() +"\n");
            System.out.println("Number of times ran: " +pair.getKey().getUsed() +"\n");
            System.out.println("Average time is: " +pair.getValue()+"\n");
        }
        if (!print)
            System.out.println("is empty");
        System.out.println("STEPS LIST: ");
        i = 1;
        print = false;
        for(Map.Entry<StepDefinition, Long> step : statistics.stats_steps.entrySet()){
            print = true;
            System.out.println(i++ +". " +step.getKey().name() +"\n");
            System.out.println("Number of times ran: " +step.getKey().getUsed() +"\n");
            System.out.println("Average time is: " +step.getValue()+"\n");
        }
        if (!print)
            System.out.println("is empty");
    }

    public void insertInfo (UUID uuid, Information info, Statistics stat){
        Pair<UUID, Information> pair = new Pair<>(uuid, info);
        list.add(pair);
        statistics.addFlow(info.flow, info.time_stamp);
        for (Map.Entry<StepDefinition, Long> step : stat.stats_steps.entrySet())
            statistics.addStepToStat(step.getKey());
    }


    public void print4Menu(){
        int i = 1;
        for(Pair<UUID, Information> pair : list){
            System.out.println((i++)+ ". " +pair.getValue().flow.getName() +" UUID: " +pair.getKey().toString() +" time: " +pair.getValue().start_time);
        }
    }
    public void print4(UUID uuid){
        System.out.println("0. Go back");
        int i = 1;
        for(Pair<UUID, Information> pair : list){
            if (uuid.equals(pair.getKey())){
                System.out.println((i++)+". " +uuid + "\n");
                pair.getValue().show();
            }
        }
    }

    public static class Statistics{
        //list <flow, <used, average time>>
        public Map<FlowDefinition, Long> stats_flows; //each flow will be here once (check by name)
        public Map<StepDefinition, Long> stats_steps;//each step will be here once (check by alias name)

        public Statistics(){
            stats_flows = new HashMap<>();
            stats_steps = new HashMap<>();
        }

        public void addFlow(FlowDefinition flow, long time){
            long new_average_time = time;
            for(Map.Entry<FlowDefinition, Long> pair : stats_flows.entrySet()){
                if (pair.getKey().getName().equals(flow.getName()))
                     new_average_time = (   pair.getValue()*(flow.getUsed()-1)  +  time  ) / flow.getUsed();
            }
                stats_flows.put(flow, new_average_time);
        }
        public void addStepToStat(StepDefinition step){
            long new_average_time = step.getDuration();
            for(Map.Entry<StepDefinition, Long> pair : stats_steps.entrySet()){
                if (pair.getKey().name().equals(step.name()) && step.getUsed()!= 0)
                    new_average_time = (   pair.getValue()*(step.getUsed()-1)  +  step.getDuration()  ) / step.getUsed();
            }
            stats_steps.put(step, new_average_time);
        }
    }

    public static class Information {
        private List<StepDefinition> steps;
        private long time_stamp;
        private String start_time;
        private FlowDefinition flow;
        private List<Pair<Pair<String, DataDefinitionDeclaration>, Object>> free_inputs; //Final name, dd, content
        private List<Pair<Pair<String, DataDefinitionDeclaration>, Object>> outputs;//Final name, dd, content

        public Information(FlowDefinition flow) {
            this.flow = flow;
//            List<StepUsageDeclaration> newList = new ArrayList();
//            for (StepUsageDeclaration step : flow.getFlowSteps()) {
//                steps.add(step.getStepDefinition());
//            }
            this.steps = new ArrayList<>();
            this.free_inputs = new ArrayList<>();
            this.outputs = new ArrayList<>();
        }
        public void setTimeStamp(long time){this.time_stamp = time;}
        public long getTimeStamp(){return this.time_stamp;}
        public void setStartTime(String pattern){this.start_time = pattern;}
        public String getStartTime(){return this.start_time;}
        public void addFreeInput(String name, DataDefinitionDeclaration dd, Object value) {
            Pair<String, DataDefinitionDeclaration> p1 = new Pair<>(name, dd);
            Pair<Pair<String, DataDefinitionDeclaration>, Object> p2 = new Pair<>(p1, value);
            free_inputs.add(p2);
        }

        public void addOutput(String name, DataDefinitionDeclaration dd, Object value) {
            Pair<String, DataDefinitionDeclaration> p1 = new Pair<>(name, dd);
            Pair<Pair<String, DataDefinitionDeclaration>, Object> p2 = new Pair<>(p1, value);
            outputs.add(p2);
        }

        public void addStep(StepDefinition step) {
            this.steps.add(step);
        }

        public String displayFreeInputs(){
            StringBuilder sb = new StringBuilder();
            for (Pair<Pair<String, DataDefinitionDeclaration>, Object> triple : free_inputs){
                sb.append("a. ").append(triple.getKey().getKey()).append("\n");
                sb.append("b. ").append(triple.getKey().getValue().dataDefinition().getType()).append("\n");
                sb.append("c. ").append(triple.getValue()).append("\n");
                sb.append("d. ").append(triple.getKey().getValue().necessity()).append("\n");
            }
            return sb.toString();
        }
        public String displayOutputs() {
            StringBuilder sb = new StringBuilder();
            for (Pair<Pair<String, DataDefinitionDeclaration>, Object> triple : outputs){
                sb.append("a. ").append(triple.getKey().getKey()).append("\n");
                sb.append("b. ").append(triple.getKey().getValue().dataDefinition().getType()).append("\n");
                sb.append("c. ").append(triple.getValue()).append("\n");
            }
            return sb.toString();
        }
        public String displaySteps() {
            StringBuilder sb = new StringBuilder();
            for (StepDefinition step : steps) {
                sb.append("a. " +step.name()).append("\n").
                append("b.").append(step.getDuration()).append("\n").
                append("c. " +step.getResult()).append("\n").
                append("d. " +step.getSummary()).append("\n");
                //Need to add logs here!!!
            }
            return sb.toString();
        }

        public void show() {
            System.out.println("2. " + flow.getName() + "\n");
            System.out.println("3. " +"Need to put flowResult here" +"\n");
            System.out.println("4. " + getTimeStamp() + "\n");
            System.out.println("5. \n" +displayFreeInputs() + "\n");
            System.out.println("6. \n" +displayOutputs()+ "\n");
            System.out.println("7. \n" +displaySteps() + "\n");
        }
    }
}
