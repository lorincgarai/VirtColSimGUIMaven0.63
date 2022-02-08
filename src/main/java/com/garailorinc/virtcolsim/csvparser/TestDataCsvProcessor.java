package com.garailorinc.virtcolsim.csvparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by garail on 2020. 01. 10..
 */
public class TestDataCsvProcessor {
    private String dataExtension = ".csv";
    private String delimiter = ";";
    private DataBase database = new DataBase();

    public void parseCsvsIntoDatabase() throws IOException
    {

        try (Stream<Path> paths = Files.walk(FileSystems.getDefault().getPath("").toAbsolutePath(), 1))
        {
            paths.filter(path -> (Files.isRegularFile(path) && String.valueOf(path).endsWith(dataExtension)))
                    .forEach(path ->
                    {
                        try
                        {
                            addCsvContentToDatabase(path);
                        } catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
        }
    }
    private void addCsvContentToDatabase(Path filepath) throws IOException
    {
        String fileNameWithExtension = filepath.getFileName().toString();
        String[] fileNameWithoutExtension = fileNameWithExtension.split("\\."); //separate from extension
        String[] fileName = fileNameWithoutExtension[0].split("-");
        String name = fileName[2];
        Enum gender = determineGenderFrom(fileName[3]);

        int age = Integer.parseInt(fileName[4]);
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(filepath)));)
        {
            Subject subject = new Subject(name, age, gender);
            String line;
            line = reader.readLine();
            while ((line = reader.readLine()) != null)
            {
                String[] linevalues = line.split(delimiter);
                ColorChange colorChange = new ColorChange(Integer.valueOf((linevalues[0]).trim()), Integer.valueOf(linevalues[1].trim()));
                String colorChangeKey = colorChange.getKeyInString();
                ResultValue resultValue = new ResultValue(Double.valueOf(linevalues[2].trim()), Double.valueOf(linevalues[3].trim()));
                subject.addColorChangeResult(colorChangeKey, resultValue);
            }
            database.addSubject(subject);
        }

    }
    private Enum determineGenderFrom(String fileNameGenderPart) {
        if (fileNameGenderPart.equalsIgnoreCase("ferfi"))
        {return Gender.MALE;}
        else if (fileNameGenderPart.equalsIgnoreCase("no"))
        {return Gender.FEMALE;}
        else throw new RuntimeException("gender not readable, string parsed: " + fileNameGenderPart);
    }

    public DataBase getDatabase()
    {
        return database;
    }

}
