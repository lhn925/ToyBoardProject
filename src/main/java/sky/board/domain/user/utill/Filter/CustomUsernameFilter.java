package sky.board.domain.user.utill.Filter;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import sky.board.domain.user.exception.CaptchaMisMatchFactorException;
import sky.board.domain.user.exception.LoginFailCountException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.PathDetails;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.UserLogService;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;

@Slf4j
public class CustomUsernameFilter extends UsernamePasswordAuthenticationFilter {

    private final UserLogService userLogService;

    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "userId";

    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;

    private boolean postOnly = true;

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failHandler;

    public CustomUsernameFilter(AuthenticationManager authenticationManager, UserLogService userLogService,
        AuthenticationSuccessHandler successHandler,
        AuthenticationFailureHandler failureHandler,

        ApiExamCaptchaNkeyService apiExamCaptchaNkeyService) {
        super(authenticationManager);
        super.setAuthenticationSuccessHandler(successHandler);
        super.setAuthenticationFailureHandler(failureHandler);
        this.successHandler = successHandler;
        this.failHandler = failureHandler;
        this.userLogService = userLogService;
        this.apiExamCaptchaNkeyService = apiExamCaptchaNkeyService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        final int LIMIT = 5;

        log.info("request.getMethod() = {}", request.getMethod());
        /**
         * 유저가 로그인 버튼을 입력한 URL 저장
         */

        log.info(" 여기는 필터 메서드");
        String chptchaKey = getRequestValue("captchaKey", request);

        // 2차인증 키가 있을 경우 인증번호를 확인하고 널일 경우 반환
//        result = {"result":false,"errorMessage":"Invalid key.","errorCode":"CT001"}

        // 2차 인증 성공시 true,아니며 false 발급을 안 받았을경우도 false
        boolean isCaptcha = false;
        if (StringUtils.hasText(chptchaKey)) {
            String captcha = request.getParameter("captcha");
            captcha = (captcha != null) ? captcha.trim() : "";

            String filename = getRequestValue("filename", request);
            if (!StringUtils.hasText(captcha)) {
                throw new UsernameNotFoundException("captcha Not found");
            }
            // 2차 인증 키 검증
            Map result = null;
            result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult(filename, chptchaKey,
                captcha);

            isCaptcha = (boolean) result.get("result");

            // 번호가 맞지 않은 경우
            if (!isCaptcha) {
                throw new CaptchaMisMatchFactorException("CaptchaMisMatchFactorException");
            }

        }
        String userId = obtainUsername(request);
        userId = (userId != null) ? userId.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        // failCount 전달  success 핸들러에 전달
        Long failCount = userLogService.getLoginLogCount(userId, LoginSuccess.FAIL, Status.ON);

        request.setAttribute("failCount", failCount);
        // 로그인 실패가 5번 이상 일 경우 그리고 2차인증을 성공하지 못했을 경우
        if (LIMIT <= failCount && !isCaptcha) {
            Map mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
            String key = (String) mapKey.get("key");
            String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
            request.setAttribute("captchaKey", key);
            request.setAttribute("imagePath", PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, image, "jpg"));

            throw new LoginFailCountException("LoginVerificationFilter count < 5");
        }

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(userId,
            password);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }


    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }


    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    private String getRequestValue(String key, HttpServletRequest request) {
        String value = request.getParameter(key);
        value = (value != null) ? value.trim() : "";
        return value;
    }
}