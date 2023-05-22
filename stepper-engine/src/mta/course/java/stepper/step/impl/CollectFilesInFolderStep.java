package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.db.database;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;
import sun.rmi.log.LogHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CollectFilesInFolderStep extends AbstractStepDefinition {

    private static final Logger LOGGER = Logger.getLogger(CollectFilesInFolderStep.class.getName());

    public CollectFilesInFolderStep(){
        super("Collect Files In Folder", true);

        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY,
                "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL,
                "Filter only these files", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA,
                "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA,
                "Total files found", DataDefinitionRegistry.NUMBER));
    }


    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        //Getting inputs
        String folder_new_name = context.getAliasName(getAliasName(), "FOLDER_NAME");
        String filter_new_name = context.getAliasName(getAliasName(), "FILTER");
        String folder_name = context.getDataValue(folder_new_name, String.class);
        String filter = context.getDataValue(filter_new_name, String.class);
        List<File> output_list = new ArrayList<>();
        int total_found = 0;
        //Changing the alias names of the inputs IMPORTANT!?
//        this.change_dd_alias(context.getMapping_of_alias()); //Making the changes (FlowLevelAliasing)

        if (filter != null)
            LOGGER.info("<Reading folder <" + folder_name + "> content with filter <" + filter + ">");
        else
            LOGGER.info("<Reading folder <" + folder_name + "> without filter");
        try {
            // create a File object for a directory
            File directory = new File(folder_name);
            if (!directory.exists())
                throw new FileNotFoundException("Folder doesn't exist");

            // check if the application can read the directory
            if (!directory.canRead())
                throw new SecurityException("Access to schema.folder denied");
            if (!directory.isDirectory())
                throw new Exception ("Not a directory");

            // try to list the files in the directory
            File[] files = directory.listFiles();

                for (File file : files) {
                    if (filter != null) {
                        if ((file != null) && file.toString().endsWith(filter))
                        {
                            if (file != null) { output_list.add(file); }
                            total_found++;
                        }
                    }else {
                        if (file != null) {
                            output_list.add(file);
                            total_found++;
                        }
                    }
                }
//            }
        } catch (FileNotFoundException e) {
            LOGGER.info("Folder doesn't exist");
            System.out.println("Error: " + e.getMessage());
            this.setSummary("FAILED because FileNotFoundException");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            String files_list_new_name = context.getAliasName(getAliasName(), "FILES_LIST");
            String total_found_new_name = context.getAliasName(getAliasName(), "TOTAL_FOUND");
            context.storeDataValue(files_list_new_name, output_list);
            context.storeDataValue(total_found_new_name, total_found);
            return result;
        } catch (SecurityException e) {
            LOGGER.info("Access to schema.folder denied");
            System.out.println("Error: " + e.getMessage());
            this.setSummary("FAILED because SecurityException");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            String files_list_new_name = context.getAliasName(getAliasName(), "FILES_LIST");
            String total_found_new_name = context.getAliasName(getAliasName(), "TOTAL_FOUND");
            context.storeDataValue(files_list_new_name, output_list);
            context.storeDataValue(total_found_new_name, total_found);
            return result;
        } catch (Exception e) {
            LOGGER.info("Error occurred");
            System.out.println("Error: " + e.getMessage());
            this.setSummary("FAILED because not a directory");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            String files_list_new_name = context.getAliasName(getAliasName(), "FILES_LIST");
            String total_found_new_name = context.getAliasName(getAliasName(), "TOTAL_FOUND");
            context.storeDataValue(files_list_new_name, output_list);
            context.storeDataValue(total_found_new_name, total_found);
            return result;
        }
        String files_list_new_name = context.getAliasName(getAliasName(), "FILES_LIST");
        String total_found_new_name = context.getAliasName(getAliasName(), "TOTAL_FOUND");
        context.storeDataValue(files_list_new_name, output_list);
        context.storeDataValue(total_found_new_name, total_found);
        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());


        if(total_found == 0) {
            LOGGER.info("WARNING: No files were found in the folder");
            this.setSummary("Step ended in warning because no files were found..");
            this.setResult(StepResult.WARNING);
        }
        else if (filter != null) {
            LOGGER.info("Found <" + total_found + "> files in folder");
            this.setSummary("SUCCESS, everything went good");
        }
        else {
            LOGGER.info("Found <" + total_found + "> files in folder matching the filter");
            this.setSummary("SUCCESS, everything went good");
        }
        this.setResult(StepResult.SUCCESS);
        return result;
    }
}
