package sky.board.domain.user.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.board.domain.user.controller.JoinController;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;


@SpringBootTest
class UserJoinServiceTest {

    @Autowired
    UserJoinService userJoinService;

    @Autowired
    JoinController joinController;

    @Test
    void join() throws Exception {
        UserJoinPostDto userJoinDto = new UserJoinPostDto();
/*

        userJoinDto.setEmail("2221312512@daum.net");
        userJoinDto.setUserName("아이유입니1다2");
        userJoinDto.setNotification_enabled(false);
        userJoinDto.setUserId("dlagksmf0171");
        userJoinDto.setPassword("dlagksmf2");
        Long join1 = userJoinService.join(userJoinDto);

        UserJoinDto userJoinDto1 = new UserJoinDto();

        userJoinDto1.setEmail("222112@daum.net");
        userJoinDto1.setUserName("아이입니다2");
        userJoinDto1.setNotification_enabled(false);
        userJoinDto1.setUserId("agksmf071");
        userJoinDto1.setPassword("dlagksmf2");
        Long join2 = userJoinService.join(userJoinDto1);

        UserJoinDto userJoinDto2 = new UserJoinDto();
        userJoinDto2.setEmail("2212512@daum.net");
        userJoinDto2.setUserName("1아이유입니다2");
        userJoinDto2.setNotification_enabled(false);
        userJoinDto2.setUserId("dksmf071");
        userJoinDto2.setPassword("dlagksmf2");
        Long join3 = userJoinService.join(userJoinDto2);
*/

        for (int i = 0; i < 10; i++) {
            UserJoinPostDto userJoinDto4 = new UserJoinPostDto();
            userJoinDto4.setEmail(i + "2512@daum.net");
            userJoinDto4.setUserName(i + "유입니다2");
            userJoinDto4.setUserId(i + "dksmf071");
            userJoinDto4.setPassword(i + "dlagksmf2");
            userJoinService.join(userJoinDto4, UserJoinAgreeDto.createUserJoinAgree());
        }
    }

    @Test
    void join2() throws Exception {
        UserJoinPostDto userJoinDto = new UserJoinPostDto();

        userJoinDto.setEmail("222132512@daum.net");
        userJoinDto.setUserName("아이유입니다2");
        userJoinDto.setPassword("dkdldb@2");
        Long join1 = userJoinService.join(userJoinDto, UserJoinAgreeDto.createUserJoinAgree());

        UserJoinPostDto userJoinDto1 = new UserJoinPostDto();

        userJoinDto1.setEmail("asd112@daum.net");
        userJoinDto1.setUserName("asd아이입니다2");
        userJoinDto1.setPassword("dkdldb@2");
        userJoinDto1.setUserId("agkasdsmf071");
        Long join2 = userJoinService.join(userJoinDto1, UserJoinAgreeDto.createUserJoinAgree());

        UserJoinPostDto userJoinDto2 = new UserJoinPostDto();

        userJoinDto2.setEmail("221asd2512@daum.net");
        userJoinDto2.setUserName("asd1아이유입니다2");
        userJoinDto2.setPassword("dkdldb@2");
        userJoinDto2.setUserId("dksasfsamf071");
        Long join3 = userJoinService.join(userJoinDto2, UserJoinAgreeDto.createUserJoinAgree());

        System.out.println("join id = " + join1);
        System.out.println("join id = " + join2);
        System.out.println("join id = " + join3);
    }

    @Test
    void checkId() {
        userJoinService.checkEmail("02512@daum.net");
    }


}