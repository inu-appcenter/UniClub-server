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
                                        "favorite": true
                                      },
                                      {
                                        "id": 5,
                                        "name": "앱디자인소모임",
                                        "info": "UI/UX 디자인",
                                        "status": "CLOSED",
                                        "favorite": false
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