package sky.board.domain.user.utili.handler.login;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomCookieLoginSuccessHandler implements CustomLoginSuccessHandler {


    private final UserLoginStatusService userLoginStatusService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) throws IOException, ServletException {
        CustomLoginSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        request.getRequestURL();
        setSession(request, authentication);
        log.info("request.getRequestURI() = {}", request.getRequestURI());
        sendRedirect(response, request.getRequestURL().toString(), request.getContextPath());
    }


    @Override
    public void sendRedirect(HttpServletResponse response, String url, String redirectUrl) throws IOException {
        response.sendRedirect(redirectUrl + url);
    }

    @Override
    public void saveContext(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    }

    @Override
    public void setSession(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserInfoDto userInfo = UserInfoDto.createUserInfo(userDetails);
        session.setAttribute(RedisKeyDto.USER_KEY, userInfo);
    }

    @Override
    public void saveLoginStatus(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        try {
            userLoginStatusService.save(request);
        } catch (LoginException e) {
            throw new RuntimeException("saveLoginStatus:" + e);
        }
    }


}