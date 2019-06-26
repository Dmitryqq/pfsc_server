/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.util;

import org.springframework.util.FileCopyUtils;

import java.io.Console;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author User
 */
public class FileUtil {

    public static void createDir(String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.createDirectories(path);
    }

    public static void deleteDir(String dir) throws IOException {
        Path rootPath = Paths.get(dir);
        Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    //создание директории если ее не существует
    public static void directoryExist(String path) throws IOException {
        System.out.println(path);
        Path testFilePath = Paths.get(path);
        if (!Files.exists(testFilePath)) {
            Files.createDirectory(testFilePath);
        }
    }

    //Выборка байтовых значений списка файлов по их путям
    public static List<byte[]> takeFiles(List<String> files) throws IOException {
        List<byte[]> temp = new ArrayList<byte[]>();
        for (String tempPath : files) {
            java.io.File tempFilePath = new java.io.File(tempPath);
            temp.add(FileCopyUtils.copyToByteArray(tempFilePath));
        }
        return temp;
    }

    //Сравнение списка байтов файлов
    public static String compareFile(List<FileArray> files) {

        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).downloaded == true) {
                for (int j = 0; j < files.size(); j++) {
                    if (Arrays.equals(files.get(i).content, files.get(j).content) && i != j) {

                        return files.get(i).fileName;
                    }
                }
            }
        }

        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).downloaded == true) {
                for (int j = 0; j < files.size(); j++) {
                    if (files.get(i).path.equals(files.get(j).path) && i != j) {
                        return files.get(i).fileName;
                    }
                }
            }
        }

        return null;
    }



    //Сравнение списка наименований файлов
    public static boolean compareFileName(List<String> files) {

        return false;
    }

    //метод определения расширения файла
    public static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
