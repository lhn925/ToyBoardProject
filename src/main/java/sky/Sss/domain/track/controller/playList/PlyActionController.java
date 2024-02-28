package sky.Sss.domain.track.controller.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalCountRepDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.service.playList.PlyActionService;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.domain.user.service.UserQueryService;


/**
 * 트랙과 관련 사용자 활동을 모아놓은 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/ply/action")
@UserAuthorize
@RestController
public class PlyActionController {

    private final PlyActionService plyActionService;

    /**
     * playList 좋아요 등록 후 총 좋아요수 반환
     *
     * @param id
     * @return
     */
    @PostMapping("/likes/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> saveLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(plyActionService.addLikes(id, token));
    }


    /**
     * playList 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     * @return
     */
    @DeleteMapping("/likes/{id}/{token}")
    public ResponseEntity<TotalCountRepDto> removeLikes(@PathVariable Long id, @PathVariable String token) {
        if (id == null || id == 0 || token == null || token.length() == 0) {
            throw new IllegalArgumentException();
        }
        return ResponseEntity.ok(plyActionService.cancelLikes(id, token));
    }


}
