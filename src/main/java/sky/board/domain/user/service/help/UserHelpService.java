package sky.board.domain.user.service.help;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.help.UserPwResetFormDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.repository.UserQueryRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserHelpService {

    private final UserQueryRepository userQueryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 업데이트
     *
     * @param userPwResetFormDto
     * @return
     */
    @Transactional
    public UserDetails passwordUpdate(UserPwResetFormDto userPwResetFormDto) throws IllegalArgumentException {

        User findByUser = userQueryRepository.findByUserId(userPwResetFormDto.getUserId());
        //현재 비밀번호 와 대조
        isPasswordSameAsNew(userPwResetFormDto, findByUser);
        User.updatePw(findByUser, userPwResetFormDto.getNewPw(), userPwResetFormDto.getPwSecLevel(), passwordEncoder);
        return CustomUserDetails.builder()
            .userId(findByUser.getUserId())
            .email(findByUser.getEmail())
            .uId(findByUser.getId())
            .username(findByUser.getUserId()).build();
    }

    private void isPasswordSameAsNew(UserPwResetFormDto userPwResetFormDto, User findByUser)
        throws IllegalArgumentException {
        //현재 비밀번호 와 대조
        boolean matches = passwordEncoder.matches(userPwResetFormDto.getNewPw(), findByUser.getPassword());

        // 현재 비밀번호와 바꾸려는 비밀번호가 같으면 error
        if (matches) {
            throw new IllegalArgumentException("pw.isPasswordSameAsNew");
        }
    }
}