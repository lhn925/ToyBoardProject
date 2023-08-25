package sky.board.domain.user.dto.join;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.utili.UserTokenUtil;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJoinAgreeDto implements Serializable  {
    @NotBlank
    private String agreeToken;

    @AssertTrue(message = "userJoinAgree.AssertTrue")
    private boolean sbbAgreement;

    @AssertTrue(message = "userJoinAgree.AssertTrue")
    private boolean infoAgreement;

    public static UserJoinAgreeDto createUserJoinAgree() {
        UserJoinAgreeDto userJoinAgreeResponseDto = new UserJoinAgreeDto();
        String token = UserTokenUtil.getToken();
        userJoinAgreeResponseDto.setAgreeToken(token);
        return userJoinAgreeResponseDto;
    }

}
