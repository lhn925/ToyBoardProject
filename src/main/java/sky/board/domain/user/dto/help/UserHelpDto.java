package sky.board.domain.user.dto.help;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.user.annotation.JoinValid;
import sky.board.domain.user.model.HelpType;
import sky.board.domain.user.utili.UserTokenUtil;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHelpDto implements Serializable {

    private String userId;

    @JoinValid(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "{userJoinForm.email}")
    private String email;

    /**
     * 암호화 되어 있는 이메일
     */
    private String enEmail;

    @NotBlank
    private String helpToken;

    private String authCode;

    private HelpType helpType;

    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    public static UserHelpDto getInstance() {
        UserHelpDto userHelpDto = new UserHelpDto();
        String token = UserTokenUtil.getToken();
        userHelpDto.setHelpToken(token);
        return userHelpDto;
    }

}
