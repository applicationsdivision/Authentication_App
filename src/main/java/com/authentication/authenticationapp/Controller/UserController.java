package com.authentication.authenticationapp.Controller;


import com.authentication.authenticationapp.Service.UserService;
import com.backoffice.BackofficeInternetBanking.dto.loginDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.authentication.authenticationapp.exceptions.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("api/user")
public class UserController {


    private UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService=userService;
    }
    @PostMapping("login")
    public  ResponseEntity<String> login(@RequestBody loginDto loginDto){
        try {


            String username = "bnr\\\\" + loginDto.getUsername();
            System.out.println(username);
            ResponseEntity<String> response = userService.authenticateUser(loginDto.getUsername(), loginDto.getPassword());

            if(response.getStatusCode()== HttpStatus.UNAUTHORIZED){

                return new ResponseEntity<String>("Invalid credentils",HttpStatus.UNAUTHORIZED );
            }
            System.out.println("authenticate");
            return response;
        }
        catch (BadCredentialsException e) {
           return new ResponseEntity<String>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }



}
