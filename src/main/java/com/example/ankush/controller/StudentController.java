package com.example.ankush.controller;

import com.example.ankush.Service.StudentService;
import com.example.ankush.dto.GetDto;
import com.example.ankush.dto.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class StudentController {
    @Autowired
    private StudentService studentService;










    // get all the student info,,,,,,,, only getDto
    @GetMapping("/all")
    public List<GetDto> getAllUser(){
        return studentService.allUser();
    }


















    //                         mapping,,,,,,, creating new student,,,,,,,

    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserDto> createUser(
            @RequestPart("data") String studentDtoJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {


        //               converting the studentDtoJson to dto,,,,,,,,,,,using service
        UpdateUserDto dto = studentService.convertJsonToDto(studentDtoJson);


        //              if image is there,,,,,,, put it into the dto,,,,,,,
        if(imageFile != null){
            dto.setImage(imageFile);
        }

        UpdateUserDto savedUser = studentService.saveUser(dto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }





















    //                 ,,,,,,,,,,,,,,,,, get student info by id,,,,,,,,,,,,,,,,,
    @GetMapping("/{studentId}")
    public ResponseEntity<GetDto> getById(@PathVariable String studentId) throws Throwable {
        GetDto user = studentService.findStudent(studentId);
        return ResponseEntity.ok(user);
    }



















    //                        ,,,,update user by student id,,,,,,, updateUserDto
    @PutMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserDto> UpdateById(
            @PathVariable String studentId,
            @RequestPart("data") String studentJsonDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {


        //        ,,,,,,,,converting json to dto
        UpdateUserDto dto = studentService.convertJsonToDto(studentJsonDto);

        //      ,,,,,,,,, set the dto image,,, only if the imagefile is there
        if(imageFile!=null){
            dto.setImage(imageFile);
        }

        UpdateUserDto updatedUser = studentService.updateStudent(studentId, dto);
        return ResponseEntity.ok(updatedUser);
    }





















    //            ...............deleting the student info using student id
    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteById(@PathVariable String studentId){
        studentService.removeUser(studentId);
        return ResponseEntity.ok("deleted");
    }









}
