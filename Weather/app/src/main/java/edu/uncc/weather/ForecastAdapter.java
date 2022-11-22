package edu.uncc.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends ArrayAdapter<Forecast> {
    public ForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView== null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_row_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.dateTime = convertView.findViewById(R.id.textViewDateTime);
            viewHolder.temp = convertView.findViewById(R.id.textViewTemp);
            viewHolder.tempMin = convertView.findViewById(R.id.textViewTempMin);
            viewHolder.tempMax = convertView.findViewById(R.id.textViewTempMax);
            viewHolder.humidity = convertView.findViewById(R.id.textViewHumidity);
            viewHolder.desc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.icon = convertView.findViewById(R.id.imageViewWeatherIcon);
            convertView.setTag(viewHolder);
        }
        Forecast forecast = getItem(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.dateTime.setText(forecast.date);
        viewHolder.temp.setText(forecast.temp+"F");
        viewHolder.tempMin.setText(forecast.tempMin+"F");
        viewHolder.tempMax.setText(forecast.tempMax+"F");
        viewHolder.humidity.setText(forecast.humidity+"%");
        viewHolder.desc.setText(forecast.description);
        Picasso.get().load("https://openweathermap.org/img/wn/"+forecast.icon+"@2x.png")
                .into(viewHolder.icon);

        return convertView;
    }

    private static class ViewHolder{
        TextView dateTime, temp, tempMin, tempMax, humidity, desc;
        ImageView icon;
    }
}
