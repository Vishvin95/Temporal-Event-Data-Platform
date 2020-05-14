package com.temporal.persistence.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.temporal.model.InputData;
import com.temporal.model.InvalidScenarioException;
import com.temporal.model.Scenario;
import com.temporal.persistence.builder.GenericSqlBuilder;
import com.temporal.persistence.connection.Excecutor;
import com.temporal.persistence.util.DBTablePrinter;
import com.temporal.query.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

public class App {
    private static Args args = new Args();
    private static CommandLogin commandLogin = new CommandLogin();
    private static CommandInsert commandInsert = new CommandInsert();
    private static CommandCreate commandCreate = new CommandCreate();
    private static CommandUpdate commandUpdate = new CommandUpdate();
    private static CommandConsole commandConsole = new CommandConsole();
    private static String INSERT = "insert";
    private static String LOGIN = "login";
    private static String CREATE = "create";
    private static String UPDATE = "update";
    private static String CONSOLE = "console";
    private static String DEFAULT_FILE = "scenario.xml";
    private static JCommander jCommander;
    private static Logger logger = LogManager.getLogger(App.class);
    public static InputData getData(String filepath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(InputData.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (InputData) jaxbUnmarshaller.unmarshal(new File(filepath));
    }
    public static void main(String ... argv) {
        App main = new App();
        jCommander = JCommander.newBuilder()
                .addObject(args)
                .addCommand(INSERT,commandInsert)
                .addCommand(CREATE,commandCreate)
                .addCommand(LOGIN,commandLogin)
                .addCommand(UPDATE,commandUpdate)
                .addCommand(CONSOLE,commandConsole)
                .build();
        try {
            jCommander.parse(argv);
            if(args.help){
                jCommander.usage();
                System.exit(1);
            }
        }catch (ParameterException e){
            System.err.println("[ERROR] : "+e.getMessage());
            System.exit(1);
        }
        main.run();
    }

    public void run() {
        if(jCommander.getParsedCommand()==null){
            System.out.println("[ERROR] : Fatal no command provided.");
        }else{
            if(jCommander.getParsedCommand().equals(INSERT)){
                if(commandInsert.insertFile!=null) {
                    commandInsert.insertFile.forEach(s -> {
                        File file = new File(s);
                        if(!file.exists()){
                            System.err.println("[ERROR] : ["+file+"] Fatal file not present.");
                            System.exit(1);
                        }
                    });
                    ArrayList<InputData> files = commandInsert.insertFile.stream().map(File::new).map(file -> {
                        try {
                            InputData inputData = getData(file.getAbsolutePath());
                            return inputData;
                        } catch ( JAXBException e) {
                            System.err.println("[ERROR] : ["+file+"] Fatal file not parsable.");
                            System.exit(1);
                        }
                        return null;
                    }).collect(Collectors.toCollection(ArrayList::new));
                    for (InputData file : files) {
                        try {
                            InsertQuery.insert(file.getTable());
                        } catch (SQLException throwables) {
                            System.err.println("[ERROR] : Fatal unable to insert. + "+throwables.getMessage());

                        }
                    }
                }
                else if(commandInsert.insertFolder!=null){
                    if(!commandInsert.insertFolder.exists()){
                        System.err.println("[ERROR] : ["+commandInsert.insertFolder+"] Fatal file not present.");
                        System.exit(1);
                    }
                    if(!commandInsert.insertFolder.isDirectory()){
                        System.err.println("[ERROR] : ["+commandInsert.insertFolder+"] Fatal path is not a directory.");
                        System.exit(1);
                    }

                    File[] files = commandInsert.insertFolder.listFiles();
                    ArrayList<File> xmlFiles = Arrays.stream(files)
                            .filter(Objects::nonNull).filter(file -> FilenameUtils.getExtension(file.getName()).equals("xml"))
                            .collect(Collectors.toCollection(ArrayList::new));
                    for(File xmlFile : xmlFiles){
                        System.out.println(xmlFile.getAbsolutePath());
                        try {
                            InputData inputData = getData(xmlFile.getAbsolutePath());
                            //System.out.println(inputData);
                            InsertQuery.insert(inputData.getTable());
                        } catch (SQLException  | JAXBException throwables) {
                            System.err.println("[ERROR] : Unable to insert +"+throwables.getMessage());
                            System.exit(1);
                        }
                    }
                }
            }
            else if(jCommander.getParsedCommand().equals(CREATE)){
                //check login info
                if(!commandCreate.file.exists()){
                    System.err.println("[ERROR] : Fatal file not present");
                    System.exit(1);
                }else{
                    try {
                        CreateQuery createQuery = new CreateQuery();
                        String s = createQuery.CreateScenario(Scenario.loadFromXML(commandCreate.file));
                        Excecutor excecutor = new Excecutor();
                        Arrays.stream(s.split(";"))
                                .map(GenericSqlBuilder::new)
                                .forEach(excecutor::addSqlQuery);
                        excecutor.execute();
                    } catch (JAXBException | InvalidScenarioException | SAXException | SQLException e) {
                       System.err.println("[ERROR] : "+e.getMessage());
                    }
                }
            }
            else if(jCommander.getParsedCommand().equals(UPDATE)){
                if(commandUpdate.updateFiles!=null) {
                    commandUpdate.updateFiles.forEach(s -> {
                        File file = new File(s);
                        if(!file.exists()){
                            System.err.println("[ERROR] : ["+file+"] Fatal file not present.");
                            System.exit(1);
                        }
                    });
                    ArrayList<InputData> files = commandUpdate.updateFiles.stream().map(File::new).map(file -> {
                        try {
                            InputData inputData = getData(file.getAbsolutePath());
                            return inputData;
                        } catch ( JAXBException e) {
                            System.err.println("[ERROR] : ["+file+"] Fatal file not parsable.");
                            System.exit(1);
                        }
                        return null;
                    }).collect(Collectors.toCollection(ArrayList::new));
                    for (InputData file : files) {
                        try {
                            UpdateQuery.update(file.getTable());
                        } catch (SQLException throwables) {
                            System.err.println("[ERROR] : Fatal unable to update. + "+throwables.getMessage());

                        }
                    }
                }
                else if(commandUpdate.updateFolder!=null){
                    if(!commandUpdate.updateFolder.exists()){
                        System.err.println("[ERROR] : ["+commandUpdate.updateFolder+"] Fatal file not present.");
                        System.exit(1);
                    }
                    if(!commandUpdate.updateFolder.isDirectory()){
                        System.err.println("[ERROR] : ["+commandUpdate.updateFolder+"] Fatal path is not a directory.");
                        System.exit(1);
                    }

                    File[] files = commandUpdate.updateFolder.listFiles();
                    ArrayList<File> xmlFiles = Arrays.stream(files)
                            .filter(Objects::nonNull).filter(file -> FilenameUtils.getExtension(file.getName()).equals("xml"))
                            .collect(Collectors.toCollection(ArrayList::new));
                    for(File xmlFile : xmlFiles){
                        System.out.println(xmlFile.getAbsolutePath());
                        try {
                            InputData inputData = getData(xmlFile.getAbsolutePath());
                            //System.out.println(inputData);
                            UpdateQuery.update(inputData.getTable());
                        } catch (SQLException  | JAXBException throwables) {
                            System.err.println("[ERROR] : Unable to insert +"+throwables.getMessage());
                            System.exit(1);
                        }
                    }
                }
            }
            else if(jCommander.getParsedCommand().equals(CONSOLE)){
                Scanner scanner = new Scanner(System.in);
                while (true){
                    System.out.print("> [query] : ");
                    String input = scanner.nextLine();
                    if(input.equals("exit")){
                        System.exit(0);
                    }
                    if(input.equals("clear")){
                        try {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            continue;
                        } catch (Exception e) {
                            System.err.println("[ERROR] : "+e.getMessage());
                        }
                    }
                    if(input.trim().split(" ")[0].equals("tselect")||
                            input.trim().split(" ")[0].equals("tjoin")){
                        try {
                            ResultSet resultSet = TemporalQuery.resolveQuery(input);
                            DBTablePrinter.printResultSet(resultSet);
                            continue;
                        } catch (Exception e) {
                            System.err.println("[ERROR] : "+e.getMessage());
                        }
                    }
                    if(input.trim().split(" ")[0].equals("delete")){
                        try {
                            DeleteQuery.delete(input);
                            continue;
                        } catch (SQLException e) {
                            System.err.println("[ERROR] : "+e.getMessage());
                        }
                    }
                    Excecutor excecutor = new Excecutor();
                    try {
                        List<ResultSet> execute = excecutor.addSqlQuery(new GenericSqlBuilder(input)).execute();
                        for(ResultSet resultSet : execute){
                            DBTablePrinter.printResultSet(resultSet);
                        }
                    } catch (SQLException throwables) {
                        System.err.println("[ERROR] : "+throwables.getMessage());
                    }
                }
            }
            else if(jCommander.getParsedCommand().equals(LOGIN)){

            }
        }
    }
}
