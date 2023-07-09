package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;


@Slf4j
@RequestMapping("/join")
@RequiredArgsConstructor
@Controller
public class JoinController {

    private final UserJoinService userJoinService;
    private final MessageSource ms;

    /**
     * 회원가입 페이지 이동 api
     *
     * @param model
     * @return
     */
    @GetMapping
    public String joinForm(@Validated @ModelAttribute UserJoinAgreeDto userJoinAgreeDto,
        BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "redirect:/join/agree";
        }
        HttpSession session = request.getSession();

        if (session.getAttribute("emailAuthCodeDto") != null) {// 인증번호 삭제 (뒤로가기 버그 방지)
            session.removeAttribute("emailAuthCodeDto");
        }

        Optional<String> agreeToken = readCookie(request.getCookies(), "agreeToken");

        // agreeToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (agreeToken.isEmpty() || (!agreeToken.orElse(null).equals(userJoinAgreeDto.getAgreeToken()))) {
            bindingResult.reject("code.error");
            return "redirect:/join/agree";
        }

        // 약관 동의 데이터 session에 저장
        session.setAttribute(userJoinAgreeDto.getAgreeToken(), userJoinAgreeDto);

        model.addAttribute("userJoinPostDto", new UserJoinPostDto());
        return "user/join/joinForm";
    }


    /**
     * 회원가입 api
     *
     * @param userJoinDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute UserJoinPostDto userJoinDto,
        BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            if (StringUtils.hasText(userJoinDto.getPassword())) { // 비밀번호 재전송
                model.addAttribute("rePw", userJoinDto.getPassword());
            }
            return "user/join/joinForm";
        }

        //cookie 기간 확인
        Cookie[] cookies = request.getCookies();

        HttpSession session = request.getSession();

        Optional<String> agreeToken = readCookie(cookies, "agreeToken");

        // 동의 여부 token이 없을 경우 다시 동의 폼으로
        // 세션에 저장해둔 동의 여부 갖고오기
        UserJoinAgreeDto userJoinAgreeDto = (UserJoinAgreeDto) session.getAttribute(agreeToken.orElse(null));
        if (userJoinAgreeDto == null) {
            return "redirect:/join/agree";
        }

        try {
            userJoinService.join(userJoinDto, userJoinAgreeDto);
        } catch (DuplicateCheckException e) {
            bindingResult.reject("join.duplication", new Object[]{e.getMessage()}, null);
            return "user/join/joinForm";
        }
        return "redirect:/";
    }

    /**
     * 이용약관 동의 페이지 이동
     * 유효토큰 생성 api
     *
     * @param model
     * @return
     */
    @GetMapping("/agree")
    public String joinAgreeForm(Model model, HttpServletResponse response) {

        // 쿠키 생성
        UserJoinAgreeDto userJoinAgreeDto = UserJoinAgreeDto.createUserJoinAgree();
        Cookie cookie = new Cookie("agreeToken", userJoinAgreeDto.getAgreeToken());
        cookie.setMaxAge(900); // 15분 유효
        cookie.setPath("/join");

        // 쿠키에 agreeToken 저장
        response.addCookie(cookie);

        //회원가입 토큰 발급
        model.addAttribute("userJoinAgreeDto", userJoinAgreeDto);
        return "user/join/joinAgreeForm";
    }


    private static Optional<String> readCookie(Cookie[] cookies, String key) {
        Optional<String> agreeToken = Arrays.stream(cookies)
            .filter(c -> c.getName().equals(key))
            .map(Cookie::getValue)
            .findAny();
        return agreeToken;
    }
}
