package sky.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sky.board.domain.user.utill.ReadCookie;

@Controller
@Slf4j
public class HomeController {



    @GetMapping("/")
    public String home(HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("authentication.toString() = {}", authentication);
        log.info("authentication.getPrincipal().toString() = {}", authentication.getPrincipal().getClass());
        log.info("authentication.getDetails() = {}", authentication.getDetails());
        log.info("authentication.getAuthorities().toString() = {}", authentication.getAuthorities());
        log.info("authentication.isAuthenticated() = {}", authentication.isAuthenticated());

//        Optional<String> ss_id = ReadCookie.readCookie(cookies, "SS_ID");

//        log.info("ss_id = {}", ss_id);


        return "home";
    }



    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/view/dashboard")
    public String dashBoardPage(@AuthenticationPrincipal UserDetails user, Model model, HttpServletRequest request){
        return "redirect:/";
    }



}
