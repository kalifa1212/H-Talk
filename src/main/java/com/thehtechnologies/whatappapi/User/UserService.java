package com.thehtechnologies.whatappapi.User;

import com.thehtechnologies.whatappapi.File.FileService;
import com.thehtechnologies.whatappapi.Security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.database.DatabaseExecutionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileService fileService;

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
        user.setLastSeen( LocalDateTime.now());

         return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse uploadPhoto(Authentication authentication, MultipartFile file) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String  CurrentUserId = userPrincipal.getId();
        //final String filePath = fileService.saveFile(file, CurrentUserId);
        log.info("currentUserId {}",CurrentUserId);
        fileService.MirasaveFile(CurrentUserId+"."+StringUtils.cleanPath(file.getOriginalFilename()).substring(StringUtils.cleanPath(file.getOriginalFilename()).lastIndexOf(".")+1),file);
        User user = userRepository.findByPublicId(CurrentUserId).orElseThrow();
        user.setPhotoUrl("/uploads/profile/image/"+CurrentUserId+"."+StringUtils.cleanPath(file.getOriginalFilename()).substring(StringUtils.cleanPath(file.getOriginalFilename()).lastIndexOf(".")+1));
        log.info("photo upload {}",user.getPhotoUrl());
        return  userMapper.toUserResponse(userRepository.save(user));
    }

    public void lastSeen(String id) {
        User user = userRepository.findByPublicId(id).orElseThrow();
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    public Resource getFile(String id) {
        User user = userRepository.findByPublicId(id).orElseThrow();
     //   log.info("user load {}",user);
        return getFile(user);
    }
    public Resource getFile(User user){
        //log.error("test 2 {}",utilisateurDto);
        Path path = Paths.get(System.getProperty("user.dir"),user.getPhotoUrl());
        log.info("trying to get ressource");
        Resource resource = null;
        try {
            resource = (Resource) new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return  resource;
    }
}
