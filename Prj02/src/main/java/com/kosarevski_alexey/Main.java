package com.kosarevski_alexey;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
/*    System.setProperty("log4j.configurationFile",
            "log4j2.xml");
        Logger logger1 = LogManager.getLogger("duplicatesBySum");
        Logger logger2 = LogManager.getLogger("duplicatesByName");
        logger1.info("aff");
        logger1.error("afaf222");
        logger2.error("error");
        logger2.info("info");*/
        ArrayList<File> dirs = new ArrayList<>();
        if(args.length != 0 ){                                               // проверка переданных параметров на null
            for (String arg : args){
                File file = new File(arg);
                if(file.exists() && file.canRead() && !file.isHidden()){     // проверка каждого переданного путя на существование и права доступа
                    dirs.add(file);
                }else {
                    System.out.println("incorrect path to directory " + arg);
                    return;
                }
            }
        }
        else {
            System.out.println("list of directories is empty");
            return;
        }

        Manager m = new Manager();
        for(File f : dirs){
            m.searchMp3(f);
        }
        try {
            m.parseMp3();
        } catch (InvalidDataException | IOException | UnsupportedTagException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }
}
