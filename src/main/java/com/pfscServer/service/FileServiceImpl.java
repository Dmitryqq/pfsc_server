package com.pfscServer.service;


import com.pfscServer.domain.Activity;
import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.domain.Commit;
import com.pfscServer.domain.Config;
import com.pfscServer.domain.File;
import com.pfscServer.domain.FileType;
import com.pfscServer.exception.ServiceException;
import com.pfscServer.repo.CommitHistoryRepo;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.repo.FileTypesRepo;
import com.pfscServer.repo.FilesRepo;
import com.pfscServer.util.FileArray;
import com.pfscServer.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
    @Autowired
    private CommitHistoryServiceImpl commitHistoryService;
    @Autowired
    CommitHistoryRepo historyRepo;
    @Autowired
    UserDetailsServiceImpl userService;

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
    public void deleteById(Long id) throws IOException, ServiceException {
        File file = fileRepo.findById(id).orElse(null);
        ApplicationUser user = userService.getCurrentUser();
        if (!(
                (file.getFileType().getRole().getRoleName().equals("User") && user.getId() == file.getCommit().getUserId()) ||
                        !file.getFileType().getRole().getRoleName().equals("User") &&
                                (user.getRole().getRoleName().equals("Admin") || user.getRole().getId() == file.getFileType().getRoleId())
        ))
            throw new ServiceException("Удаление файла типа \"" + file.getFileType().getName() + "\" данным пользователем запрещено", HttpStatus.FORBIDDEN);
        Path path = Paths.get(file.getPath());
        Files.delete(path);
        fileRepo.delete(file);

    }

    @Override
    public List<File> create(Long fileId, Long commitId, MultipartFile[] files) throws IOException, ServiceException {
        List<File> file = new ArrayList<>();
        Commit commit = commitsRepo.findById(commitId).orElse(null);
        FileType typeOfFile = typeOfFileRepo.findById(fileId).orElse(null);
        Config rootDir = configRepo.findById(1L).orElse(null);

        File temp = new File();

        int fileLength = files.length;
        int count = fileRepo.countFiles(commitId, fileId) + fileLength;


        if (count > typeOfFile.getMaxAmount()) {
            //превышен лимит кол-ва файлов
            throw new ServiceException("Превышен лимит количества файлов", HttpStatus.BAD_REQUEST);
        } else {

            if (files == null || commit == null || typeOfFile == null) {
                //одно из полей пустое
                throw new ServiceException("одно из полей пустое", HttpStatus.BAD_REQUEST);
            } else {
                ApplicationUser user = userService.getCurrentUser();
                if (!(
                        (typeOfFile.getRole().getRoleName().equals("User") && user.getId() == commit.getUserId()) ||
                                !typeOfFile.getRole().getRoleName().equals("User") &&
                                        (user.getRole().getRoleName().equals("Admin") || user.getRole().getId() == typeOfFile.getRoleId())
                ))
                    throw new ServiceException("Добавление файла типа \"" + typeOfFile.getName() + "\" данным пользователем запрещено", HttpStatus.FORBIDDEN);

                //Проверка возможности добавления файлов, если коммит отклонен или принят
                if (!historyRepo.findByCommitIdAndActivity(commitId, Activity.REJECT.getTitle()).isEmpty() || !typeOfFile.isEnableAfterAccept() && !historyRepo.findByCommitIdAndActivity(commitId, Activity.ACCEPT.getTitle()).isEmpty()) {
                    throw new ServiceException("Добавление файлов для данного наката заблокировано", HttpStatus.LOCKED);
                } else {
                    String path = commit.getDir(rootDir.getValue()) + "\\" + typeOfFile.getName();

                    fileOperation(path, commitId, fileId, typeOfFile, files);

                    for (MultipartFile uploadedFile : files) {
                        if (uploadedFile.getSize() > typeOfFile.getMaxSize() * 1024 * 1024) {
                            //рамер файла превышает допустимый
                            throw new ServiceException("размер " + uploadedFile.getOriginalFilename() + " превыщает допустимый", HttpStatus.BAD_REQUEST);
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
                            tempFile.setCreateDate(LocalDateTime.now());
                            file.add(tempFile);
                            commitHistoryService.create(commit, Activity.ADDFILE);
                        }
                    }
                    fileRepo.saveAll(file);
                    return file;
                }
            }
        }
    }

    @Override
    public void deleteByCommit(Long commitId) {
        List<File> file = fileRepo.findByCommitId(commitId);
        fileRepo.deleteAll(file);
    }

    @Override
    public String comparison(Long commitId) throws IOException {

        List<String> file = fileRepo.comparisonAllFiles(commitId);
        List<FileArray> fileArrays = new ArrayList<FileArray>();
        if (file != null) {
            for (String tempPath : file) {
                FileArray tempFile = new FileArray();
                java.io.File tempFilePath = new java.io.File(tempPath);
                tempFile.fileName = tempFilePath.getName();
                tempFile.path = tempFilePath.getName();
                tempFile.content = FileCopyUtils.copyToByteArray(tempFilePath);
                tempFile.downloaded = true;
                fileArrays.add(tempFile);
            }
        }

        String message = FileUtil.compareFile(fileArrays);
        return message;
    }

    @Override
    public void delete(Long id) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String fileRequired(Long commitId) {
        List<FileType> fileType = typeOfFileRepo.findAll();
        for (FileType temp : fileType) {
            if (temp.isRequired()) {
                if (fileRepo.countFiles(commitId, temp.getId()) <= 0) {
                    return temp.getName();
                }
            }
        }
        return null;
    }

    public void fileOperation(String path, Long commitId, Long fileId, FileType typeOfFile, MultipartFile[] files) throws IOException, ServiceException {
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
            tempFile.content = uploadFile.getBytes();
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
                throw new ServiceException("У файла неудовлетворительное расширение", HttpStatus.BAD_REQUEST);
            }
        }

        //сравнение файлов
        //допилить, нет сравнения по наименованию
        String message = FileUtil.compareFile(fileArrays);
        if (message != null) {
            throw new ServiceException("Файл " + message + " уже существует ", HttpStatus.BAD_REQUEST);
        }
    }

}


