package com.example.market.controller;

import com.example.market.dto.*;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.PurchasePropose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Category;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.facade.ImageFacade;
import com.example.market.repo.ImageRepository;
import com.example.market.service.ItemService;
import com.example.market.service.PurchaseProposeService;
import com.example.market.service.ShopService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final ImageRepository imageRepository;
    private final ShopService shopService;
    private final ItemService itemService;
    private final PurchaseProposeService proposeService;

    // 홈 화면 모든 이용자 사용가능
    @GetMapping("/home")
    public String home(){
        return authFacade.getAuth().getName();
    }


    // 내 프로필 화면
    @GetMapping("/my-profile")
    public UserDto myProfile(
    ){
        String username = authFacade.getAuth().getName();
        return UserDto.fromEntity(service.searchByUsername(username));
    }


    // 회원가입
    @PostMapping("/register")
    public UserDto signUpRequest(
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



        return UserDto.fromEntity(service.createUser(userEntity));
    }

    // 추가정보 기입
    @PostMapping("/add-info")
    public UserDto signUpAdd(
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

        return UserDto.fromEntity(service.updateUser(userEntity));
    }

    // 프로필 사진 추가
    @PostMapping("/add-image") ImageDto addImage(
            MultipartFile file,
            Authentication authentication
    ) throws IOException {
        if (file == null || file.isEmpty())
            throw new IOException("이미지 파일이 없습니다.");


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserEntity userEntity = service.searchByUsername(username);

        // ImageEntity 저장
        ImageEntity imageEntity = ImageFacade.AssociatedImage(userEntity, file);


        return ImageDto.fromEntity(imageRepository.save(imageEntity));
    }

    // 사엽자 등록 신청
    @PostMapping("/apply")
    public UserDto apply(
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
        userEntity.setStatus(Status.PROCEEDING);

        return UserDto.fromEntity(service.updateUser(userEntity));
    }

    // 모든 쇼핑몰 조회, 최근 거래한 순서
    @GetMapping("/shops-list")
    public List<ShopDto> getAllShops(){
        return shopService.searchAll();
    }

    // 이름, 카테고리로 쇼핑몰 검색 (이때 오픈된 쇼핑몰이여야 함)
    // /users/shops-search?name=철수&category=Electronics
    @GetMapping("/shops-search")
    public List<ShopDto> searchShops(
            @RequestParam("name")
            String name,
            @RequestParam("category")
            Category category
    ){
        return shopService.searchAllByNameAndCategory(name, category, Status.OPEN);
    }

    // 이름, 가격범위로 아이템 검색 (이때 아이템 이미지와, 아이템이 속한 쇼핑몰도 같이 조회할수 있어야함)
    // 이떄 오픈된 쇼핑몰이여야함
    // /users/items-search?name=컴퓨터&above=10000&under=150000
    @GetMapping("/items-search")
    public List<ItemsResponseDto> searchItems(
            @RequestParam("name")
            String name,
            @RequestParam("above")
            Integer above,
            @RequestParam("under")
            Integer under
    ){
        return itemService.searchAllByNameAndPrice(name, above, under)
                .stream()
                .map(item -> {
                    List<ImageDto> imagesDto = imageRepository.findAllByItemId(item.getId())
                            .stream()
                            .map(ImageDto::fromEntity)
                            .collect(Collectors.toList());

                    return new ItemsResponseDto(
                            ItemDto.fromEntity(item),
                            imagesDto,
                            ShopDto.fromEntity(item.getShop())
                    );
                })
                .collect(Collectors.toList());
    }

    // 유저가 쇼핑몰에 구매제안
    @PostMapping("/purchase-item")
    public PurchaseProposeDto purchaseItem(
            @RequestBody
            PurchaseProposeDto proposeDto
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        PurchasePropose newPropose = PurchasePropose.builder()
                .itemId(proposeDto.getItemId())
                .shopId(proposeDto.getShopId())
                .userId(userEntity.getId())
                .quantity(proposeDto.getQuantity())
                .status(Status.PROCEEDING)
                .build();

        return PurchaseProposeDto.fromEntity(proposeService.join(newPropose));

    }








}
