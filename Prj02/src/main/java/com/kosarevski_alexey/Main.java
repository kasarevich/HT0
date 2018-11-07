package com.kosarevski_alexey;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private final static String NAME_OF_HTML_FILE = "mp3Files.html";
    public static void main(String[] args) {
        ArrayList<File> dirs = new ArrayList<>();
        if(args.length != 0 ){
            for (String arg : args){
                File file = new File(arg);
                if(file.exists() && file.canRead() && !file.isHidden()){
                    dirs.add(file);
                }else {
                    System.out.println("incorrect path to directory " + arg);
                    continue;
                }
            }
        }
        else {
            System.out.println("list of directories is empty");
            return;
        }

        Manager manager = new Manager();
            for(File f : dirs){
                manager.searchMp3(f);
            }

        try {
            manager.parseMp3();
        } catch (InvalidDataException | IOException | UnsupportedTagException e) {
            System.out.println("The library \"mp3agic\" can't get access to data");
        }

             System.out.println(manager.getStringInfo());

        try {
            manager.writeHTML(NAME_OF_HTML_FILE);
        } catch (IOException e) {
            System.out.println("Error of writing HTML file");;
        }
    }
}
