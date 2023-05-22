package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class FileDumperStep extends AbstractStepDefinition {

    private static final Logger LOGGER = Logger.getLogger(FileDumperStep.class.getName());

    public FileDumperStep() {
        super("File Dumper", true);

        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY,
                "Content", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY,
                "Target file path", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA,
                "File Creation Result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        String content_name = context.getAliasName(getAliasName(), "CONTENT");
        String content = context.getDataValue(content_name, String.class);
        String file_path_name = context.getAliasName(getAliasName(), "FILE_NAME");
        String file_path = context.getDataValue(file_path_name, String.class);


        LOGGER.info("About to create file named <" + file_path + ">");
        try {
            File file = new File(file_path);
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                writer.write(content);
                writer.close();
                if (content.isEmpty()) {
                    LOGGER.info("Content is empty, created an empty file");
                    String result_name = context.getAliasName(getAliasName(), "RESULT");
                    context.storeDataValue(result_name,"");
                    Instant end = Instant.now();
                    this.setDuration(Duration.between(start, end).toMillis());
                    this.setResult(StepResult.WARNING);
                    setSummary("WARNING: the content of the file is empty (created an empty file..)");
                    return this.result;
                }
            } else {
                String result_name = context.getAliasName(getAliasName(), "RESULT");
                context.storeDataValue(result_name,"FAILURE, IOException");
                Instant end = Instant.now();
                this.setDuration(Duration.between(start, end).toMillis());
                this.setResult(StepResult.FAILURE);
                LOGGER.info("Failed to create file!");
                setSummary("FAILURE: there was an IO Exception..");
                return this.result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String result_name = context.getAliasName(getAliasName(), "RESULT");
            context.storeDataValue(result_name,"FAILURE, IOException");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            setSummary("FAILURE: there was an IO Exception..");
            return this.result;
        }

        String result_name = context.getAliasName(getAliasName(), "RESULT");
        context.storeDataValue(result_name,"SUCCESS");
        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);
        setSummary("SUCCESS: everything is good");
        return this.result;
    }
}
