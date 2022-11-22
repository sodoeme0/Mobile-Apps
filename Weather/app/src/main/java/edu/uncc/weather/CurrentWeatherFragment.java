package edu.uncc.weather;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CurrentWeatherFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private DataService.City mCity;
    FragmentCurrentWeatherBinding binding;
    private final String API_KEY = "b9f641a3ff497e00de5ebf498c0bf87a";
    private final OkHttpClient client = new OkHttpClient();
    Weather weather;
    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if(context instanceof CurrentWeatherLitener){
            listener = (CurrentWeatherLitener) context;
        }
        super.onAttach(context);
    }

    public interface CurrentWeatherLitener{
        void launchForecast(DataService.City city);
    }
    CurrentWeatherLitener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        String lat = String.valueOf(mCity.getLat());
        String lon = String.valueOf(mCity.getLon());
        binding.textViewCityName.setText(mCity.getCity());

        binding.buttonCheckForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.launchForecast(mCity);
            }
        });

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder()
                .addQueryParameter("lat", lat)
                .addQueryParameter("lon", lon)
                .addQueryParameter("units", "imperial")
                .addQueryParameter("appid", API_KEY).build()
                ;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                }

                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                          weather = new Weather();
                        JSONObject mainObject = jsonObject.getJSONObject("main");
                        JSONArray weatherArr = jsonObject.getJSONArray("weather");
                        JSONObject weatherObj = (JSONObject) weatherArr.get(0);
                        JSONObject windObject = jsonObject.getJSONObject("wind");
                        JSONObject cloudObject = jsonObject.getJSONObject("clouds");


                        weather.temp = mainObject.getString("temp");
                        weather.tempMax = mainObject.getString("temp_max");
                        weather.tempMin = mainObject.getString("temp_min");
                        weather.humidity = mainObject.getString("humidity");
                        weather.description = weatherObj.getString("description");
                        weather.windSpeed = windObject.getString("speed");
                        weather.windDegree = windObject.getString("deg");
                        weather.cloudiness = cloudObject.getString("all");
                        weather.icon = weatherObj.getString("icon");

                        Log.d("d", "onResponse: "+weather.icon);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            binding.textViewTemp.setText(weather.temp +" F");
                            binding.textViewTempMax.setText(weather.tempMax+" F");
                            binding.textViewTempMin.setText(weather.tempMin+" F");
                            binding.textViewDesc.setText(weather.description);
                            binding.textViewHumidity.setText(weather.humidity+" %");
                            binding.textViewWindSpeed.setText(weather.windSpeed+" mph");
                            binding.textViewWindDegree.setText(weather.windDegree+" degrees");
                            binding.textViewCloudiness.setText(weather.cloudiness+" %");
                            Picasso.get().load("https://openweathermap.org/img/wn/"+weather.icon+"@2x.png")
                                    .into(binding.imageViewWeatherIcon);

                        }
                    });
                }
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");
    }
}