package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.db.database;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CSVExporterStep extends AbstractStepDefinition {

    private static final Logger LOGGER = Logger.getLogger(CSVExporterStep.class.getName());

    public CSVExporterStep(){
        super("CSV Exporter", true);


        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY,
                "Source data", DataDefinitionRegistry.RELATION));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA,
                "CSV export result", DataDefinitionRegistry.STRING));

    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete
        String source_name = context.getAliasName(getAliasName(), "SOURCE");
        RelationData source = context.getDataValue(source_name, RelationData.class);

        LOGGER.info("About to process <" +source.getNumberOfRows() +"> lines of data");
        if (source.getNumberOfRows() == 0) {
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            String output = new String();
            String result_name = context.getAliasName(getAliasName(), "RESULT");
            context.storeDataValue(result_name, output);
            setSummary("FAILURE because the RelationData input is empty(has 0 lines of content)");
            return StepResult.FAILURE; //Should never be here...
        }
        StringBuilder result = new StringBuilder();

        List<String> columns = source.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            result.append(column);
            if (i < columns.size() - 1) {
                result.append(",");
            }
        }
        result.append("\n");

        if (source.getNumberOfRows() == 1) {
            String output = result.toString();
            String result_name = context.getAliasName(getAliasName(), "RESULT");
            context.storeDataValue(result_name, output);
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.WARNING);
            setSummary("WARNING because the RelationData input is empty (only has 1 line)");
            return StepResult.WARNING;

        }

        List<RelationData.SingleRow> rows = source.getRows();
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                String columnName = columns.get(j);
                String value = source.getRowData(i, columnName);
                result.append(value);
                if (j < columns.size() - 1) {
                    result.append(",");
                }
            }
            result.append("\n");
        }
        String output = result.toString();
        String result_name = context.getAliasName(getAliasName(), "RESULT");
        context.storeDataValue(result_name, output);

        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);
        setSummary("SUCCESS: everything is good");
        return this.result;
    }
}
