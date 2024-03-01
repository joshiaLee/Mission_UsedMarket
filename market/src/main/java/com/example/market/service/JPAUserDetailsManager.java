package com.example.market.service;

import com.example.market.entity.CustomUserDetails;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                .authorities(userEntity.getAuthorities())
                .build();

    }

    @Override
    public void createUser(UserDetails user) {
        if(this.userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try{
            CustomUserDetails customUser = (CustomUserDetails) user;
            UserEntity newUser = UserEntity.builder()
                    .username(customUser.getUsername())
                    .password(customUser.getPassword())
                    .nickname(customUser.getNickname())
                    .name(customUser.getName())
                    .age(customUser.getAge())
                    .email(customUser.getEmail())
                    .phone(customUser.getPhone())
                    .registrationNumber(customUser.getRegistrationNumber())
                    .authorities(customUser.getRawAuthorities())
                    .build();
            userRepository.save(newUser);
        } catch (ClassCastException e){
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @Override
    public boolean userExists(String login) {
        return userRepository.existsByUsername(login);
    }



    @Override
    public void updateUser(UserDetails user) {
        UserEntity updateEntity = userRepository.findByUsername(user.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + user.getUsername()));

        CustomUserDetails customUser = (CustomUserDetails) user;

        // username 은 unique 이기 때문에 바꿀수 없다.
        updateEntity.setPassword(customUser.getPassword());
        updateEntity.setNickname(customUser.getNickname());
        updateEntity.setName(customUser.getName());
        updateEntity.setAge(customUser.getAge());
        updateEntity.setEmail(customUser.getEmail());
        updateEntity.setPhone(customUser.getPhone());
        updateEntity.setRegistrationNumber(customUser.getRegistrationNumber());
        updateEntity.setAuthorities(customUser.getRawAuthorities());

        userRepository.save(updateEntity);
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
