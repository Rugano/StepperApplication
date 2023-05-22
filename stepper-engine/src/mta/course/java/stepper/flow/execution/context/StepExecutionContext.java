package mta.course.java.stepper.flow.execution.context;

import javafx.util.Pair;

import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);

    boolean storeDataValue(String dataName, Object value);

    public void insertMap(Map<Pair<String, String>, String> map1, Map<Pair<String, String>, Pair<String, String>> map2);

    public String getAliasName(String step_name, String old_name);

    public Map<Pair<String, String>, String> getMapping_of_alias();
}


// some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line
