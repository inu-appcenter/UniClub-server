package com.uniclub.global.swagger;

import com.uniclub.domain.main.dto.MainMediaUploadRequestDto;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.domain.main.dto.MainPageMediaResponseDto;
import com.uniclub.global.exception.ErrorResponse;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "메인페이지 API", description = "메인페이지 관련 기능")
public interface MainApiSpecification {

    @Operation(summary = "메인페이지 미디어 조회", description = "메인페이지에 표시될 미디어 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메인페이지 미디어 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MainPageMediaResponseDto.class),
                            examples = @ExampleObject("""
                                    [
                                      {
                                        "mediaLink": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4",
                                        "mediaType": "MAIN_PAGE"
                                      },
                                      {
                                        "mediaLink": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4",
                                        "mediaType": "MAIN_PAGE"
                                      }
                                    ]
                                    """)
                    )
            )
    })
    ResponseEntity<List<MainPageMediaResponseDto>> getMainPageMedia();

    @Operation(summary = "메인페이지 동아리 목록 조회", description = "메인페이지 동아리 목록(최대 6개) 및 즐겨찾기 여부 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메인페이지 동아리 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MainPageClubResponseDto.class),
                            examples = @ExampleObject("""
                                    [
                                      {
                                        "clubId": 1,
                                        "name": "앱센터",
                                        "imageUrl": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4",
                                        "favorite": true
                                      },
                                      {
                                        "clubId": 2,
                                        "name": "디자인소모임",
                                        "imageUrl": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4",
                                        "favorite": false
                                      },
                                      {
                                        "clubId": 3,
                                        "name": "농구동아리",
                                        "imageUrl": "https://uniclubs3.s3.ap-northeast-2.amazonaws.com/uploads/2025-08-13/840d2146-c793-4ee6-83be-acb4c817c87e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250813T162429Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1200&X-Amz-Credential=AKIAYHJAM5L7YOWJHC4E%2F20250813%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=46a114c0ef5c5921b9ea480b5fd10a197a7dfcafb782e633487fec858de4",
                                        "favorite": true
                                      }
                                    ]
                                    """)
                    )
            )
    })
    ResponseEntity<List<MainPageClubResponseDto>> getMainPageClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "메인페이지 미디어 업로드", description = "메인페이지용 미디어 업로드")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "업로드 성공"
            )
    })
    ResponseEntity<Void> uploadClubMedia(
            @RequestBody List<MainMediaUploadRequestDto> mainMediaUploadRequestDtoList
    );
}