package com.uniclub.global.swagger;

import com.uniclub.domain.qna.dto.*;
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

@Tag(name = "QnA API", description = "질문·답변 관련 기능")
public interface QnaApiSpecification {

    @Operation(summary = "QnA 페이지 질문 검색", description = "키워드, 동아리별, 답변여부, 본인질문 등의 조건으로 질문 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(
                            schema = @Schema(implementation = PageQuestionResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "content": [
                        {
                          "questionId": 1,
                          "name": "홍길동",
                          "clubName": "앱센터",
                          "content": "동아리원 모집은 언제 진행하나요?",
                          "countAnswer": 3
                        },
                        {
                          "questionId": 2,
                          "name": "익명",
                          "clubName": "디자인소모임",
                          "content": "활동비는 얼마인가요?",
                          "countAnswer": 0
                        }
                      ],
                      "hasNext": true
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
    ResponseEntity<PageQuestionResponseDto<SearchQuestionResponseDto>> getSearchQuestions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long clubId,
            @RequestParam(defaultValue = "false") boolean answered,
            @RequestParam(defaultValue = "false") boolean onlyMyQuestions,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "특정 질문 조회", description = "질문 ID로 특정 질문과 답변 리스트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = QuestionResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "questionId": 1,
                      "name": "홍길동",
                      "userId": 123,
                      "content": "동아리원 모집은 언제 진행하나요?",
                      "anonymous": false,
                      "answered": true,
                      "updatedAt": "2025-08-25T10:30:00",
                      "answers": [
                        {
                          "answerId": 1,
                          "name": "김동아리",
                          "content": "매 학기 초에 진행합니다.",
                          "anonymous": false,
                          "deleted": false,
                          "updateTime": "2025-08-25T11:00:00",
                          "parentAnswerId": null,
                          "owner": false
                        }
                      ],
                      "owner": true,
                      "president": false
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "질문 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<QuestionResponseDto> getQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    );

    @Operation(summary = "질문 등록", description = "특정 동아리에 새로운 질문 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "질문 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = QuestionCreateResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "questionId": 123,
                      "message": "질문이 등록되었습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "INVALID_INPUT_VALUE",
                      "message": "잘못된 입력입니다."
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
    ResponseEntity<QuestionCreateResponseDto> createQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long clubId,
            @Valid @RequestBody QuestionCreateRequestDto questionCreateRequestDto
    );

    @Operation(summary = "질문 수정", description = "본인이 작성한 질문 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "답변이 채택된 질문 수정 시도",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "CANNOT_UPDATE_ANSWERED_QUESTION",
                      "message": "답변이 채택된 질문은 수정할 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "질문 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
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
            )
    })
    ResponseEntity<Void> updateQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequestDto questionUpdateRequestDto
    );

    @Operation(summary = "질문 삭제", description = "본인이 작성한 질문 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "질문 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
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
            )
    })
    ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    );

    @Operation(summary = "답변 등록", description = "질문에 답변 또는 답변에 대댓글 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = AnswerCreateResponseDto.class),
                            examples = @ExampleObject("""
                    {
                      "answerId": 456,
                      "message": "답변이 등록되었습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "INVALID_INPUT_VALUE",
                      "message": "잘못된 입력입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "질문 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상위 답변 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "ANSWER_NOT_FOUND",
                      "message": "답변을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ResponseEntity<AnswerCreateResponseDto> createAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId,
            @RequestParam Long parentsAnswerId,
            @Valid @RequestBody AnswerCreateRequestDto answerCreateRequestDto
    );

    @Operation(summary = "답변 삭제", description = "본인이 작성한 답변 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "답변 완료된 질문의 답변 삭제 시도",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 400,
                      "name": "CANNOT_DELETE_ANSWER_ANSWERED_QUESTION",
                      "message": "답변이 완료된 질문의 답변은 삭제할 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "답변 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "ANSWER_NOT_FOUND",
                      "message": "답변을 찾을 수 없습니다."
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
            )
    })
    ResponseEntity<Void> deleteAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long answerId
    );

    @Operation(summary = "질문 답변 완료 표시", description = "동아리 회장이 질문을 답변 완료로 표시")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 완료 표시 성공"),
            @ApiResponse(responseCode = "404", description = "질문 찾기 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject("""
                    {
                      "code": 404,
                      "name": "QUESTION_NOT_FOUND",
                      "message": "질문을 찾을 수 없습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 (회장이 아님)",
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
    ResponseEntity<Void> markQuestionAsAnswered(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    );
}