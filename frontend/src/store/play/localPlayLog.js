import {createSlice} from '@reduxjs/toolkit';
import {saveToLocalStorage} from "utill/function";
import {LOCAL_PLY_LOG} from "utill/enum/localKeyEnum";
import {RESET_ALL} from "../actions/Types";

const initialState = {
  key: LOCAL_PLY_LOG,
  item: [-1,0,0] // 트랙아이디,플레이리스트 index,시작 날짜
}
const localPlayLog = createSlice({
  name: "localPlayLog",
  initialState: initialState,
  reducers: {
    changePlayLog(state, action) {
      const id = action.payload.id;
      const index = action.payload.index;
      const startTime = action.payload.startTime;
      state.item[0] = id;
      state.item[1] = index;
      state.item[2] = startTime;
      saveToLocalStorage(state);
    }
  },extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let playLogActions = {
  changePlayLog: localPlayLog.actions.changePlayLog,
};
export default localPlayLog.reducer;