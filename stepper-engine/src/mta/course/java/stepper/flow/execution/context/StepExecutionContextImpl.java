package mta.course.java.stepper.flow.execution.context;

import javafx.util.Pair;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;

import java.util.HashMap;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {

    private Map<Pair<String,String>, String> mapping_of_alias; //Map between a pair of ( step Alias name and the name of input/output )to alias so it will know in runtime to look for it
    //Look for Alias name if found then you have an alias to do
    //Meaning that instead of looking for input/output that's called Pair< , HERE>
    //You will change its name to the String that is on the right!
    private  Map<Pair<String, String>, Pair<String, String>> custom_mapping;//That's only for getting the info from here..
    private Map<String, DataDefinition> name_to_dd;
    private final Map<String, Object> dataValues; //<Alias Name, value>

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        mapping_of_alias = new HashMap<>();
        custom_mapping = new HashMap<>();
        name_to_dd = new HashMap<>();
        name_to_dd.put("TIME_TO_SPEND", DataDefinitionRegistry.NUMBER);
        name_to_dd.put("FOLDER_NAME", DataDefinitionRegistry.STRING);
        name_to_dd.put("FILTER", DataDefinitionRegistry.STRING);
        name_to_dd.put("FILES_LIST", DataDefinitionRegistry.LIST);
        name_to_dd.put("TOTAL_FOUND", DataDefinitionRegistry.NUMBER);
        name_to_dd.put("DELETED_LIST", DataDefinitionRegistry.LIST);
        name_to_dd.put("DELETION_STATS", DataDefinitionRegistry.MAPPING);
        name_to_dd.put("FILES_TO_RENAME", DataDefinitionRegistry.LIST);
        name_to_dd.put("PREFIX", DataDefinitionRegistry.STRING);
        name_to_dd.put("SUFFIX", DataDefinitionRegistry.STRING);
        name_to_dd.put("RENAME_RESULT", DataDefinitionRegistry.RELATION);
        name_to_dd.put("LINE", DataDefinitionRegistry.NUMBER);
        name_to_dd.put("DATA", DataDefinitionRegistry.RELATION);
        name_to_dd.put("SOURCE", DataDefinitionRegistry.RELATION);
        name_to_dd.put("RESULT", DataDefinitionRegistry.STRING);
        name_to_dd.put("CONTENT", DataDefinitionRegistry.STRING);
        name_to_dd.put("FILE_NAME", DataDefinitionRegistry.STRING);
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
        String actual_name = getActualName(dataName);
//        System.out.println(actual_name);
//        Object o = dataValues.get(dataName);
//        if (o == null)
//            return expectedDataType.cast(o);
        DataDefinition theExpectedDataDefinition = name_to_dd.get(actual_name); //Was actual name before
        if (theExpectedDataDefinition != null && expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = null;
            //checking from customMapping
            aValue = checkingCustomMappings(dataName, expectedDataType);
            if (aValue != null){
                return expectedDataType.cast(aValue);
            }
           else if (dataValues.get(dataName) != null) //checking from automatic mapping
                aValue = dataValues.get(dataName);
            // what happens if it does not exist ? I don't think it should happen...
            return expectedDataType.cast(aValue);
        } else {
            // error handling of some sort...
            //There's an error only if it's MANDATORY...
            System.out.println("WTF: I'm looking for an input that is not in the context map...");
            System.out.println(dataName + " and " + expectedDataType);
        }
        return null;
    }

    private <T> Object checkingCustomMappings(String dataName, Class<T> expectedDataType) {
        Object object = null;
        for (Map.Entry<Pair<String, String>, Pair<String, String>> map : custom_mapping.entrySet()){
            if (dataName.equals(map.getKey().getValue()))//(Target, Source)
                object = dataValues.get(map.getValue().getValue());
        }
        return object;
    }

    private String getActualName(String sourceName){
        String actual_name = sourceName;

      Pair<String, String> step_and_actual_name= null;
        for (Map.Entry<Pair<String,String>, String> entry : mapping_of_alias.entrySet()) {
            if (entry.getValue().equals(sourceName)) {
                step_and_actual_name = entry.getKey();
            }
        }
        if (step_and_actual_name != null )
            actual_name = step_and_actual_name.getValue();
        return actual_name;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {

//        if (value == null) {
//            dataValues.put(dataName, null);
//            return true;
//        }

        String actual_name = getActualName(dataName);
        DataDefinition theExpectedDataDefinition = name_to_dd.get(actual_name);//Was actual name before
//        System.out.println("About to try and put: "+actual_name +" which is a " +theExpectedDataDefinition);
        // assuming that from the data name we can get to its data definition
        // we have the DD type so we can make sure that its from the same type
        if (theExpectedDataDefinition != null && theExpectedDataDefinition.getType().isAssignableFrom(value.getClass())) {
            dataValues.put(dataName, value);
        } else {
            // error handling of some sort...
            System.out.println("There was a problem...");
            System.out.println(dataName + " and " +value);
        }
        return false;
    }
    public void insertMap(Map<Pair<String, String>, String> map1, Map<Pair<String, String>, Pair<String, String>> map2){
        mapping_of_alias = map1;
        custom_mapping = map2;
//        Pair<String,String> step_and_dd = new Pair<>(alias_name_of_step, old_name);
//        if (mapping_of_alias.put(step_and_dd, new_alias_name) != null)
//            return false; //Meaning that there already was an alias including that step and dd
//        return true;
    }
    public Map<Pair<String,String>, String> getMapping_of_alias(){return this.mapping_of_alias;}


//    public <T> T getIt(String step_name, String old_name, Class<T> expected_type){
//        Object value = null;
//        String new_name;
//        Pair<String, String> name_dd = new Pair<>(step_name, old_name);
//        new_name =getMapping_of_alias().get(name_dd);
//        if (new_name != null)
//            value = getDataValue(new_name, expected_type);
//        return expected_type.cast(value);
//    }


    public String getAliasName(String step_name, String old_name){
        String new_name = null;
        Pair<String, String> pair_to_check = new Pair<>(step_name, old_name);
        new_name = getMapping_of_alias().get(pair_to_check);
        if (new_name != null)
            return new_name;
        return old_name;
    }
}
