package edu.uncc.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class WeatherForecastFragment extends Fragment {



    private static final String ARG_PARAM2 = "param2";
    private final String API_KEY = "b9f641a3ff497e00de5ebf498c0bf87a";
    private final OkHttpClient client = new OkHttpClient();
    ArrayList<Forecast> forecasts = new ArrayList<>();
    private DataService.City city;
    ListView listView;
    ForecastAdapter adapter;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }


    public static WeatherForecastFragment newInstance(DataService.City param1) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            city = (DataService.City) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        listView =view.findViewById(R.id.listView);

        TextView cityName = view.findViewById(R.id.textViewCityName);
        cityName.setText(city.getCity());
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("lat", String.valueOf(city.getLat()))
                .addQueryParameter("lon", String.valueOf(city.getLon()))
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
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i=0; i<jsonArray.length(); i++) {
                            Forecast forecast = new Forecast();
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            JSONObject mainObject = tempObject.getJSONObject("main");
                            JSONArray weatherArr = tempObject.getJSONArray("weather");
                            JSONObject weatherObj = (JSONObject) weatherArr.get(0);
                            JSONObject windObject = tempObject.getJSONObject("wind");
                            JSONObject cloudObject = tempObject.getJSONObject("clouds");

                            forecast.temp = mainObject.getString("temp");
                            forecast.tempMax = mainObject.getString("temp_max");
                            forecast.tempMin = mainObject.getString("temp_min");
                            forecast.humidity = mainObject.getString("humidity");
                            forecast.description = weatherObj.getString("description");
                            forecast.icon = weatherObj.getString("icon");
                            forecast.date = tempObject.getString("dt_txt");
                            forecasts.add(forecast);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ForecastAdapter(getContext(), R.layout.forecast_row_item, forecasts);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            }

        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Weather Forecast");

    }
}