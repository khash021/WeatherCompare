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

public class WeatherListFragmentAdapter extends RecyclerView.Adapter<WeatherListFragmentAdapter.WeatherViewHolder> {

    //list of data
    private final ArrayList<Weather> weatherArrayList;
    //inflater used for creating the view
    private LayoutInflater inflater;
    //context
    private Context context;

    //This is our listener implemented as an interface, to be used in the Activity
    private WeatherListFragmentAdapter.ListItemClickListener itemClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }//ListItemLongClickListener

    /**
     * Public constructor
     *
     * @param context          :context of parent activity
     * @param weatherArrayList : ArrayList<Weather> containing data
     * @param listener         : listener
     */
    public WeatherListFragmentAdapter(Context context, ArrayList<Weather> weatherArrayList,
                                      WeatherListFragmentAdapter.ListItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.weatherArrayList = weatherArrayList;
        this.itemClickListener = listener;
    }//constructor

    //It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    @NonNull
    @Override
    public WeatherListFragmentAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item_fragment_forecast, parent, false);
        return new WeatherListFragmentAdapter.WeatherViewHolder(itemView, this);
    }//onCreateViewHolder

    /**
     * This connects the data to the view holder. This is where it creates each item
     *
     * @param holder   : the custom view holder
     * @param position : index of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull WeatherListFragmentAdapter.WeatherViewHolder holder, int position) {

        //TODO: if position is zero: load the descriptions (name, temp, etc). Then make sure to subtract
        //1 when getting the object from arraylist
        //TODO: testing
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
            iconString += "d";
            int iconInt = HelperFunctions.getIconInteger(iconString);
            if (iconInt != -1) {
                holder.imageIcon.setImageResource(iconInt);
            }//if/else isDay
        }
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        if (weatherArrayList == null) {
            return 0;
        } else {
            //because the first element is header, we add one
            return (weatherArrayList.size() + 1);
        }
    }//getItemCount


    //Inner class for the view holder
    class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //views
        final WeatherListFragmentAdapter weatherListAdapter;
        final TextView textProvider, textTempMin, textTempMax, textFeelMin, textFeelMax,
                textHumidity, textPop, textPopType, textPopTotal, textCloud, textWind, textWindGust,
                textVisibility, textPressure, textTotalRain, textTotalSnow;
        final ImageView imageIcon;
        final LinearLayout rootLayout;

        //constructor
        private WeatherViewHolder(View itemView, WeatherListFragmentAdapter weatherListAdapter) {
            super(itemView);

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

            //adapter
            this.weatherListAdapter = weatherListAdapter;
            //for click listener
            itemView.setOnClickListener(this);
        }//constructor

        @Override
        public void onClick(View v) {
            //get the index of the item
            int position = getLayoutPosition();
            itemClickListener.onListItemClick(position);
        }//onClick
    }//WeatherViewHolder
}//WeatherListFragmentAdapter
