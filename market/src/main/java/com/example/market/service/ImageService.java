package com.example.market.service;

import com.example.market.dto.ImageDto;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.UserEntity;
import com.example.market.facade.ImageFacade;
import com.example.market.repo.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;

    // 이미지 등록
    @Transactional
    public void addImages(MultipartFile[] files, Item savedItem) throws IOException {
        if (files != null && files.length > 0){
            for(MultipartFile file: files) {
                ImageEntity imageEntity = ImageFacade.AssociatedImage(savedItem, file);
                imageRepository.save(imageEntity);
            }
        }
    }

    public List<ImageDto> searchAllImagesByItemId(Long id){
        return imageRepository
                .findAllByItemId(id)
                .stream()
                .map(ImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long id){
        imageRepository.deleteById(id);

    }

    public ImageEntity searchById(Long id){
        return imageRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 이미지가 없습니다.")
        );
    }

    public ImageDto addImage(UserEntity userEntity, MultipartFile file) throws IOException {
        ImageEntity imageEntity = ImageFacade.AssociatedImage(userEntity, file);
        return ImageDto.fromEntity(imageRepository.save(imageEntity));
    }
}
