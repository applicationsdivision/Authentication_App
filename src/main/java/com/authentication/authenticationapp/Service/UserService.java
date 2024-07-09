package com.authentication.authenticationapp.Service;



import com.authentication.authenticationapp.model.AuthenticationRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class UserService {



    private final String authUrl = "http://172.16.21.24:4045/ldap/api/v1/authentication";



    public ResponseEntity<String> authenticateUser(String username,String password){
        try {
            RestTemplate restTemplate = new RestTemplate();
            String user = "bnr\\" + username;

            AuthenticationRequest authRequest = new AuthenticationRequest(user, password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(authRequest, headers);

            return restTemplate.exchange(authUrl, HttpMethod.POST, entity, String.class);

        }catch (HttpClientErrorException e){
            throw new BadCredentialsException("invalid credentials");
        }

    }


}
