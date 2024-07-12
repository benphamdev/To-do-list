package com.mynotes.controllers;

import com.mynotes.dto.requests.LoginRequest;
import com.mynotes.dto.responses.AuthResponse;
import com.mynotes.services.CloudinaryService;
import com.mynotes.services.JwtTokenService;
import com.mynotes.services.auth.MyCustomUserDetailService;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    public String uploadDirectory =
            System.getProperty("user.dir") + "/my-notes/src/main/resources/static/profile-pictures/";
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private MyCustomUserDetailService myCustomUserDetailService;
    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity signIn(
            @RequestBody LoginRequest loginRequest
    ) {
        // SET AUTHENTICATION:
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()
                        ));

        // SET USER OBJECT:
        MyCustomUserDetails userDetails =
                (MyCustomUserDetails) myCustomUserDetailService.loadUserByUsername(loginRequest.getEmail());
        // SET SECURITY CONTEXT:
        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);

        // GENERATE TOKEN:
        String token = jwtTokenService.generateToken(userDetails);

        // SET RESPONSE:
        AuthResponse response = new AuthResponse(token, userDetails);

        // RETURN RESPONSE:
        return new ResponseEntity(response, HttpStatus.ACCEPTED);
    }
    // END OF USER SIGN IN POST METHOD.

    @PostMapping("/register")
    public ResponseEntity signUp(
            @RequestParam("first_name") String firstName,
            @RequestParam("last_name") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        // TODO: VALIDATE IF EMAIL IS NOT ALREADY TAKEN.
        // HASH PASSWORD:
        String hashed_password = passwordEncoder.encode(password);

        // STORE USER.
        int result = userService.signUpUser(firstName, lastName, email, hashed_password);
        // END OF STORE USER.

        // CHECK FOR RESULT SET:
        if (result != 1) {
            return new ResponseEntity("Something went wrong", HttpStatus.BAD_REQUEST);
        }
        // END OF CHECK FOR RESULT SET.

        // RETURN SUCCESS RESPONSE:
        return new ResponseEntity("User Sign Up Successful!", HttpStatus.CREATED);
    }
    // END OF USER SIGN UP POST METHOD.

    @PostMapping("login/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("user_id") int userId,
            @RequestBody MultipartFile profilePicture
    ) throws IOException {

        BufferedImage bi = ImageIO.read(profilePicture.getInputStream());
        if (bi == null) {
            return new ResponseEntity<>("Invalid Image", HttpStatus.BAD_REQUEST);
        }
        Map result = cloudinaryService.upload(profilePicture);

        userService.updateUserProfilePicture(userId, (String) result.get("secure_url"));

        return new ResponseEntity<>("Profile Picture Updated", HttpStatus.ACCEPTED);
    }

//    @GetMapping("/login/profile")
//    public ResponseEntity<?> getProfile(
//            @RequestParam("user_id") int userId
//    ) throws IOException {
//        User user = userService.loadUserById(userId);
//
//        Path path = Paths.get(uploadDirectory, user.getProfile_picture());
//        Resource resource = new FileSystemResource(path.toFile());
//        String contentType = Files.probeContentType(path);
//        // get url of the profile picture
//        String url = "http://localhost:8008/api/v1/auth/login/profile?user_id=" + userId;
//
//        return ResponseEntity.ok()
//                             .contentType(org.springframework.http.MediaType.parseMediaType(
//                                     contentType))
//                             .body(resource);
//    }
}
// END OF AUTH REST CONTROLLER CLASS.
