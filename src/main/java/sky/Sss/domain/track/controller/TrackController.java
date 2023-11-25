package sky.Sss.domain.track.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.track.TotalLengthDto;
import sky.Sss.domain.track.dto.track.TrackDeleteDto;
import sky.Sss.domain.track.dto.track.TrackInfoDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.dto.track.TrackInfoUpdateDto;
import sky.Sss.domain.track.service.TrackService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.file.utili.FileStore;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/track")
@RestController
@UserAuthorize
public class TrackController {

    private final TrackService trackService;
    private final MessageSource ms;
    private final UserQueryService userQueryService;
    /**
     * 플레이 리스트 수정
     * 플레이 리스트 삭제
     * 플레이 리스트 개별 곡 삭제
     *
     * @param trackMetaUploadDto
     * @param bindingResult
     * @param request
     * @return
     */
    /**
     * 개별곡 저장
     */
    @PostMapping
    public ResponseEntity saveTrack(@Validated @RequestPart TrackInfoSaveDto trackInfoSaveDto,
        BindingResult bindingResult,
        @RequestPart(required = false) MultipartFile coverImgFile,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        TrackInfoDto trackInfoDto = trackService.saveTrackFile(trackInfoSaveDto, coverImgFile, session.getId());
        return ResponseEntity.ok(trackInfoDto);
    }
    /**
     * 트랙정보 업데이트
     *
     * @return
     */
    @PutMapping
    public ResponseEntity updateTrack(@Validated @RequestPart TrackInfoUpdateDto trackInfoUpdateDto,
        @RequestPart(required = false) MultipartFile coverImgFile,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        trackService.updateTrackInfo(trackInfoUpdateDto, coverImgFile);
        return ResponseEntity.ok().build();
    }

    /**
     * 트랙 파일 삭제
     */
    @DeleteMapping
    public ResponseEntity deleteTrack(@Validated @RequestBody TrackDeleteDto trackDeleteDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        trackService.deleteTrack(trackDeleteDto.getId(), trackDeleteDto.getToken());
        return ResponseEntity.ok().build();
    }
    /**
     *
     * 업로드한 track 시간 총합
     * @return
     */
    @GetMapping("/total")
    public ResponseEntity getTotalLength() {
        Integer totalLength = trackService.getTotalLength(userQueryService.findOne());
        return ResponseEntity.ok(new TotalLengthDto(totalLength, FileStore.TRACK_UPLOAD_LIMIT));
    }
}
