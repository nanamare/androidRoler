package com.buttering.roler.composition.baseservice;

import com.buttering.roler.VO.Role;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by kinamare on 2017-01-13.
 */

public class RoleService extends BaseService {

	public RoleService() {
		super(RoleApi.class);
	}

	@Override
	public RoleApi getAPI() {
		return (RoleApi) super.getAPI();
	}

	public Observable<List<Role>> getRoleContent(int id) {

		return Observable.create(subscriber -> {
			getAPI().getRoleContent(id)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {

						}

						@Override
						public void onError(Throwable e) {

						}

						@Override
						public void onNext(ResponseBody responseBody) {
							try {
								String result = responseBody.string();
								if (getStatusResult(result) == "true") {
									List<Role> roleList = parseParams(result);
									subscriber.onNext(roleList);

								}

							} catch (IOException e) {
								e.printStackTrace();
							}


						}

						private List<Role> parseParams(String json) {
							ArrayList<Role> roleList = new ArrayList<>();

							try {
								JSONObject object = new JSONObject(json);
								JSONArray  jsonArray = new JSONArray(object.getString("params"));
								String roleJson = jsonArray.toString();
								roleList.addAll(new Gson().fromJson(roleJson, Role.getListType()));

							} catch (JSONException e) {
								e.printStackTrace();
							}


							return roleList;
						}

					});

		});
	}


	public interface RoleApi {

		@GET("/role/read")
		Observable<ResponseBody> getRoleContent(@Query("user_id") int id);


	}
}