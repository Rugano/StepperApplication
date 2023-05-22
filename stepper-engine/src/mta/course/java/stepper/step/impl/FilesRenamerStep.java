package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FilesRenamerStep extends AbstractStepDefinition {

    private static final Logger LOGGER = Logger.getLogger(FilesRenamerStep.class.getName());

    public FilesRenamerStep(){
        super ("Files Renamer", false);

        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY,
                "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL,
                "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL,
                "Append this suffix", DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA,
                "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        String files_list_name = context.getAliasName(getAliasName(), "FILES_TO_RENAME");
        List<File> files_list = context.getDataValue(files_list_name, List.class);
        String prefix_name = context.getAliasName(getAliasName(), "PREFIX");
        String prefix = context.getDataValue(prefix_name, String.class);
        String suffix_name = context.getAliasName(getAliasName(), "SUFFIX");
        String suffix = context.getDataValue(suffix_name, String.class);

        List<String> columns = new ArrayList<>();
        columns.add(0, "Serial Number");
        columns.add(1, "Original file name");
        columns.add(2, "new file name");
        RelationData rename_result = new RelationData(columns);

        Boolean prefix_change = false;
        Boolean suffix_change = false;
        if (prefix != null && suffix != null) {
            LOGGER.info("About to start rename <" + files_list + "> files. Adding prefix: <" + prefix + "> and adding suffix: <" +
                    suffix +">");
            prefix_change = true;
            suffix_change = true;
        }
        else if (prefix != null){
            LOGGER.info("About to start rename <" + files_list + "> files. Adding prefix: <" + prefix + ">");
            prefix_change = true;
        }
        else if (suffix != null) {
            LOGGER.info("About to start rename <" + files_list + "> files. Adding suffix: <" + suffix + ">");
            suffix_change = true;
        }
        else {
            LOGGER.info("About to start rename <" + files_list + "> files.");
            LOGGER.info("Finished all the renaming");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.SUCCESS);
            String rename_result_new_name = context.getAliasName(getAliasName(), "RENAME_RESULT");
            context.storeDataValue(rename_result_new_name, rename_result);
            setSummary("SUCCESS: The list was empty");
            return this.result;
        }
        boolean warning = false;
        boolean success = false;
        String new_file_name;
        int j = 1;
        File newFile;
        for (File file : files_list) {
            if (!file.exists()) {
                LOGGER.info("Problem renaming file <" + file.getName() + ">");
                rename_result.addRowToTable(Integer.toString(j), "N/A", "N/A");
                j++;
                warning = true;
                continue;
            } else if (!file.canRead()) {
                LOGGER.info("Problem renaming file <" + file.getName() + ">");
                rename_result.addRowToTable(Integer.toString(j), "N/A", "N/A");
                j++;
                warning = true;
                continue;
            } else {
                success = true;
                String file_name = file.getName();
                int dotIndex = file_name.lastIndexOf('.');
                String name_without_extension = file_name.substring(0, dotIndex);
                String extension = file_name.substring(dotIndex);
                if (prefix_change && suffix_change) {
                    new_file_name = prefix + name_without_extension + suffix + extension;
                    newFile = new File(file.getParent(), new_file_name);
                    System.out.println("for old file named: " + file_name + ": prefix and suffix added successfully!");
                    System.out.println("new file name is: " + new_file_name);

                } else if (prefix_change) {
                    new_file_name = prefix + name_without_extension +extension;
                    newFile = new File(file.getParent(), new_file_name);
                    System.out.println("for old file named: " + file_name + ": prefix added successfully!");
                    System.out.println("new file name is: " + new_file_name);
                } else /* if(suffix change) */ {
                    new_file_name = name_without_extension +suffix +extension;
                    newFile = new File(file.getParent(), new_file_name);
                    System.out.println("for old file named: " + file_name + ": suffix added successfully!");
                    System.out.println("new file name is: " + new_file_name);
                }
                // add to rename_result
                rename_result.addRowToTable(Integer.toString(j), file_name, new_file_name);
            }
        }
        String rename_result_new_name = context.getAliasName(getAliasName(), "RENAME_RESULT");
        context.storeDataValue(rename_result_new_name, rename_result);
        LOGGER.info("Finished all the renaming");
//        System.out.println(rename_result_new_name +": " +rename_result.toString());

        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);
        setSummary("SUCCESS: The list was empty");

        if (success) {
            return result;
        }
        else if (warning) {
            this.setSummary("WARNING because one of the files couldn't be accessed or changed..");
            this.setResult(StepResult.WARNING);
            return result;
        }
        LOGGER.info("The list is empty..");
            return result;
    }
}
