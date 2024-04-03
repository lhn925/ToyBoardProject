package sky.Sss.domain.track.entity.track;


import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import sky.Sss.domain.track.dto.track.TrackInfoSaveReqDto;
import sky.Sss.domain.track.entity.temp.TempTrackStorage;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.model.MainGenreType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.utili.JsEscape;

@Slf4j
@Entity
@Setter(AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbTrack extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 45 자 이하 이모티콘 x
    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(STRING)
    private MainGenreType mainGenreType;

    // 이모티콘 사용 불가능
    private String genre;

    // 다운로드 허용 여부 ture 허용, false 불가
    @Column(nullable = false)
    private Boolean isDownload;

    // 1000자 이하
    private String description;

    // track 커버
    private String coverUrl;

    // True 면 비공개
    // false 면 공개
    @Column(nullable = false)
    private Boolean isPrivacy;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Integer trackLength;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String storeFileName;

    // feed 에 공개가 된적 이 있는지
    // true 면 feed 에 공개 됨, false 면 공개 된 적 없음
    @Column(nullable = false)
    private Boolean isRelease;

    // 비활성화 여부 true:활성화 ,false:비활성화x
    @Column(nullable = false)
    private Boolean isStatus;

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackTagLink> tags = new ArrayList<>();

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackLikes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackAllPlayLogs> plays = new ArrayList<>();

    @OneToMany(mappedBy = "ssbTrack", cascade = ALL)
    private List<SsbTrackReply> replies = new ArrayList<>();

    // 링크 삭제
    public static void rmTagLink(SsbTrack ssbTrack, SsbTrackTagLink tagLinks) {
        ssbTrack.getTags().remove(tagLinks);
    }

    public static void rmTagLink(SsbTrack ssbTrack) {
        ssbTrack.tags.clear();
    }

    public static SsbTrack create(TrackInfoSaveReqDto trackInfoSaveReqDto, TempTrackStorage tempTrackStorage,
        User user) {
        SsbTrack ssbTrack = new SsbTrack();
        setUploadTrackFile(tempTrackStorage, ssbTrack);
        uploadInfo(ssbTrack, trackInfoSaveReqDto.getGenre(), trackInfoSaveReqDto.getGenreType(),
            trackInfoSaveReqDto.isPrivacy(), trackInfoSaveReqDto.isDownload(), trackInfoSaveReqDto.getTitle(),
            trackInfoSaveReqDto.getDesc());
        ssbTrack.setUser(user);

        SsbTrack.changeStatus(ssbTrack, Status.ON);
        return ssbTrack;
    }

    public static void uploadInfo(SsbTrack ssbTrack, String genre, String mainGenreType, Boolean isPrivacy,
        Boolean isDownload, String title, String description) {
        // enum Type 적용
        MainGenreType type = MainGenreType.findByType(mainGenreType);
        String subGenreType = type.getSubGenreValue(genre);
        ssbTrack.setTitle(JsEscape.escapeJS(title));
        ssbTrack.setGenre(subGenreType);
        ssbTrack.setMainGenreType(type);
        ssbTrack.setIsPrivacy(isPrivacy);
        ssbTrack.setIsDownload(isDownload);
        if (description.trim().length() > 1000) {
            throw new IllegalArgumentException("desc.error.length");
        }

        ssbTrack.setDescription(JsEscape.escapeJS(description));
    }

    public static void updateToken(String token, SsbTrack ssbTrack) {
        ssbTrack.setToken(token);
    }

    public static void updateIsRelease(SsbTrack ssbTrack, Boolean isRelease) {
        ssbTrack.setIsRelease(isRelease);

    }

    public static void changeStatus(SsbTrack ssbTrack, Status isStatus) {
        ssbTrack.setIsStatus(isStatus.getValue());
    }

    public static void updateId(SsbTrack ssbTrack, long id) {
        ssbTrack.setId(id);
    }

    public void updateTrackInfo(long id, LocalDateTime createdDateTime) {
        this.setId(id);
        this.setCreatedDateTime(createdDateTime);
    }

    //파일 정보 저장
    private static void setUploadTrackFile(TempTrackStorage tempTrackStorage, SsbTrack ssbTrack) {
        ssbTrack.setTrackLength(tempTrackStorage.getTrackLength());
        ssbTrack.setSize(tempTrackStorage.getSize());
        ssbTrack.setOriginalName(tempTrackStorage.getOriginalName());
        ssbTrack.setStoreFileName(tempTrackStorage.getStoreFileName());

    }
    public static void deleteTrackFile(SsbTrack ssbTrack, FileStore fileStore) {
        if (StringUtils.hasText(ssbTrack.getStoreFileName())) {
            fileStore.deleteFile(FileStore.TRACK_DIR, ssbTrack.getToken() + "/" + ssbTrack.getStoreFileName());
        }
    }

    public static void deleteCoverImg(SsbTrack ssbTrack, FileStore fileStore) {
        if (StringUtils.hasText(ssbTrack.getCoverUrl())) {
            fileStore.deleteFile(FileStore.IMAGE_DIR, ssbTrack.getCoverUrl());
        }
    }

    public static void updateCoverImg(String coverUrl, SsbTrack ssbTrack) {
        if (StringUtils.hasText(coverUrl)) {
            ssbTrack.setCoverUrl(coverUrl);
        }

    }

    public static String getSsbTrackCoverPath(FileStore fileStore, String coverUrl) {
        return fileStore.getImageDir() + coverUrl;
    }

    @Builder
    public SsbTrack(Long id) {
        this.id = id;
    }
}
