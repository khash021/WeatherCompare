package tech.khash.weathercompare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tech.khash.weathercompare.R;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.HelperFunctions;

public class WeatherListAdapterToday extends RecyclerView.Adapter<WeatherListAdapterToday.WeatherViewHolder> {

    //list of data
    private final ArrayList<Weather> weatherArrayList;
    //inflater used for creating the view
    private LayoutInflater inflater;
    //context
    private Context context;
    private boolean isDay;

    //constructor
    public WeatherListAdapterToday (Context context, ArrayList<Weather> weatherArrayList, boolean isDay) {
        this.context = context;
        this.isDay = isDay;
        this.weatherArrayList = weatherArrayList;
        inflater = LayoutInflater.from(context);
    }//constructor


    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_weather_today, parent, false);
        return new WeatherListAdapterToday.WeatherViewHolder(itemView, this);
    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        if (position == 0) {
            //load the header
            //set the views
            holder.textProvider.setText("Provider");
            holder.textTempMin.setText("Min Temp");
            holder.textTempMax.setText("Max Temp");
            holder.textFeelMin.setText("Feels Min");
            holder.textFeelMax.setText("Feels Max");
            holder.textHumidity.setText("Humidity");
            holder.textPop.setText("POP");
            holder.textPopType.setText("P. Type");
            holder.textPopTotal.setText("P. Total");
            holder.textCloud.setText("Cloud");
            holder.textWind.setText("Wind");
            holder.textWindGust.setText("Gust");
            holder.textVisibility.setText("Vis");
            holder.textPressure.setText("Pressure");
            holder.textTotalRain.setText("T. Rain");
            holder.textTotalSnow.setText("T. Snow");

        } else {
            //Get the weather object
            Weather weather = weatherArrayList.get(position - 1);

            //check for null Weather
            if (weather == null) {
                return;
            }

            //set the views
            holder.textProvider.setText(weather.getProviderString());
            holder.textTempMin.setText(weather.getTempMin());
            holder.textTempMax.setText(weather.getTempMax());
            holder.textFeelMin.setText(weather.getTempFeelMin());
            holder.textFeelMax.setText(weather.getTempFeelMax());
            holder.textHumidity.setText(weather.getHumidity());
            holder.textPop.setText(weather.getPop());
            holder.textPopType.setText(weather.getPopTypeString());
            holder.textPopTotal.setText(weather.getPopTotal());
            holder.textCloud.setText(weather.getCloudCoverage());
            holder.textWind.setText(weather.getWindDirection() + " " + weather.getWindSpeed());
            holder.textWindGust.setText(weather.getWindGust());
            holder.textVisibility.setText(weather.getVisibility());
            holder.textPressure.setText(weather.getPressure());
            holder.textTotalRain.setText(weather.getTotalRain());
            holder.textTotalSnow.setText(weather.getTotalSnow());


            //imageview
            String iconString = weather.getIcon();
            //we set it to day, if for some reason we don't have isDay data
            if (isDay) {
                iconString += "d";
            } else {
                iconString += "n";
            }
            int iconInt = HelperFunctions.getIconInteger(iconString);
            if (iconInt != -1) {
                holder.imageIcon.setImageResource(iconInt);
            }//if/else isDay
        }

    }//onBindViewHolder

    @Override
    public int getItemCount() {
        if (weatherArrayList == null || weatherArrayList.size() < 1) {
            return 0;
        } else {
            return (weatherArrayList.size() + 1);
        }
    }//getItemCount





    class WeatherViewHolder extends RecyclerView.ViewHolder {
        //adapter
        final WeatherListAdapterToday adapter;
        //views
        final TextView textProvider, textTempMin, textTempMax, textFeelMin, textFeelMax,
                textHumidity, textPop, textPopType, textPopTotal, textCloud, textWind, textWindGust,
                textVisibility, textPressure, textTotalRain, textTotalSnow;
        final ImageView imageIcon;

        //constructor
        private WeatherViewHolder (View itemView, WeatherListAdapterToday adapter) {
            super(itemView);

            this.adapter = adapter;

            //find views
            textProvider = itemView.findViewById(R.id.text_provider);
            textTempMin = itemView.findViewById(R.id.text_temp_min);
            textTempMax = itemView.findViewById(R.id.text_temp_max);
            textFeelMin = itemView.findViewById(R.id.text_feel_min);
            textFeelMax = itemView.findViewById(R.id.text_feel_max);
            textHumidity = itemView.findViewById(R.id.text_humidity);
            textPop = itemView.findViewById(R.id.text_pop);
            textPopType = itemView.findViewById(R.id.text_pop_type);
            textPopTotal = itemView.findViewById(R.id.text_pop_total);
            textCloud = itemView.findViewById(R.id.text_cloud);
            textWind = itemView.findViewById(R.id.text_wind);
            textWindGust = itemView.findViewById(R.id.text_wind_gust);
            textVisibility = itemView.findViewById(R.id.text_visibility);
            textPressure = itemView.findViewById(R.id.text_pressure);
            textTotalRain = itemView.findViewById(R.id.text_total_rain);
            textTotalSnow = itemView.findViewById(R.id.text_total_snow);

            imageIcon = itemView.findViewById(R.id.image_icon);
        }//constructor

    }//WeatherViewHolder
}//WeatherListAdapterToday