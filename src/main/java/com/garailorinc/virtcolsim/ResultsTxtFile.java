package com.garailorinc.virtcolsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ResultsTxtFile
{
    private FileWriter write;
    private PrintWriter print_line;
    private String pathFile;
    private boolean append_to_file = false;

    public String createFile(String path) {
        try {
            this.pathFile = path + "\\virtcolresult" + generateDateTag() + ".txt";
            System.out.println(pathFile);
            write = new FileWriter(pathFile, append_to_file);
            print_line = new PrintWriter(write);
        }
        catch (IOException e){e.printStackTrace();}
        return pathFile;
    }

    public void writeToFile(String textLine)  {
        try {
            print_line.printf("%s" + "%n", textLine);
        }
        catch (Exception e){e.printStackTrace();}

    }

    public void closeFile(){
        print_line.close();
    }


    private String generateDateTag() {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
        String DateTag = dateFormat.format(date);
        return DateTag;
    }
}