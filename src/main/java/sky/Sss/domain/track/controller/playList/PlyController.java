package sky.Sss.domain.track.controller.playList;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingSaveDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingUpdateDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackDeleteDto;
import sky.Sss.domain.track.service.playList.PlyService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.Result;

/**
 *
 * playList 생성 수정 삭제
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/ply")
@RestController
@UserAuthorize
public class PlyController {
    private final PlyService plyService;
    private final MessageSource ms;
    /**
     * 새로 생성
     * 트랙 추가
     * 플레이리스트 삭제
     */

    /**
     * 플레이리스트 생성
     *
     * @param playListSettingSaveDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> savePly(@Validated @RequestPart PlayListSettingSaveDto playListSettingSaveDto,
        BindingResult bindingResult, @RequestPart(required = false) MultipartFile coverImgFile,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();
        PlayListInfoDto trackPlayListInfoDto = plyService.addPly(playListSettingSaveDto, coverImgFile,
            session.getId());
        return ResponseEntity.ok(trackPlayListInfoDto);
    }
    /**
     * 정보 업데이트
     * @param playListSettingUpdateDto
     * @param bindingResult
     * @param coverImgFile
     * @param request
     * @return
     */
    @PutMapping
    public ResponseEntity<?> modifyPly(@Validated @RequestPart PlayListSettingUpdateDto playListSettingUpdateDto,
        BindingResult bindingResult, @RequestPart(required = false) MultipartFile coverImgFile,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        plyService.updatePlyInfo(playListSettingUpdateDto,coverImgFile);
        return ResponseEntity.ok().build();
    }

    /**
     * 정보 삭제
     * @param playListTrackDeleteDto
     * @param bindingResult
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity<?> removePly(@Validated @RequestBody PlayListTrackDeleteDto playListTrackDeleteDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        plyService.deletePly(playListTrackDeleteDto.getId(),playListTrackDeleteDto.getToken());

        return ResponseEntity.ok().build();
    }


}