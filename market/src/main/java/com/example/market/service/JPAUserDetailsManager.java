package com.example.market.service;

import com.example.market.entity.CustomUserDetails;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JPAUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser
                = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);

        UserEntity userEntity = optionalUser.get();
        return CustomUserDetails.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();



//        UserDetails build = User
//                .withUsername(username)
//                .password(optionalUser.get().getPassword())
//                .build();
//        return build;
    }

    @Override
    public void createUser(UserDetails user) {
        if(userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try{
            CustomUserDetails customUserDetails = (CustomUserDetails) user;
            UserEntity newUser = UserEntity.builder()
                    .username(customUserDetails.getUsername())
                    .password(customUserDetails.getPassword())
                    .email(customUserDetails.getEmail())
                    .phone(customUserDetails.getPhone())
                    .build();
            userRepository.save(newUser);
        } catch (ClassCastException e){
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        UserEntity userEntity = UserEntity.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .build();
//        userRepository.save(userEntity);
    }
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }



    @Override
    public void updateUser(UserDetails user) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteUser(String username) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }


}
