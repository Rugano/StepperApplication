package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

public class PropertiesExporterStep extends AbstractStepDefinition {

    private static final Logger LOGGER = Logger.getLogger(PropertiesExporterStep.class.getName());

    public PropertiesExporterStep() {
        super("Properties Exporter", true);

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

        List<String> columns = source.getColumns();
        List<RelationData.SingleRow> rows = source.getRows();
        StringBuilder result = new StringBuilder();

        if (source.getNumberOfRows() == 0) {
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.FAILURE);
            setSummary("FAILURE There is not data at all in list");
            return this.result;//Should never be here...
        }
        LOGGER.info("About to process <" +source.getNumberOfRows() +"> lines of data");

        if (source.getNumberOfRows() == 1) {
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.WARNING);
            setSummary("WARNING because there are no lines of data in list");
            return this.result;
        }
        for (int i = 0; i < rows.size(); i++) {
            RelationData.SingleRow row = rows.get(i);
            for (int j = 0; j < columns.size(); j++) {
                String columnName = columns.get(j);
                String value = row.getData().get(columnName);
                String key = "Row-" + (i+1) + "." + columnName;
                result.append(key + "=" + value + "\n");
            }
        }
//        System.out.println("Extracted total of <" +source.getNumberOfRows()*source.getColumns().size() +">");
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