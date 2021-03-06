package com.buttering.roler.net.baseservice;

import com.buttering.roler.VO.Todo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by kinamare on 2017-01-15.
 */

public class TodoService extends BaseService {

	public TodoService() {
		super(TodoApi.class);
	}

	@Override
	public TodoApi getAPI() {
		return (TodoApi) super.getAPI();
	}

	public Observable<List<Todo>> getTodoList(int roleId) {

		return Observable.create(subscriber -> {
			getAPI().getTodoList(roleId)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
						}

						@Override
						public void onError(Throwable e) {
							subscriber.onError(e);
						}

						@Override
						public void onNext(ResponseBody responseBody) {
							try {
								String result = responseBody.string();
								if (getStatusResult(result) == "true") {
									List<Todo> todoList = parseParams(result);
									subscriber.onNext(todoList);

								} else {
									List<Todo> emptyTodo = new ArrayList<>();
									subscriber.onNext(emptyTodo);
								}

							} catch (IOException e) {
								e.printStackTrace();
							}

						}

						private List<Todo> parseParams(String json) {
							ArrayList<Todo> todo = new ArrayList<>();

							try {
								JSONObject object = new JSONObject(json);
								JSONArray jsonArray = new JSONArray(object.getString("params"));
								String todoJson = jsonArray.toString();
								todo.addAll(new Gson().fromJson(todoJson, Todo.getListType()));
							} catch (JSONException e) {
								e.printStackTrace();
							}


							return todo;
						}

					});


		});

	}

	;

	public Observable<Integer> addTodoList(String content, int todoOrder
			, String todoDate, int role_id, boolean isDone) {

		return Observable.create(subscriber -> {
			getAPI().addTodoList(content, todoOrder, todoDate, role_id, isDone)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
							subscriber.onCompleted();
							subscriber.unsubscribe();
						}

						@Override
						public void onError(Throwable e) {
							subscriber.onError(e);
						}

						@Override
						public void onNext(ResponseBody responseBody) {
							try {
								String result = responseBody.string();
								if (getStatusResult(result) == "true") {
									int id = parseParams(result);
									subscriber.onNext(id);

								} else {
									subscriber.onError(new Throwable());
								}

							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						private int parseParams(String json) {

							JsonObject ja = new JsonParser().parse(json).getAsJsonObject();
							String result = ja.get("id").getAsString();
							int id = Integer.valueOf(result);

							return id;

						}

					});
		});
	}


	public Observable<Void> deleteTodo(int id) {

		return Observable.create(subscriber -> {
			getAPI().deleteTodo(id)
					.subscribeOn(Schedulers.io())
					.subscribe(new Subscriber<ResponseBody>() {
						@Override
						public void onCompleted() {
							unsubscribe();

						}

						@Override
						public void onError(Throwable e) {
							onError(e);
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

	public Observable<Void> setDone(int todo_id, Boolean isDone) {
		String done;
		if (isDone.equals(true)) {
			done = "true";
		} else {
			done = "false";
		}
		return Observable.create(subscriber -> {
			getAPI().setDone(todo_id, done)
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
									subscriber.onError(new Throwable("false"));
								}

							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					});

		});

	}


	public interface TodoApi {

		@GET("/todo/read")
		Observable<ResponseBody> getTodoList(@Query("role_id") int roleId);

		@FormUrlEncoded
		@POST("/todo/create")
		Observable<ResponseBody> addTodoList(@Field("content") String content
				, @Field("todoOrder") int todoOrder
				, @Field("todoDate") String todoDate
				, @Field("role_id") int role_id
				, @Field("isDone") boolean isDone);

		@DELETE("/todo/delete")
		Observable<ResponseBody> deleteTodo(@Query("id") int id);

		@PUT("/todo/done/{todoId}")
		Observable<ResponseBody> setDone(@Path("todoId") int todoId, @Query("isDone") String isDone);

	}

}
