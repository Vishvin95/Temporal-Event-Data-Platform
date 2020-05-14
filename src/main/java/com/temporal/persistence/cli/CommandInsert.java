package com.temporal.persistence.cli;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.temporal.persistence.util.FileConverter;
import com.temporal.persistence.util.FileListConverter;

import java.io.File;
import java.util.List;

@Parameters(commandDescription = "For inserting data in the database")
public class CommandInsert {
    @Parameter(names = {"--single-file","-sf"},description = "Providing the file path for the single Insert",variableArity = true)
    List<String> insertFile;

    @Parameter(names = {"--bulk-file","-bf"},description = "Providing the folder for the bulk insert",converter = FileConverter.class)
    File insertFolder;
}
