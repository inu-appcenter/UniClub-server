package com.uniclub.domain.club.dto;

import com.uniclub.domain.category.entity.Category;
import com.uniclub.domain.category.entity.CategoryType;
import com.uniclub.domain.club.entity.Club;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.uniclub.domain.category.entity.CategoryType.from;

@Schema(description = "동아리 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class ClubCreateRequestDto {

    @Schema(description = "동아리 이름", example = "앱센터")
    @NotBlank(message = "동아리 이름을 입력해주세요.")
    private String name;

    @Schema(description = "동아리 카테고리", example = "학술")
    @NotBlank(message = "카테고리를 선택해주세요.")
    private String category;


    //저장을 위해 Club Entity로 변환
    public Club toClubEntity(Category category) {
        return Club.builder()
                .name(name)
                .category(category)
                .build();
    }
}
