package mta.course.java.stepper.stepper;

import mta.course.java.stepper.db.database;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.generated.STFlow;
import mta.course.java.stepper.generated.STStepper;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Stepper{
    public List<FlowDefinition> flow_list;
    public InputStream xml_file;
    public database db;
    public String summary;

    public Stepper () {
        flow_list = new ArrayList<>();
        db = new database();
    }

    public boolean start(String filePath) throws JAXBException {
        if (!filePath.endsWith(".xml"))
            return false;
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            this.xml_file = inputStream;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            return false;
        }
        return stepperInitiate();
    }

    //Makes all the instances of the Flows
    public Boolean stepperInitiate() throws JAXBException {
        try {
            STStepper stepper = deserializeFrom(this.xml_file);


            if (stepper == null)
                return false;
            int i = 1;
            boolean validation = true;
            boolean are_there_flows = false;
            this.summary = "There are no flows...";
            for (STFlow st_flow : stepper.getSTFlows().getSTFlow()) {
                are_there_flows = true;
                FlowDefinition flow = new FlowDefinitionImpl(st_flow);
                validation = validation && flow.validateFlowStructure();//Something might be off so need to check
                if(!validation) {
                    this.summary = flow.getSummaryOfXML();
//                    System.out.println(flow.getSummaryOfXML());
                    return false;
                }
                flow_list.add(flow);
            }
            for (FlowDefinition flow1 : flow_list){
                for (FlowDefinition flow2 : flow_list){
                    if (flow1 != flow2 && flow1.getName().equals(flow2.getName())){
                        this.summary = "There are two flows with the same name..";
//                        System.out.println(summary);
                        return false;
                    }
                }
            }
            this.summary = "Stepper is ready to go";
            return validation;
        }catch (JAXBException e){
            e.printStackTrace();
            this.summary = "There was a JAXB exception...";
            return false;
        }
    }

    private STStepper deserializeFrom(InputStream in) throws JAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(STStepper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (STStepper) jaxbUnmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            System.out.println("An exception occurred: " + e.getMessage());
            System.out.println("HERE");
            return null;
        }
    }

    public String showFlows(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flow_list.size(); i++){
            sb.append(i+1).append(". ").append(flow_list.get(i).getName()).append("\n");
        }
        return sb.toString();
    }
    public void showStats(){
        db.printStats();
    }


    //When pressing 3 Starting the flow execution, this function will be called from the UI
    //This function will bring you to the "menu" where you put the free inputs!
    public void executeAFlow(FlowDefinition flow){
        FLowExecutor fLowExecutor = new FLowExecutor();
        UUID uuid = randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flow);


        database.Information info = new database.Information(flow);
        database.Statistics stat = new database.Statistics();
        fLowExecutor.executeFlow(flowExecution, info, stat);
        if (info.getTimeStamp() != 0){ //Only if actually executed
            db.insertInfo(uuid, info, stat);
            flow.UsedInc();
        }
    }

    public void showPast(){
        db.print4Menu();
    }

    public String getFlowDetails(String name_of_flow){
        //somehow return information about flow for right side
        //need to do the same for left side!
        return "";
    }
}