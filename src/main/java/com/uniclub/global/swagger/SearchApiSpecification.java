package com.uniclub.global.swagger;

import com.uniclub.domain.club.dto.ClubResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "검색 API", description = "키워드 검색 기능")
public interface SearchApiSpecification {
    @Operation(
            summary = "동아리 검색",
            description = "키워드로 동아리 검색"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 결과 반환",
                    content = @Content(
                            schema = @Schema(implementation = ClubResponseDto.class),
                            examples = @ExampleObject("""
                                    [
                                      {
                                        "id": 1,
                                        "name": "앱센터",
                                        "info": "앱 개발 동아리",
                                        "status": "ACTIVE",
                                        "favorite": true,
                                        "category": "LIBERAL_ACADEMIC",
                                        "clubProfileUrl": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4"
                                      },
                                      {
                                        "id": 5,
                                        "name": "앱디자인소모임",
                                        "info": "UI/UX 디자인",
                                        "status": "CLOSED",
                                        "favorite": false,
                                        "category": "CULTURE",
                                        "clubProfileUrl": ""
                                      }
                                    ]
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<List<ClubResponseDto>> search(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword);
}