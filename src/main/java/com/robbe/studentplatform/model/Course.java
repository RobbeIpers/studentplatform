package com.robbe.studentplatform.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @Builder.Default
    private int capacity = 25;
    @ManyToMany(mappedBy = "courses")
    private List<Student> students;
    @Builder.Default
    @Column(name = "endTime")
    @JsonFormat(pattern="T'HH:mm:ss")
    private Time startTime = null;

    @Builder.Default
    @Column(name = "startTime")
    @JsonFormat(pattern="T'HH:mm:ss")
    private Time endTime = null;

    public Course(int capacity, String teacher, String name, ArrayList<Student> students, Time startTime, Time endTime) {
        this.capacity = capacity;
        this.teacher = teacher;
        this.name = name;
        this.students = students;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * add student to the course
     * @param s student to be added
     */
    public void addStudent(Student s){
        if(!students.contains(s)){
            students.add(s);
            s.getCourses().add(this);
        }
    }
}
