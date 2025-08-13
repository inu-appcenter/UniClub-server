package com.uniclub.global.swagger;

import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "메인페이지 API", description = "메인페이지 관련 기능")
public interface MainApiSpecification {

    @Operation(summary = "메인페이지 동아리 목록 조회", description = "메인페이지 동아리 목록(최대 6개) 및 즐겨찾기 여부 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메인페이지 동아리 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MainPageClubResponseDto.class))
            )
    })
    ResponseEntity<List<MainPageClubResponseDto>> getMainPageClubs(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}