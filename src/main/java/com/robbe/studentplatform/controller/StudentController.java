package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired // automatically get bean called StudentRepository to handle data
    private StudentRepository studentRepository;

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable("id") int id){
        return new Student("Robbe", "Iepers", 24);
    }

    @GetMapping
    public Iterable<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    @DeleteMapping("")
    public String deleteStudent(){
        return "delete";
    }

    @PostMapping("")
    public Student makeStudent(
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("age") int age){
        Student s = new Student(firstName, lastName, age);
        studentRepository.save(s);
        return s;
    }
}
