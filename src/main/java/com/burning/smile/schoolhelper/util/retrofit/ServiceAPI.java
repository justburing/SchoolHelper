package com.burning.smile.schoolhelper.util.retrofit;


import com.alibaba.fastjson.JSONObject;
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.data.ForumDetailBean;
import com.burning.smile.schoolhelper.data.ForumListBean;
import com.burning.smile.schoolhelper.data.FunkCommentListBean;
import com.burning.smile.schoolhelper.data.FunkListBean;
import com.burning.smile.schoolhelper.data.MyExpressDetailBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ServiceAPI {

    //登陆
    @FormUrlEncoded
    @POST("users/login")
    Observable<UserInfoBean> login(@Field("username") String userName, @Field("password") String userPassword);

    //注册
    @FormUrlEncoded
    @POST("users/register")
    Observable<UserInfoBean.UserBean> register(@Field("username") String userName, @Field("password") String userPassword, @Field("email") String email, @Field("nickname") String nickname);

    //修改用户头像
    @POST("users/avatar")
    Observable<UserInfoBean.UserBean> modifyUserAvatar(@Header("X-Auth-Token") String token, @Body RequestBody Body);

    //获取快递列表
    @GET("expresses")
    Observable<ExpressListBean> getExpress(@Header("X-Auth-Token") String token, @Query("start") String start, @Query("limit") String limit, @Query("startDate") String startDate, @Query("endDate") String endDate, @Query("type") String type, @Query("status") String status, @Query("keywordType") String keywordType, @Query("keyword") String keyword, @Query("is_urgent") String is_urgent);

    //获取快递详情
    @GET("expresses/{id}")
    Observable<ExpressListBean.Express> getExpressDetail(@Header("X-Auth-Token") String token, @Path("id") String id);

    @POST("expresses/{expressId}/apply")
    Observable<JSONObject> dealExpress(@Header("X-Auth-Token") String token, @Path("expressId") String expressId);

    //注册
    @FormUrlEncoded
    @POST("expresses")
    Observable<JsonObject> postExpress(@Header("X-Auth-Token") String token, @Field("title") String title, @Field("detail") String detail, @Field("offer") String offer, @Field("type") String type, @Field("is_urgent") String is_urgent);
    //获取旧货的列表

    /**
     * start false string 0 起始页
     * limit false string 10 每页数量
     * orderby false string updated_time desc 排序（目前支持created_time, updated_time, post_num, hits, price）排序
     * title false string 根据标题检索
     * category_id false string 根据分类检索
     * status false string 1 (0:未发布， 1:已发布，2: 已关闭)
     */
    @GET("goods")
    Observable<FunkListBean> getFunk(@Header("X-Auth-Token") String token, @Query("start") String start, @Query("limit") String limit, @Query("orderby") String orderby, @Query("title") String title, @Query("category_id") String category_id, @Query("status") String status);

    //获取货物详情
    @GET("goods/{id}")
    Observable<FunkListBean.Funk> getFunkDetail(@Header("X-Auth-Token") String token, @Path("id") String id);

    //添加货物
    @FormUrlEncoded
    @POST("goods")
    Observable<JSONObject> addFunk(@Header("X-Auth-Token") String token, @FieldMap Map<String, String> fieldMap);

    @GET("goods/{id}/posts")
    Observable<FunkCommentListBean> getFunkComments(@Header("X-Auth-Token") String token, @Path("id") String id);

    @FormUrlEncoded
    @POST("goods/{postId}/posts")
    Observable<FunkCommentListBean.PostComment> postFunkComment(@Header("X-Auth-Token") String token, @Path("postId") String id, @Field("content") String content, @Field("to_user_id") String to_user_id);


    @POST("goods/{postId}/like")
    Observable<JSONObject> likeFunk(@Header("X-Auth-Token") String token, @Path("postId") String id);

    @POST("goods/{postId}/cancelLike")
    Observable<JSONObject> cancelLikeFunk(@Header("X-Auth-Token") String token, @Path("postId") String id);


    //上传文件
    @POST("file/upload")
    Observable<JSONObject> uploadFile(@Header("X-Auth-Token") String token, @Body RequestBody Body);


    @GET("threads")
    Observable<ForumListBean> getForums(@Header("X-Auth-Token") String token, @Query("start") String start, @Query("limit") String limit, @Query("orderby") String orderby, @Query("title") String title, @Query("group_id") String group_id);


    @FormUrlEncoded
    @POST("threads")
    Observable<JSONObject> addForum(@Header("X-Auth-Token") String token, @FieldMap Map<String, String> fieldMap);


    @GET("threads/{id}")
    Observable<ForumDetailBean> getForum(@Header("X-Auth-Token") String token, @Path("id") String id);


    @GET("threads/{id}/post")
    Observable<ForumListBean.Forum> getForumComments(@Header("X-Auth-Token") String token, @Path("id") String id, @Query("post_id") String post_id);

    @FormUrlEncoded
    @POST("threads/{postId}/post")
    Observable<ForumDetailBean.CommentsBean> postForumComment(@Header("X-Auth-Token") String token, @Path("postId") String id, @Field("content") String content, @Field("from_user_id") String from_user_id);

    @FormUrlEncoded
    @POST("threads/{postId}/post")
    Observable<ForumDetailBean.CommentsBean.ItemCommentsBean> postForumItemComment(@Header("X-Auth-Token") String token, @Path("postId") String id, @Field("content") String content, @Field("post_id") String postId, @Field("from_user_id") String from_user_id);


    @GET("my/expresses")
    Observable<ExpressListBean> getMyExpress(@Header("X-Auth-Token") String token, @Query("start") String start, @Query("limit") String limit, @Query("type") String type);


    @GET("my/publish_expresses/{id}")
    Observable<MyExpressDetailBean> getMyExpressDetail(@Header("X-Auth-Token") String token, @Path("id") String id);

    @FormUrlEncoded
    @POST("my/publish_expresses/{id}/auth")
    Observable<JSONObject> authExpressDeal(@Header("X-Auth-Token") String token, @Path("id") String id, @Field("user_id") String user_id);


    @POST("my/recive_expresses/{expressId}")
    Observable<JSONObject> comfirmDelivery(@Header("X-Auth-Token") String token, @Path("expressId") String id);


    @POST("my/publish_expresses/{expressId}")
    Observable<JSONObject> comfirmFinishDeal(@Header("X-Auth-Token") String token, @Path("expressId") String id);

    @FormUrlEncoded
    @POST("collections")
    Observable<JSONObject> collect(@Header("X-Auth-Token") String token, @Field("object_type") String object_type, @Field("object_id") String object_id);

    @FormUrlEncoded
    @POST("collections/cancel")
    Observable<JSONObject> cancelCollect(@Header("X-Auth-Token") String token, @Field("object_type") String object_type, @Field("object_id") String object_id);

    @GET("collections")
    Observable<JSONObject> getCollections(@Header("X-Auth-Token") String token, @Query("object_type") String object_type);
}
