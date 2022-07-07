package com.robbe.studentplatform.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Data
public class Course {
    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Teacher is mandatory")
    private String teacher;
    private int capacity = 25;
    @ManyToMany(mappedBy = "courses")
    private List<Student> students;
    public Course(String name, String teacher, int capacity) {
        this.name = name;
        Course c;
        this.teacher = teacher;
        this.capacity = capacity;
    }

    public void addStudent(Student s){
        if(!students.contains(s)){
            students.add(s);
            s.getCourses().add(this);
           // s.addCourse(this);
        }
    }
    public Course() {
    }
}
