import React, {useEffect, useRef, useState} from 'react';
import 'css/playerBar/playerBar.css';
import 'css/track/track.css';
import ReactPlayer from "react-player";

import {
  USERS_FILE_IMAGE,
  USERS_FILE_TRACK_PLAY
} from "utill/api/ApiEndpoints";
import {
  chartLogSave,
  durationTime,
  secondsToTime,
  toggleLike
} from "utill/function";
import {
  ALL_PLAY,
  playBackTypes,
  REPEAT_ALL,
  REPEAT_ONE
} from "utill/enum/PlaybackTypes";
import {toast} from "react-toastify";
import TrackLikeApi from "../../utill/api/trackPlayer/TrackLikeApi";
import TrackLikeCancelApi from "../../utill/api/trackPlayer/TrackLikeCancelApi";

/**
 *
 *
 *
 */
export const TrackPlayer = ({
  changePlayLog,
  localPlayLog,
  changePlaying,
  playing,
  currentTrack,
  createCurrentPlayLog,
  playerSettings,
  updateSettings,
  localPly,
  localPlyAddTracks,
  updateCurrPlayLog,
  createCurrentTrack,
  shuffleOrders,
  getPlyTrackByOrder,
  updatePlyTrackInfo,
}) => {
  const playerRef = useRef();

  const [settingsInfo, setSettingsInfo] = useState(
      playerSettings.item);

  const [localPlyInfo, setLocalPlyInfo] = useState(localPly.item);
  const [playOrders, setPlayOrders] = useState(localPly.playOrders);

  const [url, setUrl] = useState(null);

  const [trackInfo, setTrackInfo] = useState(currentTrack);

  const [isPlaying, setIsPlaying] = useState(playing.item.playing);
  const [seeking, setSeeking] = useState(false);
  const [isEnd, setIsEnd] = useState(false);

  // 일시정지,다음곡,이전곡,
  useEffect(() => {
    if (currentTrack === -1) {
      return;
    }
    // 이전 트랙
    const prevPlayToken = trackInfo.playLog?.token;
    // 정보가 있고, playLog token 이 다를 경우에만 그리고
    if (currentTrack.playLog?.token
        && currentTrack.playLog.token !== prevPlayToken) {
      setUrl(
          `${USERS_FILE_TRACK_PLAY}${currentTrack.id}/${currentTrack.playLog.token}`);
    }
    setTrackInfo(currentTrack);
  }, [currentTrack]);

  useEffect(() => {
    setLocalPlyInfo(localPly.item);
    setPlayOrders(localPly.playOrders);
  }, [localPly.item, localPly.playOrders]);

  useEffect(() => {
    setIsPlaying(playing.item.playing);
    setSettingsInfo(playerSettings.item);

  }, [playing.item.playing, playerSettings.item]);

  useEffect(() => {
    if (seeking) {
      changeIsChartAndLogSave();
    }
  }, [seeking]);

  // shuffle 여부 , index 여부
  useEffect(() => {
    if (localPlyInfo.length === 0) {
      return;
    }
    const plyTrackByOrder = getPlyTrackByOrder(playerSettings.item.order);

    if (settingsInfo.order !== playerSettings.item.order) {
      changePlayLog(plyTrackByOrder.id, plyTrackByOrder.index,
          new Date().getTime());
    }

    // 셔플을 누를경우 Index 변환 문제로 인해
    // 리로딩 되는 문제 발생
    if (playerSettings.item.shuffle === settingsInfo.shuffle) {
      if (playing.item.playing) {
        createCurrentPlayLog(plyTrackByOrder.id);
        return;
      }
      createCurrentTrack(plyTrackByOrder);
    }

    // if (currentTrack !== -1) {
    //   // const orderTrack = getPlyTrackByOrder(playerSettings.item.order);
    //   changePlayLog(currentTrack.id, playerSettings.order, new Date().getTime());
    // }

  }, [playerSettings.item.order, playerSettings.item.shuffle]);

  // 일시정지,플레이버튼
  const playPause = (e) => {
    e.preventDefault(); // 기본동작 정지

    if (!isPlaying && localPlyInfo.length === 0) {
      toast.error("텍스트 추가) 재생할 트랙을 추가해주세요.")
      return;
    }

    changePlaying(!isPlaying);
    const trackId = Number.parseInt(e.target.dataset.id);
    if (trackId === -1) {
      return;
    }
    // 같은 트랙인지 확인
    const trackEq = trackInfo.id === trackId;
    if (!isPlaying) { // 재생일 경우
      const playLog = trackInfo.playLog;
      if (trackEq && playLog) {// 같은 아이디에다가 PlayLog 까지 있다면 X
        return;
      }
      createCurrentPlayLog(currentTrack.id);
    } else {
    }
  }
  let linkToTrack;
  let linkToUploader;

  let followButton = 'liked-button-t';


  const onReadyHandler = function (e) {
  }
  // 재생이 제일 먼저 시작될때
  const onStartHandler = function (e) {
    setIsEnd(false);
    // 0초부터 시작하지 않았다면 false
    if (settingsInfo.playedSeconds > 1 && trackInfo.playLog.isChartLog) {
      updateCurrPlayLog("isChartLog", false);
    }
    updateSettings("playedSeconds", settingsInfo.playedSeconds);
  }

  const onEndedHandler = (e) => {
    // 전체재생
    // 전체 반복 재생
    // 무한 반복 재생
    const playBackType = playBackTypes[settingsInfo.playBackType];
    // 확인
    setIsEnd(true);

    chartLogSave(trackInfo, trackInfo.playLog.isChartLog, updateCurrPlayLog);

    updateSettings("playedSeconds", 0);
    updateSettings("played", 0);

    // 현재 인덱스
    const playIndex = settingsInfo.order;

    // 전체 인덱스
    const localLength = localPlyInfo.length;

    // 다음 인덱스
    const nextIndex = playIndex + 1;

    // 마지막 인지 확인
    // 다음인덱스 전체인덱스보다 크면 true
    const isLast = nextIndex === localLength;

    // 전체 무한 재생이면서 랜덤재생이 아닐 경우
    if (playBackType === REPEAT_ALL) {
      nextTrackPlay(playIndex);
      return;
    }
    // 한곡 무한 반복
    if (playBackType === REPEAT_ONE) {
      createCurrentPlayLog(trackInfo.id);
      return;
    }
    // 전체 재생인데 마지막인 아닌경우
    if (!isLast && playBackType === ALL_PLAY) {
      nextTrackPlay(playIndex);
      return;
    }
    // 전체 재생인데 마지막인 경우
    if (isLast && playBackType === ALL_PLAY) {
      createCurrentTrack(trackInfo);
      return;
    }
  }
  const onProgressHandler = (e) => {
    if (trackInfo.playLog === null) {
      return;
    }
    const totalPlayTime = trackInfo.playLog.playTime + 0.05;
    updateCurrPlayLog("playTime", totalPlayTime);
    if (!trackInfo.playLog.isChartLog && totalPlayTime
        >= trackInfo.playLog.miniNumPlayTime) {
      chartLogSave(trackInfo, trackInfo.isChartLog, updateCurrPlayLog);
    }
    if (!seeking && !isEnd) {
      updateSettings("playedSeconds", e.playedSeconds);
      updateSettings("played", Number.parseInt(e.played * 100));
      updateSettings("loaded", Number.parseInt(e.loaded * 100));
    }
  }
  const onDurationHandler = (e) => {
    playerRef.current.seekTo(settingsInfo.playedSeconds, 'seconds');
  }

  function setState(param) {
  }

  const onErrorHandler = (e) => {
    createCurrentPlayLog(trackInfo.id);
  }
  const getPlayButton = (playing) => {
    return playing ? 'play-pause-btn-paused' : 'play-pause-btn';
  }
  const getMuteButton = (muted) => {
    return muted ? 'mute-volume-btn-muted' : 'mute-volume-btn';
  }
  const divRef = useRef(null);
  const onMouseUp = (event) => {
    if (!divRef.current) {
      return;
    }
    const rect = divRef.current.getBoundingClientRect();  // div의 위치와 크기 정보
    const width = rect.width;
    const relativeX = event.clientX - rect.left;  // 상대적 X 위치 계산
    // 전체 길이에서 percent;
    const percent = (relativeX / width) * 100;
    // 전체 길이에서 percent 를 곱한 값
    const seekToSeconds = trackInfo.trackLength * (percent / 100);
    if (playerRef.current) {
      playerRef.current.seekTo(seekToSeconds, 'seconds');
      updateSettings("playedSeconds", seekToSeconds);
      updateSettings("played", percent);
      setSeeking(false);// 재생이동 중
    }
    // 원한다면 상태를 업데이트 하거나 화면에 표시할 수 있습니다.
  };
  const onMouseDown = () => {
    setSeeking(true);// 재생이동 중
  }
  const onMouseMove = (event) => {
    if (!seeking) {
      return;
    }
    if (!divRef.current) {
      return;
    }
    const rect = divRef.current.getBoundingClientRect();  // div의 위치와 크기 정보
    const width = rect.width;
    const relativeX = event.clientX - rect.left;  // 상대적 X 위치 계산
    // 전체 길이에서 percent;
    const percent = (relativeX / width) * 100;
    // 전체 길이에서 percent 를 곱한 값
    const seekToSeconds = trackInfo.trackLength * (percent / 100);
    updateSettings("playedSeconds", seekToSeconds);
    updateSettings("played", percent);
  }
  const preBtnOnClick = () => {
    changeIsChartAndLogSave();
    const localLength = localPlyInfo.length;

    const maxIndex = localLength === 0 ? localLength : localLength - 1;

    const prevIndex = settingsInfo.order - 1;

    const changeIndex = prevIndex < 0 ? maxIndex : prevIndex;

    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    if (!isPlaying) {
      changePlaying(!isPlaying);
    }
    updateSettings("playedSeconds", 0);
    // 5초보다 크다면 이전곡이 아닌 0초부터 시작
    if (settingsInfo.playedSeconds > 5) {
      createCurrentPlayLog(trackInfo.id);
      return;
    }

    // 하나만 있을경우
    if (playerSettings.order === changeIndex) {
      createCurrentPlayLog(trackInfo.id);
      return;
    }

    updateSettings("order", changeIndex);
  }

  function nextTrackPlay(playIndex) {
    const totalLength = localPlyInfo.length;
    const minIndex = 0;
    const nextIndex = playIndex + 1;
    const changeIndex = nextIndex === totalLength ? minIndex : nextIndex;
    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    if (playIndex === changeIndex) {
      createCurrentPlayLog(trackInfo.id);
    } else {
      updateSettings("order", changeIndex);
    }

    updateSettings("playedSeconds", 0);

    if (!isPlaying) {
      changePlaying(!isPlaying);
    }
  }

  function changeIsChartAndLogSave() {
    if (trackInfo.playLog === null) {
      return;
    }
    chartLogSave(trackInfo, false, updateCurrPlayLog);
  }

  const nextBtnOnClick = () => {
    changeIsChartAndLogSave();
    nextTrackPlay(settingsInfo.order);
  }

  const changePlayBackType = (e) => {
    const maxIndex = playBackTypes.length - 1;
    const currentIndex = Number.parseInt(e.target.dataset.id);

    const changeIndex = currentIndex === maxIndex ? 0 : currentIndex + 1;

    updateSettings("playBackType", changeIndex);
  }

  const changeShuffleType = () => {
    const isShuffle = !settingsInfo.shuffle;
    // 랜덤 재생에서
    // 뒤로가기를 눌렀을 경우
    // 이전에 들었던곡이 없다면 랜덤곡
    // 있다면?
    // 그 이전곡 플레이 만약
    // 그 이전곡에서 다음 플레이를 누르면 뒤로가기전 플레이곡

    updateSettings("shuffle", isShuffle);
    shuffleOrders(isShuffle);
    if (isShuffle) {
      updateSettings("order", 0);
      return;
    }
    // 기본 정렬된 Index 반환
    const order = playOrders[settingsInfo.order];
    updateSettings("order", order);
  }
  // 일시정지시
  const onPauseHandler = (e) => {
    if (trackInfo.playLog === null) {
      return;
    }
    changeIsChartAndLogSave();
  }
  return (
      <div id='track-player-bar'>
        <div id='track-player-container'>
          <div id='tp-controller'>
            <div id='previous-btn'
                 className='controller-btn' onClick={preBtnOnClick}></div>
            <div id={getPlayButton(isPlaying)}
                 data-id={trackInfo.id}
                 className='controller-btn'
                 onClick={(e) => playPause(e)}></div>
            <div id='next-btn' className='controller-btn'
                 onClick={nextBtnOnClick}></div>
            <div onClick={changeShuffleType}
                 className={'controller-btn bg_player shuffle-btn '
                     + (settingsInfo.shuffle && 'active')}></div>
            <div data-id={settingsInfo.playBackType}
                 onClick={changePlayBackType}
                 className={'bg_player controller-btn '
                     + playBackTypes[settingsInfo.playBackType]}></div>
          </div>
          <div id='tp-progress'>
            <div id='tp-timepassed'>{secondsToTime(
                settingsInfo.playedSeconds)}</div>
            <div style={{width: '500px'}} ref={divRef} onMouseDown={onMouseDown}
                 onMouseOver={onMouseMove}
                 onMouseUp={onMouseUp} id='tp-scrubbar'
                 data-seconds={settingsInfo.played}>
              <div id='scrub-bg'></div>
              <div id='scrub-progress'
                   style={{width: settingsInfo.played + `%`}}></div>
              <div id='scrup-handle'
                   style={{left: settingsInfo.played + `%`}}></div>
            </div>
            <div id='tp-duration'>{durationTime(trackInfo.trackLength)}</div>
          </div>
          <div className='tp-track-dets'>

            <div className="volume_btn">
              <div id={getMuteButton(settingsInfo.muted)}
                   className='controller-btn'
                   onClick={() => updateSettings("muted", !settingsInfo.muted)}>
              </div>
              <input
                  id="volumeControl"
                  type="range"
                  className="vertical-slider"
                  min="0"
                  max="1"
                  step="0.01"
                  value={settingsInfo.muted ? 0 : settingsInfo.volume}
                  onChange={(e) => updateSettings("volume", e.target.value)}
              />
            </div>

            <div className='tp-td-uploader-pic'>
              <a href={linkToTrack}><img className="player_cover_img"
                                         src={trackInfo.coverUrl
                                             && USERS_FILE_IMAGE
                                             + trackInfo.coverUrl}
                                         alt={""}/></a>
            </div>
            <div className='tp-td-track-info text-start'>
              <a href={linkToUploader}><p
                  className='tp-track-uploader'>{trackInfo.userName}</p></a>
              <a href={linkToTrack}><p
                  className='tp-track-name'>{trackInfo.title}</p>
              </a>
            </div>
            <div className={'controller-btn ' + (trackInfo.isLike
                ? 'liked-button-t' : 'liked-button')}
                 data-id={currentTrack.id}
                 onClick={(e) => toggleLike(trackInfo,updatePlyTrackInfo,
                     e)}></div>
            <div id={followButton} className='controller-btn'
                 onClick={(e) => toggleLike(currentTrack.id, e)}></div>
            <div id='playlist-button' className='controller-btn'></div>
          </div>
        </div>

        <ReactPlayer
            ref={playerRef}
            width='100%'
            height='0%'
            url={url}
            // url={getUrl()}
            // url={"/users/file/track/play/202/124a25c32864139a2149"} // 재생할 url
            playing={isPlaying} // 재생 여부 기본 : false
            onError={onErrorHandler}
            loop={settingsInfo.playbackRate === REPEAT_ONE} // 반복재생 여부 false
            volume={Number.parseFloat(settingsInfo.volume)} // 볼륨값 기본 Null
            muted={settingsInfo.muted} // 음소거 여부
            onStart={onStartHandler}
            //일시중지 또는 버퍼링 후 미디어 재생이 시작되거나 재개될 때 호출됩니다
            progressInterval={50}
            //onProgress콜백 사이의 시간 (밀리초)
            onEnded={onEndedHandler}

            //미디어 재생이 끝나면 호출됩니다 . ◦ 다음으로 설정된
            //   경우 실행되지 않습니다 .looptrue
            onProgress={onProgressHandler}
            onPause={onPauseHandler}
            // 콜백을 포함하고 played 분수 및 초 단위 loaded 로 진행   ◦ 예:playedSecondsloadedSeconds
            /*{ played: 0.12, playedSeconds: 11.3, loaded: 0.34, loadedSeconds: 16.7 }*/
            onDuration={onDurationHandler}// 미디어 지속 시간(초)을 포함하는 콜백
            onReady={onReadyHandler}//미디어가 로드되고 재생할 준비가 되면 호출됩니다.
        />
      </div>
  );
};

