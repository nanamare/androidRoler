package com.buttering.roler.net.baseservice;

import com.buttering.roler.VO.MyInfoDAO;
import com.buttering.roler.VO.Schedule;
import com.buttering.roler.VO.User;
import com.buttering.roler.net.serialization.RolerResponse;
import com.buttering.roler.util.SharePrefUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by kinamare on 2016-12-17.
 */

public class UserService extends BaseService {

	public static boolean DEBUG = false;

	private String userId;
	private String name;

	public UserService() {
		super(UserAPI.class);
	}

	@Override
	public UserAPI getAPI() {
		return (UserAPI) super.getAPI();
	}


	public Observable<User> signUp(User user) {


		return Observable.create(subscriber -> {

			getAPI()
					.signUp(user.getName(), user.getEmail(), user.getPassword())
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
							subscriber.onCompleted();
							subscriber.unsubscribe();

						}

						@Override
						public void onError(Throwable e) {
							e.printStackTrace();
							subscriber.onError(e);
						}

						@Override
						public void onNext(ResponseBody responseBody) {

							try {
								String result = parseParams(responseBody.string());
								if (result.equals("true")) {
									MyInfoDAO.getInstance().setUserId(userId);
									MyInfoDAO.getInstance().setName(name);
									MyInfoDAO.getInstance().saveAccountInfo(userId, user.getEmail(), user.getPassword(), user.getName(), "NULL");
									subscriber.onNext(user);

								} else {
									subscriber.onError(new Throwable());
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						private String parseParams(String json) {
							JsonObject ja = new JsonParser().parse(json).getAsJsonObject();
							String result = ja.get("result").getAsString();
							userId = ja.get("id").getAsString();
							name = ja.get("name").getAsString();

							return result;
						}

					});

		});
	}

	public Observable<RolerResponse> setProfilePhotos(String profileImgUrl) {

		User userInfo = MyInfoDAO.getInstance().getMyUserInfo();
		userInfo.setPicture_url(profileImgUrl);

		RolerRequest req = new RolerRequest.Builder()
				.setParam(new Gson().toJson(userInfo)).build();

		return getAPI().setProfilePhotos(req)
				.subscribeOn(Schedulers.io());
	}


	public Observable<String> isDuplicateEmail(String email) {

		return Observable.create(subscriber -> {
			getAPI()
					.isDuplicateEmail(email)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
							subscriber.onCompleted();
							subscriber.unsubscribe();
						}

						@Override
						public void onError(Throwable e) {

						}

						@Override
						public void onNext(ResponseBody responseBody) {
							try {
								String result = parseParams(responseBody.string());
								if (result.equals("true")) {
									subscriber.onNext(result);
								} else {
									subscriber.onError(new Throwable());
								}
							} catch (IOException e) {
								e.printStackTrace();
							}

						}

						private String parseParams(String json) {
							JsonObject ja = new JsonParser().parse(json).getAsJsonObject();
							String param = ja.get("result").getAsString();
							return param;
						}

					});

		});
	}

	public Observable<String> signIn(String email, String pwd) {
		return Observable.create(subscriber -> {
			getAPI().signIn(email, pwd)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
							subscriber.onCompleted();
							subscriber.unsubscribe();

						}

						@Override
						public void onError(Throwable e) {
							subscriber.onError(new Throwable("서버에 문제가 있습니다."));
						}

						@Override
						public void onNext(ResponseBody responseBody) {
							try {
								String result = parseParams(responseBody.string());
								if (result.equals("true")) {
									subscriber.onCompleted();

								} else {
									subscriber.onError(new Throwable("이메일 또는 비밀번호를 확인해주세요"));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}

						}

						private String parseParams(String json) {
							JsonObject ja = new JsonParser().parse(json).getAsJsonObject();
							String result = ja.get("result").getAsString();
							String name = ja.get("name").getAsString();
							String email = ja.get("email").getAsString();
							String id = ja.get("id").getAsString();
							String picUrl = ja.get("imageUrl").getAsString();
							String accessToken = ja.get("access_token").getAsString();
							SharePrefUtil.putSharedPreference("accessToken",accessToken);

							MyInfoDAO.getInstance().loginAccountInfo(id, email, name, picUrl);
							return result;
						}


					});

		});

	}

	public Observable<Void> registerToken(String token, String email) {
		return Observable.create(subscriber -> {
			getAPI().registerToken(token, email)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {

						}

						@Override
						public void onError(Throwable e) {
							e.printStackTrace();
						}

						@Override
						public void onNext(ResponseBody responseBody) {

							try {
								String result = responseBody.string();
								if (getStatusResult(result) == "true") {
									subscriber.onNext(null);

								} else {

									subscriber.onError(new Throwable());

								}

							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					});

		});
	}


	public interface UserAPI {

		@FormUrlEncoded
		@POST("/sign/in")
		Observable<ResponseBody> signIn(@Field("email") String email, @Field("password") String password);

		@GET("/sign/duplitcation")
		Observable<ResponseBody> isDuplicateEmail(@Query("email") String email);

		@PUT("/sign/photos")
		Observable<RolerResponse> setProfilePhotos(@Body RolerRequest req);


		@FormUrlEncoded
		@POST("/sign/up")
		Observable<ResponseBody> signUp(@Field("name") String name, @Field("email") String email, @Field("password") String password);

		@FormUrlEncoded
		@PUT("/fcm/register")
		Observable<ResponseBody> registerToken(@Field("token") String token, @Field("email") String email);

	}
}
