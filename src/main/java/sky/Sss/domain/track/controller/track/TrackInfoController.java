package sky.Sss.domain.track.controller.track;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLengthRepDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.file.utili.FileStore;

/**
 */

@Slf4j
@RestController
@RequestMapping("/tracks/info")
@RequiredArgsConstructor
public class TrackInfoController {
    private final TrackService trackService;
    private final UserQueryService userQueryService;
    /**
     * 재생할 트랙 정보 출력
     *
     * @param id track Id
     * @param request
     * @return
     * @throws SsbTrackAccessDeniedException
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrackPlayRepDto> getTrackInfo(@PathVariable Long id, HttpServletRequest request) throws SsbTrackAccessDeniedException {
        TrackPlayRepDto trackPlayDto = trackService.getAuthorizedTrackInfo(id, Status.ON,
            request.getHeader("User-Agent"));
        if (trackPlayDto == null) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(trackPlayDto);
    }

    /**
     * 업로드한 track 시간 총합
     *
     * @return
     */
    @UserAuthorize
    @GetMapping("/total")
    public ResponseEntity<TotalLengthRepDto> getTotalLength() {
        Integer totalLength = trackService.getTotalLength(userQueryService.findOne());
        return ResponseEntity.ok(new TotalLengthRepDto(totalLength, FileStore.TRACK_UPLOAD_LIMIT));
    }
}