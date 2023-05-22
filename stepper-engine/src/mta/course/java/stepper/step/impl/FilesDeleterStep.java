package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FilesDeleterStep extends AbstractStepDefinition {


    private static final Logger LOGGER = Logger.getLogger(FilesDeleterStep.class.getName());

    public FilesDeleterStep(){
        super("Files Deleter", false);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY,
                "Files to delete", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA,
                "Files failed to be deleted", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA,
                "Deletion summary results", DataDefinitionRegistry.MAPPING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        String files_name = context.getAliasName(getAliasName(), "FILES_LIST");
        List<File> files = (List<File>) context.getDataValue(files_name, List.class);
        List<String> not_deleted = new ArrayList<>();
        Map<String, Integer> deletion_stats = new HashMap<>();
        boolean warning = false;
        LOGGER.info("About to start delete <" + files.size() + "> files");


        if (files.isEmpty()) {
            deletion_stats.put("cdr", 0);
            deletion_stats.put("car", 0);
            String DELETED_FILES_name = context.getAliasName(getAliasName(), "DELETED_LIST");
            String DELETION_STATS_name = context.getAliasName(getAliasName(), "DELETION_STATS");
            context.storeDataValue(DELETED_FILES_name, not_deleted);
            context.storeDataValue(DELETION_STATS_name, deletion_stats);
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setSummary("The files list is empty...");
            this.setResult(StepResult.FAILURE);
        }


        int count = 0;
        for (File file : files) {
            //check if the file exist
            if (!file.exists()) {
                LOGGER.info("File doesn't exist");
                System.out.println("Error: File doesn't exist: " + file.toString());
                count = deletion_stats.getOrDefault("cdr", 0);
                deletion_stats.put("cdr", count + 1);
                not_deleted.add(file.toString());
                warning = true;
                continue; // skip this file and move on to the next one
            }
            // check if the application can read the directory
            if (!file.canRead()) {
                LOGGER.info("Access to file denied");
                System.out.println("Error: Access to file denied: " + file.toString());
                count = deletion_stats.getOrDefault("cdr", 0);
                deletion_stats.put("cdr", count + 1);
                not_deleted.add(file.toString());
                warning = true;
                continue; // skip this file and move on to the next one
            }
            if (file.delete()) {
                count = deletion_stats.getOrDefault("car", 0);
                //count = deletion_stats.containsKey("car") ? deletion_stats.get("car") : 0; //should be the same..
                deletion_stats.put("car", count + 1);
            } else {
                LOGGER.info("Failed to delete file <" + file.toString() + ">");
                count = deletion_stats.getOrDefault("cdr", 0);
                deletion_stats.put("cdr", count + 1);
                not_deleted.add(file.toString());
                warning = true;
            }
        }

        //Adding outputs to context!
        String DELETED_FILES_name = context.getAliasName(getAliasName(), "DELETED_LIST");
        String DELETION_STATS_name = context.getAliasName(getAliasName(), "DELETION_STATS");

        context.storeDataValue(DELETED_FILES_name, not_deleted);
        context.storeDataValue(DELETION_STATS_name, deletion_stats);
        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);

        if (warning && count == 0) {
            LOGGER.info("FAILURE: All of the files were not deleted!");
            this.setSummary("FAILURE because all of the files were not deleted properly");
            this.setResult(StepResult.FAILURE);
            return result;
        }
        else if (warning) {
            LOGGER.info("WARNING: Some of the files were not deleted!");
            this.setSummary("Warning because some of the files were not deleted properly");
            this.setResult(StepResult.WARNING);
            return result;
        }
        LOGGER.info("All of the files were deleted successfully");
        this.setSummary("SUCCESS: all of the files were deleted properly");
        return result;

    }
}
