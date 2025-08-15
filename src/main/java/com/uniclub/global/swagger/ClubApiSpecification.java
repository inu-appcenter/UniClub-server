package com.uniclub.global.swagger;


import com.uniclub.domain.club.dto.*;
import com.uniclub.global.exception.ErrorResponse;
import com.uniclub.global.security.UserDetailsImpl;

import java.util.List;
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

    @Operation(summary = "동아리 조회", description = "전체, 카테고리별, 정렬순 동아리 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
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
            @ApiResponse(responseCode = "400", description = "유효하지 않은 정렬 기준",
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
            @ApiResponse(responseCode = "404", description = "카테고리 찾기 실패",
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

    @Operation(summary = "관심동아리 등록/취소", description = "관심 동아리 토글 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 처리 성공",
                    content = @Content(
                            schema = @Schema(implementation = ToggleFavoriteResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "message": "관심 동아리 등록 완료"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "동아리 찾기 실패",
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
    ResponseEntity<ToggleFavoriteResponseDto> toggleFavorite(
            @PathVariable Long clubId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "동아리 등록", description = "신규 동아리 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾기 실패",
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
            ),
            @ApiResponse(responseCode = "409", description = "동아리명 중복",
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

    @Operation(summary = "동아리 홍보페이지 작성 및 수정", description = "동아리 홍보페이지 작성/수정")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "처리 성공"),
            @ApiResponse(responseCode = "404", description = "동아리 찾기 실패",
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
            @ApiResponse(responseCode = "403", description = "권한 없음",
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
            ),
            @ApiResponse(responseCode = "404", description = "멤버십 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "MEMBERSHIP_NOT_FOUND",
                      "message": "해당 동아리 권한을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상태 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "STATUS_NOT_FOUND",
                      "message": "해당 상태를 찾을 수 없습니다."
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

    @Operation(summary = "동아리 홍보페이지 조회", description = "동아리 홍보페이지 조회")
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
            @ApiResponse(responseCode = "404", description = "동아리 찾기 실패",
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
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long clubId
    );

    @Operation(summary = "동아리 미디어 업로드", description = "동아리 홍보용 미디어 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "404", description = "동아리 찾기 실패",
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
            @ApiResponse(responseCode = "403", description = "권한 없음",
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
            ),
            @ApiResponse(responseCode = "404", description = "멤버십 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "MEMBERSHIP_NOT_FOUND",
                      "message": "해당 동아리 권한을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "미디어 타입 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "MEDIA_TYPE_NOT_FOUND",
                      "message": "해당 미디어 유형을 찾을 수 없습니다"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "중복 미디어 타입",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "DUPLICATE_MEDIA_TYPE",
                      "message": "해당 타입의 이미지는 동아리만 하나만 등록 가능합니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<Void> uploadClubMedia(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long clubId,
            @RequestBody List<ClubMediaUploadRequestDto> clubMediaUploadRequestDtoList
    );

    @Operation(summary = "동아리 삭제", description = "동아리 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "동아리 찾기 실패",
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