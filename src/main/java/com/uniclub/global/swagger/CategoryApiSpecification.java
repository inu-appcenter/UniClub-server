package com.uniclub.global.swagger;

import com.uniclub.domain.category.dto.CategoryRequestDto;
import com.uniclub.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "카테고리 API", description = "카테고리 생성·삭제 기능")
public interface CategoryApiSpecification {

    @Operation(summary = "카테고리 생성", description = "신규 카테고리 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(
                    responseCode = "409",
                    description = "카테고리명 중복",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 409,
                      "name": "DUPLICATE_CATEGORY_NAME",
                      "message": "이미 존재하는 카테고리입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Long> createCategory(
            @Valid @RequestBody CategoryRequestDto categoryRequestDto
    );

    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CATEGORY_NOT_FOUND",
                      "message": "해당 카테고리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId
    );
}