package sky.board.domain.user.utili;

import jakarta.validation.GroupSequence;
import sky.board.domain.user.utili.ValidationGroups.NotBlankGroup;
import sky.board.domain.user.utili.ValidationGroups.PatternCheckGroup;
// validation 우선순위 설정
@GroupSequence({NotBlankGroup.class, PatternCheckGroup.class})
public interface ValidationSequence {

}
