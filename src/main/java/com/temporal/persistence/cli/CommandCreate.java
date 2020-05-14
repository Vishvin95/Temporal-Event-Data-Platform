package com.temporal.persistence.cli;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.temporal.persistence.util.FileConverter;

import java.io.File;

@Parameters(commandDescription = "For creating schema in the database")
public class CommandCreate {
    @Parameter(names = {"--schema-file","-f"},description = "Providing the file path for the createScema",converter = FileConverter.class,required = true)
    File file = new File("schema.xml");
}
