package com.example.vivek.sampleproject.network;

import com.example.vivek.sampleproject.data.Properties;
import com.example.vivek.sampleproject.model.Feature;
import com.example.vivek.sampleproject.model.Feed;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Vivek on 17-01-2018.
 */

public class TaskLoader {

    private static TaskLoader INSTANCE;

    private List<Properties> propertiesList = new ArrayList<>();
    //private List<Properties> properties = null;

    public static TaskLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TaskLoader();
        }
        return INSTANCE;
    }

    public void loadEarthquakes(final Callback<List<Properties>> callback) {
        if (!propertiesList.isEmpty()) {
            callback.getResult(propertiesList);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CallRetrofitApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        CallRetrofitApi retrofitApi = retrofit.create(CallRetrofitApi.class);

        final Call<Feed> feedCall = retrofitApi.getResult();

        feedCall.enqueue(new retrofit2.Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                List<Feature> features = response.body().getFeatures();
                for (int i = 0; i< features.size(); i++){
                    double magnitude = features.get(i).getProperties().getMag();
                    String location = features.get(i).getProperties().getPlace();
                    String detailsUrl = features.get(i).getProperties().getDetail();
                    long dateTime = features.get(i).getProperties().getTime();
                    Properties properties = new Properties(magnitude, location, dateTime, detailsUrl);
                    propertiesList.add(properties);
                }
                callback.getResult(propertiesList);
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Timber.d("Proble fetching JSON data %s", t.getMessage());
            }
        });
    }
}
