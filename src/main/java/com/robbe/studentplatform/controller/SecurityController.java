package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.StudentRepository;
import com.robbe.studentplatform.security.JwtRequest;
import com.robbe.studentplatform.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/security")
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private StudentRepository studentRepository;

    /**
     *  Endpoint to generate JWTtokens
     * @param authenticationRequest includes password and username
     * @return token value if authenticated
     */
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body("This user has been disabled contact your admins.");
        } catch (BadCredentialsException e) {
            ResponseEntity.status(401).body("You tried to login with invalid credentials");
        }

        final Student s = studentRepository.findByUsername(authenticationRequest.getUsername()).get(0);

        final String token = jwtTokenUtil.generateToken(s.getUsername());

        return ResponseEntity.ok("Hi "+ s.getFirstname() + " " + s.getLastname() + ",\nuse this BEARER token to authenticate other api calls: "+ token);
    }

}