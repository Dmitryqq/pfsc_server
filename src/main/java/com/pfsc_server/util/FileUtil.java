/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Comparator;

/**
 *
 * @author User
 */
public class FileUtil {
    
    public static void createDir(String dir) throws IOException{
        Path path = Paths.get(dir);
        Files.createDirectories(path);
    }
    
    public static void deleteDir(String dir) throws IOException{
        Path rootPath = Paths.get(dir);
        Files.walk(rootPath)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
    }

}
