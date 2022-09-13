package com.robbe.studentplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robbe.studentplatform.exception.MissingDataException;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.StudentRepository;
import com.robbe.studentplatform.security.JwtTokenUtil;
import com.robbe.studentplatform.security.JwtUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@WebMvcTest(value = StudentController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenUtil.class)})
class StudentControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    StudentRepository studentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    JwtUserDetailsService jwtUserDetailsService;
    String jwtToken;
    UserDetails DUMMY = new User("Robbe_Ipers1998", "password", new ArrayList<>());

    @BeforeEach
    void setAuthorization(){
        STUDENT_1.setUsername();
        jwtToken = jwtTokenUtil.generateToken(DUMMY.getUsername());
        when(studentRepository.findByUsername(DUMMY.getUsername())).thenReturn(Collections.singletonList(STUDENT_1));
        when(jwtUserDetailsService.loadUserByUsername(DUMMY.getUsername())).thenReturn(DUMMY);
    }

    Student STUDENT_1 = new Student("testfirst", "testlast","$2a$12$aFJPtJjYUNVjxW7NFwt/gORIMicd1vkLEEN1ciutRbVmcU1cNzJEi",24);
    Student STUDENT_2 =  new Student("testfirst2", "testlast2","$2a$12$aFJPtJjYUNVjxW7NFwt/gORIMicd1vkLEEN1ciutRbVmcU1cNzJEi",34);
    @Autowired
    MockMvc mvc;

    @BeforeTestClass
    void  setSecurity(){
        STUDENT_1.setUsername();
        STUDENT_2.setUsername();
    }
    @Test
    void getStudent() throws Exception{
        Mockito.when(studentRepository.findById(1)).thenReturn(Optional.of(STUDENT_1));
        mvc.perform(MockMvcRequestBuilders
                        .get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
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
                .header("Authorization", "Bearer " + jwtToken)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                        .andExpect(status().isOk());
    }

    @Test
    public void deleteStudent_notFound() throws Exception {
        Mockito.when(studentRepository.findById(99)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .delete("/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                        .andExpect(status().isNotFound())
                        .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MissingDataException))
                        .andExpect(result ->
                        assertEquals("No student with that id", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
    @Test
    void makeStudent() throws Exception {
       Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenAnswer(i->i.getArguments()[0]);
       String body =
                """
                {
                "firstname": "Robbe",
                "lastname": "Ipers",
                "password": "password",
                "age": "24"
                }
                """;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(body);

        mvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.courses").doesNotExist())
                .andExpect(jsonPath("$.age", is(24)))
                .andExpect(jsonPath("$.firstname", is("Robbe")))
                .andExpect(jsonPath("$.lastname", is("Ipers")));

    }
}