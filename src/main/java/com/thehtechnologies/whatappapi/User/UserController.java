package com.thehtechnologies.whatappapi.User;

import com.thehtechnologies.whatappapi.Security.JwtUtil;
import com.thehtechnologies.whatappapi.Security.MyUserDetailsService;
import com.thehtechnologies.whatappapi.Security.UserPrincipal;
import com.thehtechnologies.whatappapi.User.auth.AuthenticationRequest;
import com.thehtechnologies.whatappapi.User.auth.AuthenticationResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@CrossOrigin(origins ="*")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping(path = "/save")
    public ResponseEntity<UserResponse> save(@RequestBody User user){
        return ResponseEntity.ok(userService.create(user));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(path = "/image",consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> upload(Authentication authentication,
                                               @Parameter()
                                               @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.uploadPhoto(authentication,file));
    }

    @GetMapping(value = "image/display/{id}", produces= MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity getPhoto(@PathVariable( "id") String id){
      //  log.info("inside display image");
        Resource resource=userService.getFile(id);;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\" profile \"")
                .body(resource);
    }


    //TODO implement badcredential exception handler
    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponse> authentification(@RequestBody AuthenticationRequest authenticationRequestP) {

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestP.getLogin());

        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(authenticationRequestP.getLogin(), authenticationRequestP.getPassword());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        if (authenticationResponse.isAuthenticated()) {
            // 📌 Enregistrer l'utilisateur dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
            UserPrincipal userPrincipal= (UserPrincipal) authenticationResponse.getPrincipal();
            // 🔑 Générer le token JWT
            final String jwt = jwtUtil.generateToken(userDetails);
            final String email = jwtUtil.extractUserName(jwt);
            final String id=userPrincipal.getId();
            userService.lastSeen(id);
            return ResponseEntity.ok(AuthenticationResponse.builder().accessToken(jwt).email(email).id(id).build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.finAllUsersExceptSelf(authentication));
    }
}
