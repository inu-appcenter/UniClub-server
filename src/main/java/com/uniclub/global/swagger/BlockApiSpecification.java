package com.uniclub.global.swagger;

import com.uniclub.global.exception.ErrorResponse;
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
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Block API", description = "사용자 차단 기능")
public interface BlockApiSpecification {

    @Operation(
            summary = "질문 작성자 차단",
            description = """
                    질문 ID를 기반으로 해당 질문의 작성자를 차단합니다.

                    - 익명 질문이더라도 서버에서 작성자를 식별하여 차단하므로, 클라이언트는 userId를 알 필요가 없습니다.
                    - 차단은 양방향으로 처리됩니다. A가 B를 차단하면, A의 피드에서 B의 글이 숨겨지고 B의 피드에서도 A의 글이 숨겨집니다.
                    - 이미 차단한 사용자를 다시 차단하려 하면 409를 반환합니다.
                    - 자기 자신이 작성한 질문으로 차단을 시도하면 400을 반환합니다.
                    - 탈퇴한 사용자의 질문으로 차단을 시도하면 404를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "차단 성공"),
            @ApiResponse(responseCode = "400", description = "자기 자신을 차단 시도",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "CANNOT_BLOCK_SELF",
                      "message": "자기 자신을 차단할 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "질문 또는 작성자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "이미 차단한 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 409,
                      "name": "ALREADY_BLOCKED",
                      "message": "이미 차단한 사용자입니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<Void> blockByQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    );

    @Operation(
            summary = "답변 작성자 차단",
            description = """
                    답변 ID를 기반으로 해당 답변의 작성자를 차단합니다.

                    - 익명 답변이더라도 서버에서 작성자를 식별하여 차단하므로, 클라이언트는 userId를 알 필요가 없습니다.
                    - 차단은 양방향으로 처리됩니다. A가 B를 차단하면, A의 피드에서 B의 글이 숨겨지고 B의 피드에서도 A의 글이 숨겨집니다.
                    - 이미 차단한 사용자를 다시 차단하려 하면 409를 반환합니다.
                    - 자기 자신이 작성한 답변으로 차단을 시도하면 400을 반환합니다.
                    - 탈퇴한 사용자의 답변으로 차단을 시도하면 404를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "차단 성공"),
            @ApiResponse(responseCode = "400", description = "자기 자신을 차단 시도",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "CANNOT_BLOCK_SELF",
                      "message": "자기 자신을 차단할 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "답변 또는 작성자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "ANSWER_NOT_FOUND",
                      "message": "답변을 찾을 수 없습니다."
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "이미 차단한 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 409,
                      "name": "ALREADY_BLOCKED",
                      "message": "이미 차단한 사용자입니다."
                    }
                    """)
                    )
            )
    })
    ResponseEntity<Void> blockByAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long answerId
    );
}
