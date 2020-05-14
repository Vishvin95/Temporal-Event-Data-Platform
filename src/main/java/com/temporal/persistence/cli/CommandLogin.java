package com.temporal.persistence.cli;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.temporal.persistence.util.FileConverter;

import java.io.File;

@Parameters(commandDescription = "For login in the database")
public class CommandLogin {
    @Parameter(names = {"--url"},description = "JDBC url for the database.",required = true)
    String url;

    @Parameter(names = {"--username","-u"},description = "Username for the database.",required = true)
    String username;

    @Parameter(names = {"--password","-p"},description = "Password for the database.",required = true)
    String password;

    @Parameter(names = {"--database","-d"},description = "Database for the operations.",required = true)
    String databse;

    @Parameter(names = {"--file,-f"},description = "File for login details",converter = FileConverter.class)
    File file = new File("login.properties");
}
