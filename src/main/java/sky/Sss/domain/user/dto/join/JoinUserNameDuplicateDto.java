package sky.Sss.domain.user.dto.join;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.annotation.JoinValid;
import sky.Sss.global.utili.validation.regex.RegexPatterns;


/**
 * 아이디 중복 Dto
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinUserNameDuplicateDto implements Serializable  {

    @JoinValid(regexp = RegexPatterns.USER_NAME_REGEX, message = "{userJoinForm.userName}")
    private String userName;
}

