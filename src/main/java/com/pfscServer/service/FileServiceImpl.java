package com.pfscServer.service;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.Config;
import com.pfscServer.domain.File;
import com.pfscServer.domain.FileType;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.repo.FileTypesRepo;
import com.pfscServer.repo.FilesRepo;
import com.pfscServer.util.FileArray;
import com.pfscServer.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements EntityService<File, Long>, FileService {

    @Autowired
    private FilesRepo fileRepo;
    @Autowired
    private ConfigsRepo configRepo;
    @Autowired
    private FileTypesRepo typeOfFileRepo;
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
    public List<File> create(Long fileId, Long commitId, MultipartFile[] files) throws IOException {
        List<File> file = new ArrayList<>();
        Commit commit = commitsRepo.findById(commitId).orElse(null);
        FileType typeOfFile = typeOfFileRepo.findById(fileId).orElse(null);
        Config rootDir = configRepo.findById(1L).orElse(null);

        File temp = new File();

        int fileLength = files.length;
        int count = fileRepo.countFiles(commitId, fileId) + fileLength;

        if (count > typeOfFile.getMaxAmount()) {
            //превышен лимит кол-ва файлов
            temp.setPath("Превышен лимит");
            file.add(temp);
            return file;
        } else {

            if (rootDir == null || commit == null || typeOfFile == null) {
                //одно из полей пустое
                temp.setPath("одно из полей пустое");
                file.add(temp);
                return file;
            } else {
                String path = commit.getDir(rootDir.getValue()) + "\\" + typeOfFile.getName();
                FileUtil.directoryExist(path);
                //Кол-во файлов по данному commit id
                List<String> allFile = fileRepo.allFiles(commitId, fileId);
                //лист байтов
                List<FileArray> fileArrays = new ArrayList<>();

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
                    for (String tempPath : allFile) {
                        FileArray tempFile = new FileArray();
                        java.io.File tempFilePath = new java.io.File(tempPath);
                        tempFile.fileName = tempFilePath.getName();
                        tempFile.path = tempPath;
                        tempFile.content = FileCopyUtils.copyToByteArray(tempFilePath);
                        tempFile.downloaded = false;
                        fileArrays.add(tempFile);
                    }
                }


                //выборка байтов загружаемых файлов
                for (MultipartFile uploadFile : files) {
                    FileArray tempFile = new FileArray();
                    String pathFile = path + "\\" + uploadFile.getOriginalFilename();
                    tempFile.fileName = uploadFile.getOriginalFilename();
                    tempFile.path = pathFile;
                    System.out.println(tempFile.content + "\n");
                    tempFile.downloaded = true;
                    fileArrays.add(tempFile);
                }

                //проверка на расширение файла
                String temps = typeOfFile.getTypes();
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
                String message = FileUtil.compareFile(fileArrays);
                if (message != null) {
                    temp.setPath("Файл " + message + " уже существует ");
                    file.add(temp);
                    return file;
                }

                for (MultipartFile uploadedFile : files) {
                    if (uploadedFile.getSize() > typeOfFile.getMaxSize() * 1024 * 1024) {
                        //рамер файла превышает допустимый
                        temp.setPath("размер файла превыщает допустимый");
                        file.add(temp);
                        return file;
                    } else {
                        File tempFile = new File();
                        tempFile.setFileTypeId(fileId);
                        tempFile.setCommitId(commitId);
                        tempFile.setFileType(typeOfFile);
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


