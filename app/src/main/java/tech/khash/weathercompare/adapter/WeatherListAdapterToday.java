package tech.khash.weathercompare.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
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
        View itemView = inflater.inflate(R.layout.list_item_today, parent, false);
        return new WeatherListAdapterToday.WeatherViewHolder(itemView, this);
    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        if (position == 0) {
            //load the header
            //set the views
            holder.textProvider.setText(context.getResources().getString(R.string.provider));
            holder.textTempMin.setText(context.getResources().getString(R.string.min_temp));
            holder.textTempMax.setText(context.getResources().getString(R.string.max_temp));
            holder.textFeelMin.setText(context.getResources().getString(R.string.feels_min));
            holder.textFeelMax.setText(context.getResources().getString(R.string.feels_max));
            holder.textHumidity.setText(context.getResources().getString(R.string.humidity));
            holder.textPop.setText(context.getResources().getString(R.string.pop));
            holder.textPopType.setText(context.getResources().getString(R.string.pop_type));
            holder.textPopTotal.setText(context.getResources().getString(R.string.pop_total));
            holder.textCloud.setText(context.getResources().getString(R.string.cloud));
            holder.textWind.setText(context.getResources().getString(R.string.wind));
            holder.textWindGust.setText(context.getResources().getString(R.string.gust));
            holder.textVisibility.setText(context.getResources().getString(R.string.visibility));
            holder.textPressure.setText(context.getResources().getString(R.string.pressure));
            holder.textTotalRain.setText(context.getResources().getString(R.string.total_rain));
            holder.textTotalSnow.setText(context.getResources().getString(R.string.total_snow));

            //set the background color
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.background));

        } else {
            //Get the weather object
            Weather weather = weatherArrayList.get(position - 1);

            //check for null Weather
            if (weather == null) {
                return;
            }

            //set the views
            holder.textProvider.setText(weather.getProviderString());
            holder.textHumidity.setText(weather.getHumidity());
            holder.textPop.setText(weather.getPop());
            holder.textPopType.setText(weather.getPopTypeString());
            holder.textCloud.setText(weather.getCloudCoverage());
            holder.textPressure.setText(weather.getPressure());


            //set the views based on selected units
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean metric = sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_key_metric), true);
            if (metric) {
                //metric
                holder.textTempMin.setText(weather.getTempMin());
                holder.textTempMax.setText(weather.getTempMax());
                holder.textFeelMin.setText(weather.getTempFeelMin());
                holder.textFeelMax.setText(weather.getTempFeelMax());
                holder.textPopTotal.setText(weather.getPopTotal());
                holder.textWind.setText(weather.getWindDirection() + " " + weather.getWindSpeed());
                holder.textWindGust.setText(weather.getWindGust());
                holder.textVisibility.setText(weather.getVisibility());
                holder.textTotalRain.setText(weather.getTotalRain());
                holder.textTotalSnow.setText(weather.getTotalSnow());
            } else {
                //imperial
                holder.textTempMin.setText(weather.getTempMinImperial());
                holder.textTempMax.setText(weather.getTempMaxImperial());
                holder.textFeelMin.setText(weather.getTempFeelMinImperial());
                holder.textFeelMax.setText(weather.getTempFeelMaxImperial());
                holder.textPopTotal.setText(weather.getPopTotalImperial());
                holder.textWind.setText(weather.getWindDirection() + " " + weather.getWindSpeedImperial());
                holder.textWindGust.setText(weather.getWindGustImperial());
                holder.textVisibility.setText(weather.getVisibilityImperial());
                holder.textTotalRain.setText(weather.getTotalRainImperial());
                holder.textTotalSnow.setText(weather.getTotalSnowImperial());

            }
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
        final LinearLayout rootLayout;

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

            rootLayout = itemView.findViewById(R.id.root_layout);
        }//constructor

    }//WeatherViewHolder
}//WeatherListAdapterToday
