package edu.uncc.weather;

public class Weather {
    String temp, tempMax, tempMin,
    description, humidity, windSpeed, windDegree, cloudiness, icon;
    @Override
    public String toString() {
        return "Weather{" +
                "temp='" + temp + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", description='" + description + '\'' +
                ", humidity='" + humidity + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", windDegree='" + windDegree + '\'' +
                ", cloudiness='" + cloudiness + '\'' +
                '}';
    }
}
