package mta.course.java.stepper.step;

import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.impl.*;

public enum StepDefinitionRegistry {
    SPEND_SOME_TIME("Spend Some Time", new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER("Collect Files In Folder", new CollectFilesInFolderStep()),
    FILES_DELETER("Files Deleter", new FilesDeleterStep()),
    FILES_RENAMER("Files Renamer", new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR("Files Content Extractor", new FilesContentExtractorStep()),
    CSV_EXPORTER("CSV Exporter", new CSVExporterStep()),
    PORPERTIES_EXPORTER("Properties Exporter", new PropertiesExporterStep()),
    FILE_DUMPER("File Dumper", new FileDumperStep())
    ;


    private final String identifier;
    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(String identifier, StepDefinition stepDefinition) {
        this.identifier = identifier;
        this.stepDefinition = stepDefinition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public StepDefinition getStepDefinition() {
        return stepDefinition.clone();
    }

    public static StepDefinition fromIdentifier(String identifier, String aliasName) {
        for (StepDefinitionRegistry stepDefinitionRegistry : StepDefinitionRegistry.values()) {
            if (stepDefinitionRegistry.getIdentifier().equals(identifier)) {
                StepDefinition g = stepDefinitionRegistry.getStepDefinition();
                g.setAliasName(aliasName);
//                System.out.println("Changed :" +identifier +"+to: " +aliasName);
                return g;
            }
        }
        return new NoSuchStep();
//        throw new IllegalArgumentException("No StepDefinitionRegistry with identifier " + identifier);
    }
}