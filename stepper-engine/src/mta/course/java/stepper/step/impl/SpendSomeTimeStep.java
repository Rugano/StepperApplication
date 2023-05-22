package mta.course.java.stepper.step.impl;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.*;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    private static final Logger LOGGER = Logger.getLogger(SpendSomeTimeStep.class.getName());


    public SpendSomeTimeStep() {
        super("Spend Some Time", true);

        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY,
                "Total sleeping time (sec)", DataDefinitionRegistry.NUMBER));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        Instant start = Instant.now();
        used++; // Maybe need to delete

        String time_new_name = context.getAliasName(getAliasName(), "TIME_TO_SPEND");//Knowing the change
        Integer time = context.getDataValue(time_new_name, Integer.class);//Getting actual value

        if (time == null) {
            System.out.println(time_new_name +" not found"); //shouldn't be an option..
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.WARNING);
            setSummary("WARNING because i couldn't find the time to sleep (doesn't exist)");
            return this.result;
        }
        if (time <= 0){
            LOGGER.info(time +" is not a positive number...");
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.WARNING);
            setSummary("WARNING because Can't sleep for negative time...");
            return this.result;
        }
        LOGGER.info("About to sleep for " + time + " seconds...");
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            // Restore interrupted state of the thread
            Thread.currentThread().interrupt();
            Instant end = Instant.now();
            this.setDuration(Duration.between(start, end).toMillis());
            this.setResult(StepResult.WARNING);
            setSummary("WARNING because there was an InterruptedException..");
            return this.result;
        }
        LOGGER.info("Done sleeping.");
        Instant end = Instant.now();
        this.setDuration(Duration.between(start, end).toMillis());
        this.setResult(StepResult.SUCCESS);
        setSummary("SUCCESS: everything is good");
        return this.result;
    }
}
