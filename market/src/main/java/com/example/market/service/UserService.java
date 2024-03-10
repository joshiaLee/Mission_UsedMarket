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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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




    // create(From Dto) 용
    @Transactional
    public UserEntity createUserFromDto(UserDto userDto) {
        if(this.userExists(userDto.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UserEntity userEntity = UserEntity.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .authorities("ROLE_UNACTIVATED")
                .build();

        return userRepository.save(userEntity);
    }

    // 테스트 데이터 용
    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        if(this.userExists(userEntity.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return userRepository.save(userEntity);
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }



    // update


    @Transactional
    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }


    // 사업자 승인
    @Transactional
    public UserDto approveBusinessApplication(Long userId) {
        UserEntity userEntity = searchById(userId);
        userEntity.setAuthorities("ROLE_CEO");
        userEntity.setStatus(Status.ADMITTED);
        return UserDto.fromEntity(updateUser(userEntity));
    }

    // 사업자 거절
    @Transactional
    public UserDto rejectBusinessApplication(Long userId) {
        UserEntity userEntity = searchById(userId);
        userEntity.setStatus(Status.REJECTED);
        return UserDto.fromEntity(updateUser(userEntity));
    }


    // delete
    @Transactional
    public void deleteUser(String username) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);

    }

    // changePassword
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }


    public UserEntity addInfo(UserEntity userEntity, UserDto userDto) {
        userEntity.setNickname(userDto.getNickname());
        userEntity.setName(userDto.getName());
        userEntity.setAge(userDto.getAge());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());

        // 승급(비활성 -> 일반)
        userEntity.setAuthorities("ROLE_USER");

        return updateUser(userEntity);
    }

    public UserDto registerCEO(UserEntity userEntity, String registrationNumber) {
        userEntity.setRegistrationNumber(registrationNumber);
        userEntity.setStatus(Status.PROCEEDING);
        return UserDto.fromEntity(updateUser(userEntity));

    }
}
