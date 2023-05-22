package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FilesContentExtractorStep extends AbstractStepDefinition {


    private static final Logger LOGGER = Logger.getLogger(FilesContentExtractorStep.class.getName());

    public FilesContentExtractorStep(){
        super("Files Content Extractor", true); //Might need to change stepName

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY,
                "Files to extract", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY,
                "Line number to extract", DataDefinitionRegistry.NUMBER));
        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA,
                "Data extraction", DataDefinitionRegistry.RELATION));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        String files_list_name = context.getAliasName(getAliasName(), "FILES_LIST");
        List<File> files_list = (List<File>) context.getDataValue(files_list_name, List.class);
        String line_name = context.getAliasName(getAliasName(), "LINE");
        Integer line = context.getDataValue(line_name, Integer.class);

        boolean applied = false;
        List<String> columns = new ArrayList<>();
        columns.add(0, "Serial Number");
        columns.add(1, "File name");
        columns.add(2, "Line");
        RelationData data = new RelationData(columns);
        int serialNumber = 1;

        if (files_list == null){
            String data_name = context.getAliasName(getAliasName(), "DATA");
            context.storeDataValue(data_name, data);
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.SUCCESS);
            setSummary("SUCCESS: files list is empty");
            return this.result;
        }

        for (File file : files_list) {
            try {
                applied = false;
                if (!file.exists()){
                    System.out.println("This file doesn't exist: " +file.getAbsolutePath());
                    data.addRowToTable(String.valueOf(serialNumber), "File not found", "N/A");
                    continue;
                }
                String fileName = file.getName();
                LOGGER.info("About to start work on file <" +fileName +">");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line_read = reader.readLine();
                int lineNumber = 1;

                while (line_read != null) {
                    if (lineNumber == line) {
                        data.addRowToTable(String.valueOf(serialNumber), fileName, line_read);
                        applied = true;
                        serialNumber++;
                        break;
                    }
                    line_read = reader.readLine();
                    lineNumber++;
                }
                if (!applied) {
                    data.addRowToTable(String.valueOf(serialNumber), fileName, "No such line");
                    LOGGER.info("Problem extracting line nummber <" +line+"> from file <" +fileName +">");
                }
                reader.close();
            } catch(IOException e){
                String data_name = context.getAliasName(getAliasName(), "DATA");
                context.storeDataValue(data_name, data);
                e.printStackTrace();
                Instant end = Instant.now();
                this.setDuration(Duration.between(start, end).toMillis());
                this.setResult(StepResult.FAILURE);
                setSummary("FAILURE: There was an IO Exception");
                return this.result;
            } catch (Exception e){
                String data_name = context.getAliasName(getAliasName(), "DATA");
                context.storeDataValue(data_name, data);
                Instant end = Instant.now();
                this.setDuration(Duration.between(start, end).toMillis());
                this.setResult(StepResult.FAILURE);
                setSummary("FAILURE: There was an Exception");
                return this.result;
            }
        }
        String data_name = context.getAliasName(getAliasName(), "DATA");
        context.storeDataValue(data_name, data);
        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);
        setSummary("SUCCESS: Finished properly");
        return this.result;
    }
}
