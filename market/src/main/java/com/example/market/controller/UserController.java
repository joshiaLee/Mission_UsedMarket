package com.example.market.controller;

import com.example.market.dto.ItemDto;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.dto.UserDto;
import com.example.market.dto.CustomUserDetails;
import com.example.market.repo.ImageRepository;
import com.example.market.repo.ItemRepository;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final ImageRepository imageRepository;

    private final ItemRepository itemRepository;
    @GetMapping("/home")
    public String home(){
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info(authFacade.getAuth().getName());
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login-form";
    }

    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication
    ){
        log.info(authentication.getName());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getUsername());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getEmail());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPhone());
        return "my-profile";
    }

    @GetMapping("/register")
    public String signUpForm(){
        return "register-form";
    }

    // 회원가입
    @PostMapping("/register")
    public String signUpRequest(
            @RequestBody
            UserDto userDto
    ){

        if(!userDto.getPassword().equals(userDto.getPasswordCheck())) {
            log.info("비밀번호와 비밀번호 확인이 다릅니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity userEntity = UserEntity.fromUserDto(userDto);
        userEntity.setAuthorities("ROLE_UNACTIVATED");

        service.createUser(userEntity);

        return "redirect:/users/login";
    }

    // 추가정보 기입
    @PostMapping("/add-info")
    public String signUpAdd(
            @RequestBody
            UserDto userDto,
            Authentication authentication
    )  {
        boolean isValid = StringUtils.isNotBlank(userDto.getNickname()) &&
                StringUtils.isNotBlank(userDto.getName()) &&
                userDto.getAge() != null && // Assuming age is an Integer
                StringUtils.isNotBlank(userDto.getEmail()) &&
                StringUtils.isNotBlank(userDto.getPhone());

        if (!isValid) {
            log.info("데이터 오류");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모든 데이터를 입력해야 합니다.");
        }

        // 접속자 정보
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 엔티티 조회
        String username = userDetails.getUsername();
        UserEntity userEntity = service.searchByUsername(username);


        userEntity.setNickname(userDto.getNickname());
        userEntity.setName(userDto.getName());
        userEntity.setAge(userDto.getAge());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());


        // 승급(비활성 -> 일반)
        userEntity.setAuthorities("ROLE_USER");



        service.updateUser(userEntity);

        return "redirect:/users/home";
    }
    
    @PostMapping("/add-image") String addImage(
            MultipartFile file,
            Authentication authentication
    ) throws IOException {
        if (file == null || file.isEmpty())
            throw new IOException("이미지 파일이 없습니다.");


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserEntity userEntity = service.searchByUsername(username);

        // ImageEntity 저장
        ImageEntity imageEntity = AssociatedImage(userEntity, file);
        imageRepository.save(imageEntity);

        return "redirect:/users/home";
    }

    @PostMapping("/apply")
    public String apply(
        @RequestBody
        UserDto userDto,
        Authentication authentication
    ){
       if(!StringUtils.isNotBlank(userDto.getRegistrationNumber())) {
           log.info("데이터 오류");
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 사업자 번호");
       }

        // 접속자가 누구인지
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserEntity userEntity = service.searchByUsername(username);
        
        userEntity.setRegistrationNumber(userDto.getRegistrationNumber());
        userEntity.setStatus("Proceeding");

        service.updateUser(userEntity);

        return "redirect:/users/home";
    }

    @PostMapping("/add-item")
    public String addItem(
            MultipartFile[] files,
            @ModelAttribute
            ItemDto itemDto,
            Authentication authentication
    ) throws IOException {
        Item item = Item.fromDto(itemDto);
        log.info(itemDto.toString());

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserEntity userEntity = service.searchByUsername(username);


        item.setUserEntity(userEntity);
        item.setStatus("OnSale");

        itemRepository.save(item);

        if (files != null && files.length > 0){
            for(MultipartFile file: files) {
                ImageEntity imageEntity = AssociatedImage(item, file);
                imageRepository.save(imageEntity);
            }
        }

        return "redirect:/users/home";
    }

    private static String extractPath() {

        Class<?> clazz = UserController.class;
        // 정규식 패턴 설정

        // 클래스의 위치를 나타내는 URL을 가져옴
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        String fullPath = location.getPath();

        String pattern = "(/home.+?)/build";
        Pattern regex = Pattern.compile(pattern);

        // 매칭 수행
        Matcher matcher = regex.matcher(fullPath);
        if (matcher.find()) {
            // 매칭된 부분 반환
            return matcher.group(1) + "/src/main/java/com/example/images/";
        } else {
            // 매칭되지 않은 경우 예외처리 또는 기본값 반환 가능
            return "";
        }
    }

    private static ImageEntity AssociatedImage(Object entity, MultipartFile file) throws IOException {
        String projectPath = extractPath();

        // 보안상 UUID 사용
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + file.getOriginalFilename();

        // 파일 객체 생성(절대경로, 파일이름)
        File saveFile = new File(projectPath, fileName);

        // 절대경로에 파일 저장
        file.transferTo(saveFile);

        // 이미지 엔티티 반환
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.associateImageWithEntity(entity,fileName,projectPath + fileName);
        return imageEntity;


    }

}
