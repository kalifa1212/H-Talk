package com.thehtechnologies.whatappapi.User;

import com.thehtechnologies.whatappapi.Security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> finAllUsersExceptSelf(Authentication connectedUser) {
        UserPrincipal userPrincipal = (UserPrincipal) connectedUser.getPrincipal();
        String  CurrentUserId = userPrincipal.getId();
        return userRepository.findAllUsersExceptSelf(CurrentUserId)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
    public UserResponse create(User user) {
        Optional<User> test = userRepository.findByEmail(user.getEmail());
        if(!test.isEmpty()){
            throw new EntityNotFoundException("l'utilisateur existe deja");
        }
        //TODO testing

        String passwd=user.getPassword();
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(passwd));

         return userMapper.toUserResponse(userRepository.save(user));
    }

}
