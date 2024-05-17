package sky.Sss.global.redis.dto;


public class RedisKeyDto {
    public static final String REDIS_SESSION_KEY = "spring:session:sessions:";

    public static final String REDIS_TAGS_KEY = "spring:tags:";
    public static final String REDIS_WS_SESSION_KEY = "spring:session:ws:sessions:";
    public static final String REDIS_USER_CACHE_TOKEN_KEY = "spring:user:cache:token";
    public static final String REDIS_USER_WS_LIST_SESSION_KEY = "spring:user:ws:list:session:";

    public static final String REDIS_REMEMBER_KEY = "spring:session:remember:";
    public static final String REDIS_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    public static final String REDIS_LOGIN_KEY = "spring:redisToken:redis:";
    public static final String REDIS_TRACK_LIKES_MAP_KEY = "spring:track:likes:";

    public static final String REDIS_TRACK_REPLY_LIKES_MAP_KEY = "spring:track:reply:likes:";
    public static final String REDIS_PLY_REPLY_LIKES_MAP_KEY = "spring:ply:reply:likes:";
    public static final String REDIS_PLY_LIKES_MAP_KEY = "spring:ply:likes:";

    public static final String REDIS_PLY_POSITION_MAP_KEY = "spring:ply:position:";

    public static final String REDIS_USERS_INFO_MAP_KEY = "spring:users:info:";

    public static final String REDIS_USER_IDS_MAP_KEY = "spring:user:ids:";
    public static final String REDIS_USER_EMAILS_MAP_KEY = "spring:user:email:";
    public static final String REDIS_USER_PK_ID_MAP_KEY = "spring:user:pk:id:";
    public static final String REDIS_USER_NAMES_MAP_KEY = "spring:user:usernames:";

    public static final String REDIS_TRACKS_INFO_MAP_KEY = "spring:tracks:info:";
    public static final String REDIS_TRACKS_UID_SET_KEY = "spring:track:uids:";


    public static final String REDIS_TRACK_REPOST_MAP_KEY = "spring:track:repost:";
    public static final String REDIS_PLY_REPOST_MAP_KEY = "spring:ply:repost:";


    public static final String REDIS_PLY_REPLY_MAP_KEY = "spring:ply:reply:";

    public static final String REDIS_TRACK_REPLY_MAP_KEY = "spring:track:reply:";
    public static final String REDIS_TRACK_REPLY_LIKES_KEY = "spring:reply:likes:";

    // 특정 유저가 팔로우 하고 있는 유저들의 목록
    public static final String REDIS_USER_FOLLOWING_MAP_KEY = "spring:user:following:";

    // 특정 유저를 팔로우 하고 있는 유저들의 목록
    public static final String REDIS_USER_FOLLOWER_MAP_KEY = "spring:user:follower:";


    public static final String REDIS_PUSH_MSG_LIST_KEY = "spring:push:msg:list:";
    public static final String REDIS_USER_KEY = "USER_ID";


    // 특정 유저가 팔로우 하는 있는 유저들의 총 합
    public static final String REDIS_USER_FOLLOWING_TOTAL_MAP_KEY = "spring:user:following:total";
    public static final String REDIS_USER_TOTAL_LENGTH_MAP_KEY = "spring:user:total:length";

    //
    public static final String REDIS_USER_FOLLOWER_TOTAL_MAP_KEY = "spring:user:follower:total";

    public static final String REDIS_TRACK_LIKES_TOTAL_MAP_KEY = "spring:track:likes:total";
    public static final String REDIS_PLY_LIKES_TOTAL_MAP_KEY = "spring:ply:likes:total";
    public static final String REDIS_REPLY_LIKES_TOTAL_MAP_KEY = "spring:reply:likes:total";




}
