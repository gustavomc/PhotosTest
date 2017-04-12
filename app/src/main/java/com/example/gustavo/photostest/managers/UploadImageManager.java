package com.example.gustavo.photostest.managers;

import android.util.Log;

import com.example.gustavo.photostest.PhotosTestApplication;
import com.example.gustavo.photostest.controllers.ApiController;
import com.example.gustavo.photostest.events.PostBinEvent;
import com.example.gustavo.photostest.models.ApiResponse;
import com.example.gustavo.photostest.models.BinObject;
import com.example.gustavo.photostest.models.ListItem;
import com.example.gustavo.photostest.models.PostBinResponse;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class UploadImageManager {
    private UploadImageManager(){ }

    public static void load(BinObject data){

        Call<PostBinResponse> callResponse = ApiController.getApi().postBin(data);

        callResponse.enqueue(new Callback<PostBinResponse>() {

            @Override
            public void onResponse(Call<PostBinResponse> call, Response<PostBinResponse> response) {
                if (response.isSuccessful()) {
                    //obtener respuesta
                    PostBinResponse data = response.body();

                    //guardar ID recibido del servicio en local con Realm
                    ListItem item = new ListItem();
                    int index = data.getUri().lastIndexOf('/') + 1;
                    item.setId(data.getUri().substring(index, data.getUri().length()));
                    item.setIncrement(ListItem.getNextKey());
                    item.setName("Foto " + ListItem.getNextKey());

                    Realm realm = PhotosTestApplication.getRealmInstance();
                    realm.beginTransaction();
                    realm.copyToRealm(item);
                    realm.commitTransaction();

                    //enviar evento success
                    PhotosTestApplication.getBusProvider().post( new PostBinEvent(true, data, null) );
                } else {
                    String message = "error";
                    ApiResponse error = ApiController.getResponse( response.errorBody() );
                    int code = response.code();
                    if(error != null)
                        message = error.getMessage();

                    Log.e("ERROR", code + ": " + message);

                    PhotosTestApplication.getBusProvider().post( new PostBinEvent(false, null, message) );
                }
            }

            @Override
            public void onFailure(Call<PostBinResponse> call, Throwable t) {
                PhotosTestApplication.getBusProvider().post( new PostBinEvent(false, null, t.getMessage()) );
            }

        });
    }
}
