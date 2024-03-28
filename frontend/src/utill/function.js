//패스워드 안전도 체크 함수
import {EmailApi} from "utill/api/email/EmailApi";
import {CodeCheckApi} from "utill/api/email/CodeCheckApi";
import {useState} from "react";

export function PwSecureCheckFn(level) {
  const secureLevel = {
    secLevelStr: '',
    secLevelClass: ''
  }
  switch (level) {
    case 0:
      secureLevel.secLevelStr = "사용불가";
      secureLevel.secLevelClass = "dangerous";
      break;
    case 1:
      secureLevel.secLevelStr = "위험";
      secureLevel.secLevelClass = "dangerous";
      break;
    case 2:
      secureLevel.secLevelStr = "보통";
      secureLevel.secLevelClass = "normal";
      break;
    case 3:
      secureLevel.secLevelStr = "안전";
      secureLevel.secLevelClass = "safe";
      break;
  }
  return secureLevel;
}

export function DateToDay(date) {
  return date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
}
export function getMonth(date) {
  return (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1;
}
export function FullDateTime (createdDateTime){
  let language = navigator.language;
  let timeString = new Date(createdDateTime).toLocaleTimeString(
      language);
  let dateString = new Date(
      createdDateTime).toLocaleDateString().split(".").join(
      ":").split(" ").join("");
  dateString = dateString.slice(0, dateString.length - 1);
  let localeString = new Date(createdDateTime).toLocaleString(
      language, {weekday: 'short'});
  return dateString + " " + localeString + " " + timeString;
}
export function DateFormat(date) {
  let month = getMonth(date);
  let day = DateToDay(date);
  return date.getFullYear() + "-" + month + "-" + day;
}

export function PwSecureLevel(password) {// 패스워드 안전 강도
  // 소문자로 이루어졌는데 같은문자만 도배일경우 - 사용불가
  // 그리고 8문자이하면 사용불가 한글 사용불가

  // 소문자인데 2개이상 다른걸로 이루어져 있는데 8글자이면 - 위험
  // 소문자인데 2개이상 다른걸로 이루어져 있는데 10글자이면 - 보통
  // 대문자+소문자,소문자+숫자,대문자로만 이어진 경우,소문자+특수기호,대문자+특수기호 9글자 이하면 보통
  // 소문자인데 2개이상 다른걸로 이루어져 있고 숫자 및 특수기호 포함,대문자 하나라도 포함하고 10글자 이상이면 - 안전

  let secLevel = 0;
  let regex1 = /^(.)\1{1,7}$/ // 같은 문자 반복 및 한글 8글자 이하 차단

  let regex2 = /[A-Z]/g;
  // 대문자 찾기

  let regex3 = /[!@#$%^&*()\-_=+\\|[\]{};:'",.<>/?]/;
  //특수표현식 찾기

  let regex4 = /\d+/g;
  // 숫자 찾기

  let regex5 = /(.)\1{7,}/;
  //연속된 문자열 찾기

  // let regex6 = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()\-_=+\\|[\]{};:'",.<>/?]).{8,16}$/;
  // //  비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.

  let regex7 = /[ㄱ-ㅎ가-힣]/;
  // 한글확인

  let regex8 = /[\uD800-\uDBFF][\uDC00-\uDFFF]/;
  // 이모지 확인
  /**
   * 0점 사용불가
   * 1점 위험
   * 2점 보통
   * 3점 안전
   *
   */

  /**
   * 소문자로 이루어졌는데 같은문자만 도배일경우 - 사용불가
   * 그리고 8문자이하면 사용불가 한글 사용불가
   */

  if (password.length < 8 || password.length >= 17) {
    secLevel = 0;
    return secLevel;
  }

  /**
   * 이모지 확인
   * 한글 확인
   * 연속된 글자 확인
   * 연속된 문자열 확인
   */
  if (regex8.test(password) ||
      regex7.test(password) ||
      regex1.test(password)) { // 0점 반환
    secLevel = 0;
    return secLevel;
  }

  if (regex2.test(password)) { // 대문자 검색
    console.log("2:" + regex2.test(password))
    ++secLevel;
  }
  if (regex4.test(password)) { // 숫자 검색
    ++secLevel;
  }
  if (regex3.test(password)) { // 특수표현 검색
    ++secLevel;
  }
  if (secLevel < 1) {
    if (regex5.test(password)) {
      secLevel = 0;
      return secLevel;
    }
  }
  return secLevel;
}

export function GetCountDownTime(minutes, seconds, remainingTime, setTimer,
    errorMsg) {
  let message = ": 인증 유효시간: " + minutes + ":"
      + seconds;
  if (remainingTime < 1) {
    setTimer(0);
    return {message: errorMsg, error: true};
    // 인증 시간 만료시 수행할 작업 추가
  } else if (remainingTime <= 60) { // 유효시간 1분남을시에 text-color 빨강으로 변경
    return {message: message, error: true};
  } else {
    return {message: message, error: false};
  }

}

export function GetInterval(timer, setTimer, authTimeLimit, setCountDownTime,
    errorMsg) {
  return setInterval(() => {
    const emailInterValObject = EmailInterValEvent(timer, authTimeLimit);
    const remainingTime = parseInt(emailInterValObject.timer);
    const minutes = emailInterValObject.minutes;
    const seconds = emailInterValObject.seconds;
    const getCountDownTime = GetCountDownTime(minutes, seconds, remainingTime,
        setTimer, errorMsg);

    setCountDownTime(
        {message: getCountDownTime.message, error: getCountDownTime.error});
  }, 1000);
}

export function Regex(type, value) { // 정규표현식 모음
  let regex;
  let testResult;

  switch (type) {
    case "userId": // * 아이디: 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.
      regex = /^[a-z0-9_-]{5,20}$/;
      break;
    case "userName":
      regex = /^[a-zA-Z0-9가-힣]{2,8}$/;
      break;
    case "email": //이메일 형식이 아닙니다.
      regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      break;
    default:
      return true;
  }

  testResult = regex.test(value);
  return testResult;
}

export function StartCountdown( // 유효시간 5분 알림
    authTimeLimit, authIssueTime) {

  return (new Date(authTimeLimit) - new Date(authIssueTime)) / 1000;
}

export function EmailInterValEvent(timer, authTimeLimit) {
  let minutes, seconds;
  timer = (new Date(authTimeLimit) - new Date()) / 1000;
  minutes = parseInt(timer / 60, 10); // 10진수로 출력
  seconds = parseInt(timer % 60, 10);

  minutes = minutes < 10 ? "0" + minutes : minutes;
  seconds = seconds < 10 ? "0" + seconds : seconds;

  return {
    timer: timer,
    minutes: minutes,
    seconds: seconds
  }

}

export function ChangeError(setErrors, name, message, error) {
  setErrors((errors) => {
    return {
      ...errors,
      [name]: {message: message, error: error}
    }
  });
}
export const encodeFileToBase64 = (fileBlob,setCoverImg) => {
  const reader = new FileReader();
  reader.readAsDataURL(fileBlob);

  return new Promise((resolve) => {
    reader.onload = () => {
      setCoverImg(reader.result);
      resolve();
    };
  });
};


export async function SendCode(url, body, setErrors, setAuth, setTimer,
    setAuthTimeLimit, messages) {
  const response = await EmailApi(url, body);
  if (response.code !== 200) {
    if (response.data.errorDetails !== undefined) {
      response.data.errorDetails.map((data) => {
        ChangeError(setErrors, "email", data.message, true);
      });
    } else {
      ChangeError(setErrors, "email", messages, true);
    }
  } else {
    ChangeError(setErrors, "email", '', false);
    ChangeError(setErrors, "authCode", '', false);
    setAuth({authToken: response.data.authToken, success: false})
    setTimer(await StartCountdown(response.data.authTimeLimit,
        response.data.authIssueTime));
    setAuthTimeLimit(await response.data.authTimeLimit);
  }
}

export async function AuthCodeCheck(setInputs,authCode, auth, setErrors, setCountDownTime,
    t, setTimer, setAuthTimeLimit, setAuth, emailRef , sendType) {
  const response = await CodeCheckApi(
      {authCode: authCode, authToken: auth.authToken, sendType:sendType});
  if (response.code !== 200) {
    if (response.data.errorDetails) {
      response.data.errorDetails.map((data) => {
        ChangeError(setErrors, "authCode", data.message, true);
      });
      setCountDownTime({message: '', error: false});
      return;
    }
    ChangeError(setErrors, "authCode", t(`errorMsg.server`), true);
  } else {
    ChangeError(setErrors, "authCode", t(`msg.auth.success`), false);
    ChangeError(setErrors, "email", '', false);
    setTimer(0);
    setAuthTimeLimit(null);
    setCountDownTime({message: '', error: false});
    setAuth({...auth, success: true})
    emailRef.current.value = response.data.email;
    setInputs((inputs) => {
      return{
        ...inputs,
        email:response.data.email
      }
    })
  }
}

// 상태와 로직을 하나의 hook으로 추출
export function useToggleableOptions(typeNames,initialOptions,selected) {
  const [options] = useState(initialOptions); // 옵션은 고정값이라 상태로 관리할 필요가 없음
  const [name,setName] = useState(typeNames); // 옵션은 고정값이라 상태로 관리할 필요가 없음
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(
      selected === null? initialOptions[0] : selected );

  const toggleOptions = () => {
    setIsOpen(!isOpen);
  }
  const onOptionClicked = value => () => {
    setSelectedOption(value);
    setIsOpen(false);
  };
  return {name,options, isOpen, selectedOption,onOptionClicked,setIsOpen,toggleOptions};
}

export function RegexCheck (name, input_value, setErrors, t)  {
  let isRegex = !Regex(name, input_value);
  let message = Regex ? t(`msg.userJoinForm.` + name) : '';
  ChangeError(setErrors, name, message, isRegex);
  return isRegex;
}

export async function ClickBtnSendCode(url, inputs, t, setErrors, variable, body,
    setAuth, setTimer, setAuthTimeLimit) {
  let email = inputs.email;
  if (email === "") {
    ChangeError(setErrors, "email", t(`errorMsg.NotBlank`), true);
    return;
  }
  if (!Regex("email", email)) {
    return;
  }
  if (variable.current.isDoubleClick) {
    return;
  }
  variable.current.isDoubleClick = true;
  await SendCode(url,
      body,
      setErrors, setAuth, setTimer,
      setAuthTimeLimit,
      t(`errorMsg.server`));
  variable.current.isDoubleClick = false;
}

export async function ClickBtnAuthCodeCheck(setInputs, inputs, auth, t, setErrors,
    variable,
    setCountDownTime,
    setTimer, setAuthTimeLimit, setAuth, emailRef, type) {
  const authCode = inputs.authCode;
  if (authCode === "" || auth.authToken === "") {
    const message = authCode === "" ? t(`msg.userJoinForm.authCode.NotBlank`)
        : t(`errorMsg.error.authToken`);
    ChangeError(setErrors, "authCode", message, true);
    return;
  }
  if (variable.current.isDoubleClick) {
    return;
  }
  variable.current.isDoubleClick = true;
  await AuthCodeCheck(setInputs, authCode, auth, setErrors, setCountDownTime, t,
      setTimer, setAuthTimeLimit, setAuth, emailRef, type);
  variable.current.isDoubleClick = false;
}