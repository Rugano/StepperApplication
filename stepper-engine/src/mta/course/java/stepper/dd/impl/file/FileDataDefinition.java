package mta.course.java.stepper.dd.impl.file;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.io.File;

public class FileDataDefinition extends AbstractDataDefinition{
    public FileDataDefinition() {super("File", false, File.class);}
}
