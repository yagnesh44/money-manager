package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.dto.CategoryDTO;
import com.yeggi.moneymanager.entity.CategoryEntity;
import com.yeggi.moneymanager.entity.ProfileEntity;
import com.yeggi.moneymanager.repository.CategoryRepository;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class  CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    //save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists for this profile.");
        }
        CategoryEntity newCategory = toEntity(categoryDTO,profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    //get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIconUrl(categoryDTO.getIconUrl());

        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }






    //Helper Methods

    // Method to convert CategoryDTO to CategoryEntity
    public CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .iconUrl(categoryDTO.getIconUrl())
                .profile(profileEntity)
                .build();
    }

    // Method to convert CategoryEntity to CategoryDTO
    public CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .type(categoryEntity.getType())
                .iconUrl(categoryEntity.getIconUrl())
                .createdAt(categoryEntity.getCreatedAt().toString())
                .updatedAt(categoryEntity.getUpdatedAt().toString())
                .profileId(categoryEntity.getProfile()!=null?categoryEntity.getProfile().getId():null)
                .build();

    }


}
