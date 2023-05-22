package mta.course.java.stepper.dd.impl;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.doublr.DoubleDataDefinition;
import mta.course.java.stepper.dd.impl.list.ListDataDefinition;
import mta.course.java.stepper.dd.impl.mapping.MapDataDefinition;
import mta.course.java.stepper.dd.impl.number.NumberDataDefinition;
import mta.course.java.stepper.dd.impl.relation.RelationDataDefinition;
import mta.course.java.stepper.dd.impl.mapping.MapDataDefinition;
import mta.course.java.stepper.dd.impl.file.FileDataDefinition;
import mta.course.java.stepper.dd.impl.string.StringDataDefinition;


public enum DataDefinitionRegistry implements DataDefinition{
    STRING(new StringDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    RELATION(new RelationDataDefinition()),
    NUMBER(new NumberDataDefinition()),
    MAPPING(new MapDataDefinition()),
    FILE(new FileDataDefinition()),
    LIST(new ListDataDefinition()),
    ;

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }
}
