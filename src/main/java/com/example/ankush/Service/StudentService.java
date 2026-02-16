package com.example.ankush.Service;

import com.example.ankush.dto.GetDto;
import com.example.ankush.dto.UpdateUserDto;
import com.example.ankush.entity.Bank;
import com.example.ankush.entity.User;
import com.example.ankush.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;









    //     ...........method for converting the json to dto

    public UpdateUserDto convertJsonToDto(String json) throws IOException {
        return objectMapper.readValue(json, UpdateUserDto.class);
    }





















    //auto generated student id,,,,,,,,stu0001
    private String generateId(){
        String lastId = studentRepository.findMaxStudentId();

        if(lastId == null){
            return "Stu0001";
        }

        int nextNum = Integer.parseInt(lastId.substring(3)) +1;
        return String.format("Stu%04d",nextNum);
    }










    // return the student info,,,,,, studentId, student name, student class
    public List<GetDto> allUser() {
        return studentRepository.findAll().stream()
                .map(u -> new GetDto(
                        u.getStudentId() != null ? u.getStudentId() : "N/A",
                        u.getStudentName() != null ? u.getStudentName() : "Unknown",
                        u.getStudentClass() != null ? u.getStudentClass() : "None"
                ))
                .toList();
    }












    // save new user,,,,,,
    public UpdateUserDto saveUser(UpdateUserDto dto) throws IOException {

        User user = new User();
        String stuId = generateId();


        // whole login for the image saving>>>>> only if the image is there
        if(dto.getImage() != null && !dto.getImage().isEmpty()){

            String projectRoot = System.getProperty("user.dir");
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
        else{
            user.setImagePath(null);
            dto.setReturnImagePath(null);
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



        // ,,,,,,,,,,,,,, hanldle bank,,,saving bank info
        if(dto.getBankDetails() != null){
            Bank bank = new Bank();

            bank.setBankName(dto.getBankDetails().getBankName());
            bank.setBranchName(dto.getBankDetails().getBranchName());
            bank.setAccountNo(dto.getBankDetails().getAccountNo());
            bank.setIfscCode(dto.getBankDetails().getIfscCode());

            bank.setUser(user);

            user.setBankDetails(bank);
        }


        studentRepository.save(user);
        return dto;
    }















    // find student by student id
    public GetDto findStudent(String studentId) throws Throwable {
        User user = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return new GetDto(user.getStudentId(), user.getStudentName(), user.getStudentClass());

    }














    //updating student using student id.,..... and deleting the old image as well
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




        // 3. Handle Image ONLY if a new file is uploaded
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String projectRoot = System.getProperty("user.dir");
            String uploadFolder = projectRoot + File.separator + "uploads" + File.separator;




            if (tempUser.getImagePath() != null) {

                String fileName = tempUser.getImagePath().replace("/uploads/", "").replace("uploads/", "");
                Path path = Paths.get(projectRoot + File.separator + "uploads" + File.separator + fileName);

                try {
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    System.out.println("Could not delete file, but continuing with DB delete: " + e.getMessage());
                }
            }

            // Save the new file
            String originalName = dto.getImage().getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String fileName = tempUser.getStudentId() + extension;
            Path path = Paths.get(uploadFolder + fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, dto.getImage().getBytes());

            // Update database field
            tempUser.setImagePath("/uploads/" + fileName);
        }
        else{


            if (tempUser.getImagePath() != null) {
                String projectRoot = System.getProperty("user.dir");
                // Extract filename from path (e.g., "/uploads/Stu0001.jpg" -> "Stu0001.jpg")
                String fileName = tempUser.getImagePath().replace("/uploads/", "").replace("uploads/", "");
                Path path = Paths.get(projectRoot + File.separator + "uploads" + File.separator + fileName);

                try {
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    System.out.println("Could not delete file: " + e.getMessage());
                }
            }

            tempUser.setImagePath(null);
            dto.setReturnImagePath(null);
        }



        // ..................................updating bank details

        //................if user update the bank details........ new datails
        if(dto.getBankDetails() != null){
            Bank bank = tempUser.getBankDetails();

            // ...........if old bank details are null
            if(bank == null){
                bank = new Bank();
                bank.setUser(tempUser);
                tempUser.setBankDetails(bank);
            }

            bank.setBankName(dto.getBankDetails().getBankName());
            bank.setBranchName(dto.getBankDetails().getBranchName());
            bank.setAccountNo(dto.getBankDetails().getAccountNo());
            bank.setIfscCode(dto.getBankDetails().getIfscCode());
        }
        // ............... if new datils are null
        else if(tempUser.getBankDetails() != null){
            tempUser.setBankDetails(null);
        }












        // 4. Save to Database
        User savedUser = studentRepository.save(tempUser);

        // 5. Update DTO to return the CURRENT data (not null)
        dto.setReturnImagePath(savedUser.getImagePath());

        return dto;
    }











    @Transactional
    public void removeUser(String studentId) {

        User user = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        if (user.getImagePath() != null) {
            String projectRoot = System.getProperty("user.dir");
            String fileName = user.getImagePath().replace("/uploads/", "").replace("uploads/", "");
            Path path = Paths.get(projectRoot + File.separator + "uploads" + File.separator + fileName);

            try {
                Files.deleteIfExists(path);
            } catch (Exception e) {
                System.out.println("Could not delete file, but continuing with DB delete: " + e.getMessage());
            }
        }

        studentRepository.deleteByStudentId(studentId);

    }

}