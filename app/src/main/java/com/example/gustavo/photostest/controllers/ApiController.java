package com.example.gustavo.photostest.controllers;

import android.support.annotation.NonNull;

import com.example.gustavo.photostest.models.ApiResponse;
import com.example.gustavo.photostest.models.BinObject;
import com.example.gustavo.photostest.models.PostBinResponse;
import com.example.gustavo.photostest.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class ApiController {

    public static final String TAG = ApiController.class.getSimpleName();
    private static Api sApi = null;
    private static Gson sGson = null;

    ApiController() {
        throw new RuntimeException("Don't!");
    }

    public static void init(@NonNull String baseUrl) {
        if (sApi != null) {
            throw new IllegalStateException("Api already initialized");
        }
        GsonUtils.ExcludeFieldsWithoutSerializedName efwsn = new GsonUtils.ExcludeFieldsWithoutSerializedName();
        sGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .addSerializationExclusionStrategy(efwsn)
                .addDeserializationExclusionStrategy(efwsn)
                .registerTypeHierarchyAdapter(byte[].class, new GsonUtils.ByteArrayToBase64Serializer())
                .setLenient()
                .create();
        GsonConverterFactory gsonConverter = GsonConverterFactory.create(sGson);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(gsonConverter)
                .build();

        sApi = retrofit.create(Api.class);
    }

    public static Api getApi() {
        return sApi;
    }

    public static Gson getGson() {
        return sGson;
    }

    public static ApiResponse getResponse(ResponseBody response){
        try {
            return sGson.fromJson(response.string(), ApiResponse.class);
        }
        catch (Exception e){
            return null;
        }
    }

    public interface Api {

        @GET("bins/{binId}")
        Call<BinObject> getBin(
                @Path("binId") String id
        );

        @Headers({"contentType: application/json; charset=utf-8", "Content-Type: application/json"})
        @POST("bins")
        Call<PostBinResponse> postBin(@Body BinObject data);

    }
}
