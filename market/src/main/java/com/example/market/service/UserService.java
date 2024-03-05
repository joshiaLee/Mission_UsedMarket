package com.example.market.service;

import com.example.market.dto.CustomUserDetails;
import com.example.market.dto.UserDto;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username));

        return CustomUserDetails.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity.getAuthorities())
                .build();

    }

    public UserEntity searchByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );
    }
    public UserEntity searchById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<UserDto> userEntitySearchByAuthoritiesAndStatus(String authorities, Status status){
        List<UserEntity> userEntityList = userRepository.findByAuthoritiesAndStatus(authorities, status);

        return userEntityList.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());

    }




    // create(entity) 용
    public UserEntity createUser(UserEntity user) {
        if(this.userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return userRepository.save(user);
    }
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }



    // update


    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }


    // delete
    public void deleteUser(String username) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);

    }

    // changePassword
    public void changePassword(String oldPassword, String newPassword) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }


}
