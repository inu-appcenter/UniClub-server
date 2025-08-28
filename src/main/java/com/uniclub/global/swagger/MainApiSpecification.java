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
                                        "mediaLink": "https://s3.amazonaws.com/bucket/presigned-url-for-main-banner-1",
                                        "mediaType": "MAIN_PAGE"
                                      },
                                      {
                                        "mediaLink": "https://s3.amazonaws.com/bucket/presigned-url-for-main-banner-2",
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
                                        "name": "앱센터",
                                        "imageUrl": "https://s3.amazonaws.com/bucket/presigned-url-for-club-main-image",
                                        "favorite": true
                                      },
                                      {
                                        "name": "디자인소모임",
                                        "imageUrl": "",
                                        "favorite": false
                                      },
                                      {
                                        "name": "농구동아리",
                                        "imageUrl": "https://s3.amazonaws.com/bucket/presigned-url-for-basketball-club",
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