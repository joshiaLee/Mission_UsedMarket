package com.example.market.facade;

import com.example.market.controller.UserController;
import com.example.market.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageFacade {
    public static String extractPath() {

        Class<?> clazz = UserController.class;
        // 정규식 패턴 설정

        // 클래스의 위치를 나타내는 URL 을 가져옴
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

    public static ImageEntity AssociatedImage(Object entity, MultipartFile file) throws IOException {
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
