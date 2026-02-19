package com.example.ankush.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ankush.dto.BankDto;
import com.example.ankush.dto.GetDto;
import com.example.ankush.dto.UpdateUserDto;
import com.example.ankush.entity.Bank;
import com.example.ankush.entity.StudentDocuments;
import com.example.ankush.entity.User;
import com.example.ankush.repository.StudentRepository;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    //......convert json to dto method
    public UpdateUserDto convertJsonToDto(String json) throws IOException {
        return objectMapper.readValue(json, UpdateUserDto.class);
    }

    //.......generate the student id(constum made)
    private String generateId(){
        String lastId = studentRepository.findMaxStudentId();
        if(lastId == null){
            return "Stu0001";
        }
        int nextNum = Integer.parseInt(lastId.substring(3)) +1;
        return String.format("Stu%04d",nextNum);
    }

    //......get call... return all user getdto
    public List<GetDto> allUser() {
        return studentRepository.findAll().stream()
                .map(u -> new GetDto(
                        u.getStudentId() != null ? u.getStudentId() : "N/A",
                        u.getStudentName() != null ? u.getStudentName() : "Unknown",
                        u.getStudentClass() != null ? u.getStudentClass() : "None"
                ))
                .toList();
    }

    //.......saving new user
    public UpdateUserDto saveUser(UpdateUserDto dto) throws IOException {
        User user = new User();
        String stuId = generateId();
        String projectRoot = System.getProperty("user.dir");

        if(dto.getImage() != null && !dto.getImage().isEmpty()){
            String uploadFolder = projectRoot + File.separator + "uploads" + File.separator;
            String originalName = dto.getImage().getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String fileName = stuId + extension;
            Path path = Paths.get(uploadFolder + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, dto.getImage().getBytes());
            String virturalPath = "/uploads/" + fileName;
            user.setImagePath(virturalPath);
            dto.setReturnImagePath(virturalPath);
        }

        if(dto.getDocuments()!=null && !dto.getDocuments().isEmpty()){
            String docFolder = projectRoot + File.separator + "uploads" + File.separator + "documents" + File.separator + stuId + File.separator;
            List<String> docPaths = new ArrayList<>();
            int count = 1;

            for(MultipartFile file : dto.getDocuments()){
                if(!file.isEmpty()){
                    String originalName = file.getOriginalFilename();
                    String extension = originalName.substring(originalName.lastIndexOf("."));
                    String fileName = stuId + "_" + count + extension;
                    Path path = Paths.get(docFolder + fileName);
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());
                    String virtualDocPath = "/uploads/documents/" + stuId + "/" + fileName;
                    StudentDocuments docEntity = new StudentDocuments();
                    docEntity.setDocumentPath(virtualDocPath);
                    docEntity.setUser(user);
                    user.getDocuments().add(docEntity);
                    docPaths.add(virtualDocPath);
                    count++;
                }
            }
            dto.setReturnDocumentsPaths(docPaths);
        }

        user.setStudentId(stuId);
        user.setStudentName(dto.getStudentName());
        user.setStudentClass(dto.getStudentClass());
        user.setFatherName(dto.getFatherName());
        user.setDob(dto.getDob());
        user.setGender(dto.getGender());
        user.setNationality(dto.getNationality());
        user.setPhoneNo(dto.getPhoneNo());
        user.setAddress(dto.getAddress());
        user.setAadharNo(dto.getAadharNo());

        if(dto.getBankDetails()!=null && !dto.getBankDetails().isEmpty()){
            for(BankDto bankDto : dto.getBankDetails()){
                Bank bank = new Bank();
                bank.setBankName(bankDto.getBankName());
                bank.setBranchName(bankDto.getBranchName());
                bank.setAccountNo(bankDto.getAccountNo());
                bank.setIfscCode(bankDto.getIfscCode());
                bank.setUser(user);
                user.getBankDetails().add(bank);
            }
        }

        studentRepository.save(user);
        return dto;
    }

    //.......get student by id
    public GetDto findStudent(String studentId) throws Throwable {
        User user = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return new GetDto(user.getStudentId(), user.getStudentName(), user.getStudentClass());

    }

    //........update student info
    public UpdateUserDto updateStudent(String studentId, UpdateUserDto dto) throws IOException {
        User tempUser = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        tempUser.setStudentName(dto.getStudentName());
        tempUser.setAddress(dto.getAddress());
        tempUser.setDob(dto.getDob());
        tempUser.setStudentClass(dto.getStudentClass());
        tempUser.setAadharNo(dto.getAadharNo());
        tempUser.setNationality(dto.getNationality());
        tempUser.setGender(dto.getGender());
        tempUser.setFatherName(dto.getFatherName());
        tempUser.setPhoneNo(dto.getPhoneNo());

        String projectRoot = System.getProperty("user.dir");

       // ........image logic
       if(dto.getImage() == null){
            
       }
       else{
            if(dto.getImage().isEmpty()){
                if(tempUser.getImagePath()!=null){
                    String oldFileName = tempUser.getImagePath().replace("/uploads/", "");
                    Files.deleteIfExists(Paths.get(projectRoot + File.separator + "uploads" + File.separator + oldFileName));
                    tempUser.setImagePath(null);
                }                
            }
            else{
                if(tempUser.getImagePath()!=null){
                    String oldFileName = tempUser.getImagePath().replace("/uploads/", "");
                    Files.deleteIfExists(Paths.get(projectRoot + File.separator + "uploads" + File.separator + oldFileName));
                }
                String extension = dto.getImage().getOriginalFilename().substring(dto.getImage().getOriginalFilename().lastIndexOf("."));
                String newFileName = studentId + extension;
                Path path = Paths.get(projectRoot + File.separator + "uploads" + File.separator + newFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, dto.getImage().getBytes());
                tempUser.setImagePath("/uploads/" + newFileName);
            }
       }

        //........document logic

        String docFolderPath = projectRoot + File.separator + "uploads" + File.separator + "documents" + File.separator + studentId;
        File docFolder = new File(docFolderPath);

        // ...if delete links are there
        if(dto.getDeleteDocumentPaths()!=null && !dto.getDeleteDocumentPaths().isEmpty()){

            //....check whether no of links and no of new document are equal 
            int noOfFiles = 0;
            if(dto.getDocuments() != null){
                for(MultipartFile file : dto.getDocuments()){
                    if(!file.isEmpty()){
                        noOfFiles++;
                    }
                }
            }
            if(dto.getDeleteDocumentPaths().size() != noOfFiles){
                throw new RuntimeException("   :(       Count Mismatch       :(      no of delete links not equals to new documents uploaded");
            }

            //....check whether links are present in the databases;
            for( String path : dto.getDeleteDocumentPaths()){
                Boolean found = false;
                for(StudentDocuments doc : tempUser.getDocuments()){
                    if(doc.getDocumentPath().equals(path)){
                        found = true;
                         break;
                    }
                }
                if(!found){
                    throw new RuntimeException("   :(       Count Mismatch       :(      delete link not found :" + path);
                }
            }

            //.......duplicate paths
            HashSet<String> seen = new HashSet<>();
            for(String path : dto.getDeleteDocumentPaths()){
                if(!seen.add(path)){
                    throw new RuntimeException("   :(  duplicate paths : " + path);
                }
            }

            //.....replace the document one by one
            for( int i = 0; i<dto.getDeleteDocumentPaths().size(); i++){
                String virtualPath = dto.getDeleteDocumentPaths().get(i);
                String physicalPath = virtualPath.replace("/uploads/documents/" + studentId + "/", docFolderPath + File.separator);
                MultipartFile newFile = dto.getDocuments().get(i);
                Files.write(Paths.get(physicalPath), newFile.getBytes());

            }
            
            //return all the existing paths from the db
            List<String> existingPaths = new ArrayList<>();
            for(StudentDocuments doc : tempUser.getDocuments()){
                existingPaths.add(doc.getDocumentPath());
            }
            dto.setReturnDocumentsPaths(existingPaths);

        }
        else{
             if(dto.getDocuments() == null){
            // Case 1: do nothing, but still return existing paths
            List<String> existingPaths = new ArrayList<>();
            for(StudentDocuments doc : tempUser.getDocuments()){
                existingPaths.add(doc.getDocumentPath());
            }
            dto.setReturnDocumentsPaths(existingPaths);}
        else{
            //.....method to find documents is empty or not
            Boolean hasRealFiles = false;
            for( MultipartFile file : dto.getDocuments()){
                if(!file.isEmpty()){
                    hasRealFiles = true;
                    break;
                }
            }
            //..... student ticked the box but no document in it;
            if(!hasRealFiles){
                if(docFolder.exists()){
                    deleteDirectory(docFolder);
                }
                tempUser.getDocuments().clear();
                dto.setReturnDocumentsPaths(null);
            }
            else{
                //.......student uploaded a real file;
                if(docFolder.exists()){
                    deleteDirectory(docFolder);
                }
                tempUser.getDocuments().clear();

                List<String> newPaths = new ArrayList<>();
                int count = 1;
                for(MultipartFile file : dto.getDocuments()){
                    if(!file.isEmpty()){
                        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                        String fileName = studentId + "_" + count + extension;
                        Path path = Paths.get(docFolderPath + File.separator + fileName);
                        Files.createDirectories(path.getParent());
                        Files.write(path, file.getBytes());
                        String virtualPath = "/uploads/documents/" + studentId + "/" + fileName;
                        StudentDocuments docEntity = new StudentDocuments();
                        docEntity.setDocumentPath(virtualPath);
                        docEntity.setUser(tempUser);
                        tempUser.getDocuments().add(docEntity);
                        newPaths.add(virtualPath);
                        count++;
                    }
                }
                dto.setReturnDocumentsPaths(newPaths);
            }
        }

        }

        //........handling bank
        if(dto.getBankDetails() == null){}
        else{
            if(dto.getBankDetails().isEmpty()){
                tempUser.getBankDetails().clear();
            }
            else{
                tempUser.getBankDetails().clear();
                for(BankDto bankDto : dto.getBankDetails()){
                Bank bank = new Bank();
                bank.setBankName(bankDto.getBankName());
                bank.setBranchName(bankDto.getBranchName());
                bank.setAccountNo(bankDto.getAccountNo());
                bank.setIfscCode(bankDto.getIfscCode());
                bank.setUser(tempUser);
                tempUser.getBankDetails().add(bank);
            }
            }
        }

        User savedUser = studentRepository.save(tempUser);
        dto.setReturnImagePath(savedUser.getImagePath());
        return dto;
    }


    //........delete user by student id
    @Transactional
    public void removeUser(String studentId) {
        User user = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String projectRoot = System.getProperty("user.dir");

        if (user.getImagePath() != null) {
            String fileName = user.getImagePath().replace("/uploads/", "");
            Path path = Paths.get(projectRoot + File.separator + "uploads" + File.separator + fileName);
            try { Files.deleteIfExists(path); } catch (Exception e) { /* Log error */ }
        }

        File docFolder = new File(projectRoot + File.separator + "uploads" + File.separator + "documents" + File.separator + studentId);
        if (docFolder.exists()) {
            try {
                deleteDirectory(docFolder);
            } catch (IOException e) {
                System.out.println("Could not delete doc folder: " + e.getMessage());
            }
        }
        studentRepository.deleteByStudentId(studentId);
    }

    //...... delete directory from the folder
    private void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}