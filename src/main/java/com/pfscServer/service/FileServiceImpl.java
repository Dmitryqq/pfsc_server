package com.pfscServer.service;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.Config;
import com.pfscServer.domain.File;
import com.pfscServer.domain.FileType;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.repo.FilesRepo;
import com.pfscServer.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;
import com.pfscServer.repo.FileTypesRepo;

@Service
public class FileServiceImpl implements EntityService<File, Long>, FileService {

    @Autowired
    private FilesRepo fileRepo;
    @Autowired
    private ConfigsRepo configRepo;
    @Autowired
    private FileTypesRepo fileTypeRepo;
    @Autowired
    private CommitsRepo commitsRepo;

    @Override
    public List<File> getAll() {
        return fileRepo.findAll();
    }

    @Override
    public File getById(Long id) {
        return fileRepo.findById(id).orElse(null);
    }

    @Override
    public File save(File file) {
        return null;
    }

    @Override
    public void delete(Long id) throws IOException {
        File file = fileRepo.findById(id).orElse(null);
        Path path = Paths.get(file.getPath());
        Files.delete(path);
        fileRepo.delete(file);
    }

    @Override
    public List<File> create(Long fileTypeId, Long commitId, MultipartFile[] files) throws IOException {
        List<File> file = new ArrayList<>();
        Commit commit = commitsRepo.findById(commitId).orElse(null);
        FileType fileType = fileTypeRepo.findById(fileTypeId).orElse(null);
        Config rootDir = configRepo.findFirstByName("rootDir");

        File temp = new File();

        int fileLength = files.length;
        int count = fileRepo.countFiles(commitId, fileTypeId) + fileLength;

        if (count > fileType.getMaxAmount()) {
            //превышен лимит кол-ва файлов
            temp.setPath("Превышен лимит");
            file.add(temp);
            return file;
        } else {

            if (rootDir == null || commit == null || fileType == null) {
                //одно из полей пустое
                temp.setPath("одно из полей пустое");
                file.add(temp);
                return file;
            } else {
                String path = commit.getDir(rootDir.getValue()) + "\\" + fileType.getName();
                FileUtil.directoryExist(path);
                //Кол-во файлов по данному commit id
                List<String> allFile = fileRepo.allFiles(commitId, fileTypeId);
                //лист байтов
                List<byte[]> listByte = new ArrayList<>();

                /*
                //При помощи java.nio выборка файлов
                List<Path> newFilesNIO = new ArrayList<Path>();
                if (allFile != null) {
                    for (String tempPath : allFile) {
                        Path tempFilePath = Paths.get(tempPath);
                        if (Files.exists(tempFilePath)) {
                            newFilesNIO.add(tempFilePath);
                        }
                    }
                }
                //При помощи java.io выборка байтовых значений файлов
                for (Path temps : newFilesNIO) {
                    listByte.add(FileCopyUtils.copyToByteArray(temps.toFile()));
                }
                */


                //При помощи java.io выборка байтовых значений файлов
                if (allFile != null) {
                    listByte = FileUtil.takeFiles(allFile);
                }


                //выборка байтов загружаемых файлов
                for (MultipartFile uploadFile : files) {
                    String pathFile = path + "\\" + uploadFile.getOriginalFilename();
                    allFile.add(pathFile);
                    listByte.add(uploadFile.getBytes());
                }

                //проверка на расширение файла
                String temps = fileType.getTypes();
                String[] arrStrings1 = temps.split(",");
                for (MultipartFile uploadFile : files) {
                    String tempType = FileUtil.getFileExtension(uploadFile.getOriginalFilename());
                    Integer counts = 0;
                    for (String arrStrings11 : arrStrings1) {
                        if (tempType.equals(arrStrings11)) {
                            counts++;
                        }
                    }
                    if (counts == 0) {
                        temp.setPath("У файла неудовлетворительное расширение");
                        file.add(temp);
                        return file;
                    }
                }

                //сравнение файлов
                //допилить, нет сравнения по наименованию
                if (FileUtil.compareFile(listByte) || FileUtil.compareFileName(allFile)) {
                    temp.setPath("есть два одинаковых файла");
                    file.add(temp);
                    return file;
                }

                for (MultipartFile uploadedFile : files) {
                    if (uploadedFile.getSize() > fileType.getMaxSize() * 1024) {
                        //рамер файла превышает допустимый
                        temp.setPath("размер файла превыщает допустимый");
                        file.add(temp);
                        return file;
                    } else {
                        File tempFile = new File();
                        tempFile.setCommitId(commitId);
                        tempFile.setFileTypeId(fileTypeId);
                        tempFile.setFileType(fileType);
                        tempFile.setCommit(commit);

                        String pathFile = path + "\\" + uploadedFile.getOriginalFilename();
                        tempFile.setPath(pathFile);

                        //выполнение сохранения файла через java.nio
                        Path getPathFile = Paths.get(pathFile);
                        uploadedFile.transferTo(getPathFile);

                        //выполнение сохранения файла через java.io
                        //uploadedFile.transferTo(new java.io.File(pathFile));

                        file.add(tempFile);
                    }
                }
                fileRepo.saveAll(file);
                return file;
            }
        }
    }

    @Override
    public void deleteByCommit(Long commitId) {
        List<File> file = fileRepo.findByCommitId(commitId);
        fileRepo.deleteAll(file);
    }
}
