import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LIKES} from "utill/api/ApiEndpoints";

export default async function TrackLikeApi(id) {
  return authApi.post(TRACKS_LIKES + id);
}