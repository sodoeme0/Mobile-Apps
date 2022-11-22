package edu.uncc.weather;

public class Forecast {
    String date, temp, tempMax, tempMin, humidity, description, icon;


    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", temp='" + temp + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", humidity='" + humidity + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

}
