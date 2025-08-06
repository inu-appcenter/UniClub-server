package com.uniclub.global.swagger;


import com.uniclub.domain.club.dto.*;
import com.uniclub.global.exception.ErrorResponse;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "동아리 API", description = "동아리 조회·등록·관리 기능")
public interface ClubApiSpecification {

    @Operation(summary = "동아리 조회", description = "전체, 카테고리별, 정렬순 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = PageClubResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "content": [
                        {
                          "id": 1,
                          "name": "앱센터",
                          "info": "앱 개발 동아리",
                          "status": "ACTIVE",
                          "favorite": true
                        },
                        {
                          "id": 2,
                          "name": "디자인소모임",
                          "info": "UI/UX 디자인",
                          "status": "CLOSED",
                          "favorite": false
                        }
                      ],
                      "hasNext": true
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 정렬 기준입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "INVALID_SORT_CONDITION",
                      "message": "유효하지 않은 정렬 기준입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 카테고리를 찾을 수 없습니다.",
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
    ResponseEntity<PageClubResponseDto<ClubResponseDto>> getClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String cursorName,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "관심동아리 등록/취소", description = "관심 동아리 추가 혹은 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 처리 성공",
                    content = @Content(
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject("""
                    "관심 동아리 등록 완료"
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 동아리를 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CLUB_NOT_FOUND",
                      "message": "해당 동아리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<String> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "동아리 등록", description = "새로운 동아리를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 동아리입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 409,
                      "name": "DUPLICATE_CLUB_NAME",
                      "message": "이미 존재하는 동아리입니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> createClub(
            @Valid @RequestBody ClubCreateRequestDto clubCreateRequestDto
    );

    @Operation(summary = "동아리 홍보페이지 작성 및 수정", description = "홍보글을 생성하거나 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "처리 성공"),
            @ApiResponse(responseCode = "404", description = "해당 동아리를 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CLUB_NOT_FOUND",
                      "message": "해당 동아리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "사용 권한이 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 403,
                      "name": "INSUFFICIENT_PERMISSION",
                      "message": "사용 권한이 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> promotionRegister(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long clubId,
            @RequestBody ClubPromotionRegisterRequestDto clubPromotionRegisterRequestDto
    );

    @Operation(summary = "동아리 홍보페이지 조회", description = "특정 동아리의 홍보글을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ClubPromotionResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "name": "앱센터",
                      "status": "ACTIVE",
                      "startTime": "2025-08-10T09:00:00",
                      "endTime": "2025-08-20T18:00:00",
                      "description": "신입 회원 모집",
                      "notice": "온라인 설명회 8/15",
                      "location": "4호관 107호",
                      "presidentName": "홍길동",
                      "presidentPhone": "010-1234-5678",
                      "youtubeLink": "https://youtu.be/example",
                      "instagramLink": "https://instagram.com/appcenter",
                      "profileImage": "https://cdn.example.com/profile.jpg",
                      "backgroundImage": "https://cdn.example.com/bg.jpg",
                      "mediaLinks": [
                        "https://cdn.example.com/media1.png",
                        "https://cdn.example.com/media2.mp4"
                      ]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 동아리를 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CLUB_NOT_FOUND",
                      "message": "해당 동아리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<ClubPromotionResponseDto> getClubPromotion(
            @PathVariable Long clubId
    );

    @Operation(summary = "동아리 삭제", description = "특정 동아리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 동아리를 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "CLUB_NOT_FOUND",
                      "message": "해당 동아리를 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteClub(
            @PathVariable Long clubId
    );
}
