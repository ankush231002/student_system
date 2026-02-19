package com.example.ankush.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ankush.dto.GetDto;
import com.example.ankush.dto.UpdateUserDto;
import com.example.ankush.service.StudentService;

@RestController
@RequestMapping("/user")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/all")
    public ResponseEntity<List<GetDto>> getAllDto(){
        return ResponseEntity.ok(studentService.allUser());
    }

    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserDto> createUser(
            @RequestPart("data") String studentDtoJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents) throws IOException {
  
        UpdateUserDto dto = studentService.convertJsonToDto(studentDtoJson);

        if(imageFile != null){
            dto.setImage(imageFile);
        }
        if(documents != null && !documents.isEmpty()){
            dto.setDocuments(documents);
        }

        UpdateUserDto savedUser = studentService.saveUser(dto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<GetDto> getById(@PathVariable String studentId) throws Throwable {
        GetDto user = studentService.findStudent(studentId);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateUserDto> UpdateById(
            @PathVariable String studentId,
            @RequestPart("data") String studentJsonDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents) throws IOException {

        UpdateUserDto dto = studentService.convertJsonToDto(studentJsonDto);

        if(imageFile!=null){
            dto.setImage(imageFile);
        }
        if(documents != null){
            dto.setDocuments(documents);
        }

        UpdateUserDto updatedUser = studentService.updateStudent(studentId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteById(@PathVariable String studentId){
        studentService.removeUser(studentId);
        return ResponseEntity.ok("deleted");
    }
}
