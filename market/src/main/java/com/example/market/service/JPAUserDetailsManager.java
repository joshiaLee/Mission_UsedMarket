package com.example.market.service;

import com.example.market.dto.UserDto;
import com.example.market.dto.CustomUserDetails;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JPAUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;

    public UserEntity searchById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username));

        return CustomUserDetails.fromUserEntity(userEntity);

    }

    @Override
    public void createUser(UserDetails user) {
        if(this.userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try{
            UserEntity newUser = UserEntity.fromCustomUserDetails((CustomUserDetails) user);
            userRepository.save(newUser);
        } catch (ClassCastException e){
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }



    @Override
    public void updateUser(UserDetails user) {
        UserEntity updateEntity = userRepository.findByUsername(user.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + user.getUsername()));

        CustomUserDetails customUser = (CustomUserDetails) user;

        UserEntity.setUserEntity(updateEntity, customUser);

        userRepository.save(updateEntity);
    }

    public List<UserDto> userEntitySearchByAuthoritiesAndStatus(String authorities, String status){
        List<UserEntity> userEntityList = userRepository.findByAuthoritiesAndStatus(authorities, status);

        return userEntityList.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());

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
