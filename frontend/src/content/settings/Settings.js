import "css/settings/settings.css"
import {Link, useParams} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {SettingsSecurity} from "./security/SettingsSecurity";
import {SettingsAccount} from "./account/SettingsAccount";
import ModalContent from "modal/content/ModalContent";
import {useTranslation} from "react-i18next";

export function Settings({location, navigate}) {
  const params = useParams();
  const userInfo = useSelector(state => state.userReducer);
  let root = "settings";
  if (params["root"] !== undefined) {
    root = params.root
  }
  const [modalVisible, setModalVisible] = useState(false)

  const dispatch = useDispatch();
  const {t} = useTranslation();

  const openModal = () => {
    setModalVisible(true)
  }
  const closeModal = () => {
    setModalVisible(false)
  }

  return (
      <div className="container settings_container mt-5">
        <div className="row justify-content-center">
          <div className="col-12 col-md-10">
            <h1 className="text-start settings_title">Settings</h1>
            <div className="tabs">
              <PrivacyNav navigate={navigate} root={root}/>
            </div>

            <div className="col-12 col-md-12" id="settings">
              <ul className="list-group list-group-flush">
                {
                  <>
                    <SettingsContents
                        dispatch={dispatch}
                        openModal={openModal} root={root} userInfo={userInfo}/>
                    <ModalContent closeModal={closeModal}
                                  modalVisible={modalVisible}/>
                  </>
                }
              </ul>
            </div>
          </div>
        </div>
      </div>
  )
}

function SettingsContents({
  root,
  userInfo,
  dispatch,
  openModal,
}) {
  if (root === "security") {
    return (
        <>
          <SettingsSecurity dispatch={dispatch} openModal={openModal} userInfo={userInfo}/>
        </>
    );
  } else {
    return (
        <>
          <SettingsAccount userInfo={userInfo}/>
        </>
    );
  }
}

function PrivacyNav(props) {
  const prevRootRef = useRef({root: props.root});
  const [active, setActive] = useState({
    settings: "",
    security: "",
    history: "",
    notifications: ""
  });
  useEffect(() => {
    let prevRoot = prevRootRef.current.root;
    console.log("prevRoot : " + prevRoot)
    setActive(
        {...active, [prevRootRef.current.root]: "", [props.root]: "active"});
    if (prevRoot !== props.root) {
      prevRootRef.current.root = props.root;
    }
  }, [props.root])
  return (
      <>
        <ul className="nav nav-tabs">
          <li className="nav-item">
            <Link value="settings"
                  className={"nav-link link_font_color " + active.settings}
                  to="/settings">Account</Link>
          </li>
          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.security}
                  to="/settings/security">Security</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.history}
                  to="/settings/history">Manage History</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.notifications}
                  to="/settings/notifications">Notifications</Link>
          </li>
        </ul>
      </>
  )
}