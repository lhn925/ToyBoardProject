package sky.board.global.openapi;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.PathDetails;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.UserLogService;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.error.dto.Result;
import sky.board.global.openapi.dto.CaptchaNkeyDto;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;


@Slf4j
@RequestMapping("/open")
@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final UserLogService userLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final MessageSource ms;

    @GetMapping("/{userId}")
    public ResponseEntity loginFailCheck(@PathVariable String userId) throws InterruptedException {
        // 실패 횟수 가져온 다음
        Long loginLogCount = userLogService.getLoginLogCount(userId, LoginSuccess.FAIL, Status.ON);

        // 실패횟수가 5번 이하면 200
        if (loginLogCount < 5) {
            return new ResponseEntity(new Result<>(userId), HttpStatus.OK);
        }

        Map mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) mapKey.get("key");
        String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

        CaptchaNkeyDto captcha = CaptchaNkeyDto.builder()
            .captchaKey(key)
            .imageName(image).build();

        return new ResponseEntity(new Result<>(captcha), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/image/{fileName}")
    public Resource getImage(@PathVariable String fileName) throws MalformedURLException {
        return new UrlResource("file:" + PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, fileName, "jpg"));
    }

    @GetMapping("/again")
    public ResponseEntity captchaKEyAgain(@Validated @ModelAttribute CaptchaNkeyDto captchaNkeyDto,
        BindingResult bindingResult
        , HttpServletRequest request) throws IOException {

        log.info("captchaNkeyDto.getCaptchaKey() = {}", captchaNkeyDto.getCaptchaKey());
        log.info("captchaNkeyDto.getImageName() = {}", captchaNkeyDto.getImageName());
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        String fileName = PathDetails.getFileName(captchaNkeyDto.getImageName());
        log.info("fileName = {}", fileName);
            //받은 이미지 삭제
        apiExamCaptchaNkeyService.deleteImage(fileName);


        String key = (String) apiExamCaptchaNkeyService.getApiExamCaptchaNkey().get("key");
        String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

        log.info("image = {}", image);

        CaptchaNkeyDto captcha = CaptchaNkeyDto.builder()
            .captchaKey(key)
            .imageName(image).build();

        return new ResponseEntity(new Result<>(captcha), HttpStatus.OK);
    }

}