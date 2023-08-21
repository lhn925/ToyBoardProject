package sky.board.domain.user.api;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.join.JoinEmailDuplicateDto;
import sky.board.domain.user.dto.join.JoinIdDuplicateDto;
import sky.board.domain.user.dto.join.JoinUserNameDuplicateDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.error.dto.Result;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/join/api")
public class JoinApiController {

    private final UserJoinService userJoinService;
    private final MessageSource ms;


    /**
     * id:joinApi_1
     * 유저아이디 중복체크
     *
     * @param userId
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/id")
    public ResponseEntity checkUserId(@Validated @ModelAttribute("userId") JoinIdDuplicateDto userId,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        try {
            userJoinService.checkId(userId.getUserId());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));

            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(userId), HttpStatus.OK);
    }


    /**
     * id:joinApi_2
     * 유저네임 중복체크
     *
     * @param userName
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/userName")
    public ResponseEntity checkUserName(@Validated @ModelAttribute("userName") JoinUserNameDuplicateDto userName,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            userJoinService.checkUserName(userName.getUserName());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(userName), HttpStatus.OK);
    }

    /**
     * 이메일 중복체크
     * id:joinApi_3
     * @param email
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity checkEmail(@Validated @ModelAttribute("email") JoinEmailDuplicateDto email, BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            userJoinService.checkEmail(email.getEmail());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(email), HttpStatus.OK);
    }
}
