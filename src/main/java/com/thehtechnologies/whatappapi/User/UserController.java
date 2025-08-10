package com.thehtechnologies.whatappapi.User;

import com.thehtechnologies.whatappapi.Security.JwtUtil;
import com.thehtechnologies.whatappapi.Security.MyUserDetailsService;
import com.thehtechnologies.whatappapi.Security.UserPrincipal;
import com.thehtechnologies.whatappapi.User.auth.AuthenticationRequest;
import com.thehtechnologies.whatappapi.User.auth.AuthenticationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(path = "/save")
    public ResponseEntity<UserResponse> save(@RequestBody User user){
        return ResponseEntity.ok(userService.create(user));
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
