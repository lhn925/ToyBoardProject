package sky.board.domain.user.service.log;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.log.LoginLogRepository;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.global.auditor.AuditorAwareImpl;
import sky.board.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLoginLogService {

    private final LoginLogRepository loginLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;
    private final AuditorAware auditorAware;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     */
    @Transactional
    public void save(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {

        Long uId = null;
        String userId = request.getParameter("userId");
        Optional<User> findOne = userQueryRepository.findByUserId(userId);


        User user = findOne.orElse(null);
        if (user != null) {
            uId = user.getId();
        }
        //비 로그인으로 접근시 저장할 userId
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        UserLoginLog userLoginLog = getUserLoginLog(uId, request, isSuccess, isStatus);
        Optional<UserLoginLog> saveLog = Optional.ofNullable(loginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(() -> new RuntimeException());
    }


    public Long getCount(String userId, LoginSuccess loginSuccess, Status isStatus) {

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserLoginLog> loginLogPage = loginLogRepository.getLoginLogPageable(userId, loginSuccess,
            isStatus.getValue(),
            pageRequest);
        return loginLogPage.getTotalElements();
    }

    /**
     * 유저 로그인 개체 생성
     *
     * @param request
     * @param isSuccess
     * @return
     */
    public UserLoginLog getUserLoginLog(Long uId, HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        return UserLoginLog.getLoginLog(uId, locationFinderService, request, isSuccess, isStatus);
    }

    @Transactional
    public void delete(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");
        loginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
    }

}
