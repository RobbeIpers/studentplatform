package com.robbe.studentplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robbe.studentplatform.exception.MissingDataException;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.CourseRepository;
import com.robbe.studentplatform.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @MockBean
    CourseRepository courseRepository;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    StudentRepository studentRepository;

    Student STUDENT_1 = new Student("testfirst", "testlast",24);
    Student STUDENT_2 = new Student("testfirst2", "testlast2",34);
    @Autowired
    MockMvc mvc;

    @Test
    void getStudent() throws Exception{
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.of(STUDENT_1));
        mvc.perform(MockMvcRequestBuilders
                        .get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.firstname", is("testfirst")))
                .andExpect(jsonPath("$.age", is(24)));
    }

    @Test
    void getStudentCoursesById() throws Exception{
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.of(STUDENT_1));

        mvc.perform(MockMvcRequestBuilders
                .get("/students/1/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> emptyArray());
    }

    @Test
    void getAllStudents() throws Exception {
        List<Student> students = new ArrayList<>(Arrays.asList(STUDENT_1, STUDENT_2));
        Mockito.when(studentRepository.findAll()).thenReturn(students);
        mvc.perform(MockMvcRequestBuilders
                        .get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].firstname", is("testfirst2")))
                .andExpect(jsonPath("$[0].firstname", is("testfirst")));

    }

    @Test
    public void deleteStudent_success() throws Exception {
        Mockito.when(studentRepository.findById(2)).thenReturn(Optional.of(STUDENT_2));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/students/2")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void deleteStudent_notFound() throws Exception {
        Mockito.when(studentRepository.findById(99)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .delete("/students/99")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MissingDataException))
                        .andExpect(result ->
                        assertEquals("No student with that id", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
    @Test
    void makeStudent() throws Exception {
        Student s = Student.builder()
                .firstname("Robbe")
                .lastname("Ipers")
                .age(24).build();

        Mockito.when(studentRepository.save(s)).thenReturn(s);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(s));

        mvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.courses").doesNotExist())
                .andExpect(jsonPath("$.age", is(24)))
                .andExpect(jsonPath("$.firstname", is("Robbe")))
                .andExpect(jsonPath("$.lastname", is("Ipers")));

    }
}