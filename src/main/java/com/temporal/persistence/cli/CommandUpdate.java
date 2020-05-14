package com.temporal.persistence.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.temporal.persistence.util.FileConverter;

import java.io.File;
import java.util.List;

@Parameters(commandDescription = "For Updating data in the database")
public class CommandUpdate {
    @Parameter(names = {"--single-file","-sf"},description = "Providing the file path for the single Insert",variableArity = true)
    List<String> updateFiles;

    @Parameter(names = {"--bulk-file","-bf"},description = "Providing the folder for the bulk update",converter = FileConverter.class)
    File updateFolder;
}