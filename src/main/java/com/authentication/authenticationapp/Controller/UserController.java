package com.authentication.authenticationapp.Controller;


import com.authentication.authenticationapp.Service.EmailService;
import com.authentication.authenticationapp.Service.UserService;
import com.authentication.authenticationapp.dto.EmailDto;
import com.authentication.authenticationapp.dto.OtpValidationDto;
import com.authentication.authenticationapp.dto.loginDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import com.authentication.authenticationapp.Service.AppLogger;


import java.util.concurrent.TimeUnit;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("api/user")
public class UserController {


    private UserService userService;
    private EmailService emailService;

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserController(UserService userService,EmailService emailService,RedisTemplate redisTemplate)
    {
        this.userService=userService;
        this.emailService=emailService;
        this.redisTemplate=redisTemplate;
    }
    @PostMapping("login")
    public  ResponseEntity<String> login(@RequestBody loginDto loginDto){
        try {

            AppLogger.LOGGER.info("User " + loginDto.getUsername() +" login attempt");
            String username = "bnr\\\\" + loginDto.getUsername();
            System.out.println(username);
            ResponseEntity<String> response = userService.authenticateUser(loginDto.getUsername(), loginDto.getPassword());



            if(response.getStatusCode()== HttpStatus.UNAUTHORIZED){
                AppLogger.LOGGER.info("User " + loginDto.getUsername() +" login failed");
                return new ResponseEntity<String>("Invalid credentils",HttpStatus.UNAUTHORIZED );
            }
            AppLogger.LOGGER.info("User " + loginDto.getUsername() +" login successful");
            System.out.println("authenticate");
            // Generate OTP
            String otp = userService.generateOtp();
            String email = extractEmailFromResponse(response.getBody());

            // Send OTP email
            emailService.sendOtpEmail(email, otp);
            AppLogger.LOGGER.info("OTP sent");

            // Cache username and OTP
            cacheOtp(loginDto.getUsername(), otp);
            return response;
        }
        catch (BadCredentialsException | JsonProcessingException e) {
            return new ResponseEntity<String>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/attach_email")
    public ResponseEntity<String> attachEmail(@RequestBody EmailDto emaildto) {
        // Generate OTP
        String otp = userService.generateOtp();
        // Send OTP email
        emailService.sendOtpEmail(emaildto.getEmail(), otp);

        cacheOtp(emaildto.getEmail(), otp);

        return new ResponseEntity<>("Otp is sent to the email. It will expire in 5 minutes.",HttpStatus.OK);

    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidationDto otpValidationDto) {
        String cachedOtp = getCachedOtp(otpValidationDto.getUsername());
        if (cachedOtp != null && cachedOtp.equals(otpValidationDto.getOtp())) {
            AppLogger.LOGGER.info("OTP validated successfully");
            return new ResponseEntity<>(new String("OTP validated successfully"), HttpStatus.OK);
        } else {
            AppLogger.LOGGER.info("Invalid OTP");
            return new ResponseEntity<>(new String("Invalid OTP"), HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractEmailFromResponse(String responseBody) throws JsonProcessingException {
        // Extract email from response JSON body
        // Assuming responseBody is a JSON string, use a JSON library to extract the "mail" field
        // using Jackson ObjectMapper:
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("data").path("mail").asText();
    }

    private void cacheOtp(String username, String otp) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(username, otp, 5, TimeUnit.MINUTES); // Cache for 5 minutes
    }

    private String getCachedOtp(String username) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(username);
    }
}