package com.yeggi.moneymanager.repository;


import com.yeggi.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    //select * from tbl_categories where profile_id = ?
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from tbl_categories where id = ? and profile_id = ?
    Optional<CategoryEntity> findByIdAndProfileId(Long categoryId, Long profileId);

    //select * from tbl_categories where type = ? and profile_id = ?
     List<CategoryEntity>findByTypeAndProfileId(String type, Long profileId);

     //select exists from tbl_categories where name = ? and profile_id = ?
    // Used to check for duplicate category names within the same profile
     Boolean existsByNameAndProfileId(String name, Long profileId);




}
