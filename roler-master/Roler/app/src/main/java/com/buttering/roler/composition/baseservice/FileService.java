package com.buttering.roler.composition.baseservice;

import com.buttering.roler.VO.MyInfoDAO;
import com.buttering.roler.VO.User;
import com.buttering.roler.composition.serialization.RolerResponse;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Created by kinamare on 2016-12-29.
 */

public class FileService extends BaseService {

	public FileService() {
		super(FileAPI.class);
	}

	@Override
	public FileAPI getAPI() {
		return (FileAPI) super.getAPI();
	}


	public Observable<ResponseBody> uploadProfileImg(MultipartBody.Part file) {

		// finally, execute the request
		return getAPI()
				.uploadProfileImg(
						MyInfoDAO.getInstance().getMyUserInfo().getEmail(),
						"profile",
						file)
				.subscribeOn(Schedulers.io());
	}



	private interface FileAPI {
		@Multipart
		@POST("/files")
		Observable<RolerResponse> uploadFile(
				@Query("userId") String userId,
				@Query("fileType") String fileType,
				@Part MultipartBody.Part file);


		@Multipart
		@POST("/files")
		Observable<ResponseBody> uploadProfileImg(
				@Query("email") String email,
				@Query("fileType") String fileType,
				@Part MultipartBody.Part file);


	}

}
