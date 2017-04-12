package com.example.gustavo.photostest.managers;

import android.util.Log;

import com.example.gustavo.photostest.PhotosTestApplication;
import com.example.gustavo.photostest.controllers.ApiController;
import com.example.gustavo.photostest.events.GetBinEvent;
import com.example.gustavo.photostest.models.ApiResponse;
import com.example.gustavo.photostest.models.BinObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class GetImageManager {
    private GetImageManager(){ }

    public static void load(String id){

        Call<BinObject> callResponse = ApiController.getApi().getBin(id);

        callResponse.enqueue(new Callback<BinObject>() {

            @Override
            public void onResponse(Call<BinObject> call, Response<BinObject> response) {
                if (response.isSuccessful()) {
                    BinObject data = response.body();
                    PhotosTestApplication.getBusProvider().post( new GetBinEvent(true, data, null) );
                } else {
                    String message = "error";
                    ApiResponse error = ApiController.getResponse( response.errorBody() );
                    int code = response.code();
                    if(error != null)
                        message = error.getMessage();

                    Log.e("ERROR", code + ": " + message);

                    PhotosTestApplication.getBusProvider().post( new GetBinEvent(false, null, message) );
                }
            }

            @Override
            public void onFailure(Call<BinObject> call, Throwable t) {
                PhotosTestApplication.getBusProvider().post( new GetBinEvent(false, null, t.getMessage()) );
            }

        });

    }
}
