package tech.khash.weathercompare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tech.khash.weathercompare.R;
import tech.khash.weathercompare.model.Weather;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * Main adapter class for Weather to be used with RecyclerView in showing weather data
 */

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder> {

    //list of data
    private final ArrayList<Weather> weatherArrayList;
    //inflater used for creating the view
    private LayoutInflater inflater;
    //context
    private Context context;

    //This is our listener implemented as an interface, to be used in the Activity
    private ListItemClickListener itemClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }//ListItemLongClickListener

    /**
     *      Public constructor
     * @param context  :context of parent activity
     * @param weatherArrayList : ArrayList<Weather> containing data
     * @param listener : listener
     */
    public WeatherListAdapter (Context context, ArrayList<Weather> weatherArrayList,
                               ListItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.weatherArrayList = weatherArrayList;
        this.itemClickListener = listener;
    }//constructor

    //It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    @NonNull
    @Override
    public WeatherListAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item_weather, parent, false);
        return new WeatherViewHolder(itemView, this);
    }//onCreateViewHolder

    /**
     * This connects the data to the view holder. This is where it creates each item
     *
     * @param holder   : the custome view holder
     * @param position : index of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull WeatherListAdapter.WeatherViewHolder holder, int position) {
        //TODO: if position is zero: load the descriptions (name, temp, etc). Then make sure to subtract
        //1 when getting the object from arraylist
        //Get the weather object
        Weather weather = weatherArrayList.get(position);

        //check for null Weather
        if (weather == null) {
            return;
        }

        //set the views
        holder.textDay.setText(weather.getDate());
        holder.textTempMin.setText(weather.getTempMin());
        holder.textTempMax.setText(weather.getTempMax());
        holder.textSummaryDay.setText(weather.getSummaryDay());
        holder.textPopDay.setText(weather.getPopDay());
        holder.textCloudDay.setText(weather.getCloudDay());
        holder.textSummaryNight.setText(weather.getSummaryNight());
        holder.textPopNight.setText(weather.getPopNight());
        holder.textCloudNight.setText(weather.getCloudNight());


    }//onBindViewHolder

    @Override
    public int getItemCount() {
        if (weatherArrayList ==null) {
            return 0;
        } else {
            return weatherArrayList.size();
        }
    }//getItemCount


    //Inner class for the view holder
    class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //views
        final WeatherListAdapter weatherListAdapter;
        final TextView textDay, textTempMin, textTempMax, textSummaryDay, textPopDay, textCloudDay,
        textSummaryNight, textPopNight, textCloudNight;

        //constructor
        private WeatherViewHolder (View itemView, WeatherListAdapter weatherListAdapter) {
            super(itemView);

            //find views
            textDay = itemView.findViewById(R.id.text_day);
            textTempMin = itemView.findViewById(R.id.text_temp_min);
            textTempMax = itemView.findViewById(R.id.text_temp_max);
            textSummaryDay = itemView.findViewById(R.id.text_summary_day);
            textPopDay = itemView.findViewById(R.id.text_pop_day);
            textCloudDay = itemView.findViewById(R.id.text_cloud_day);
            textSummaryNight = itemView.findViewById(R.id.text_summary_night);
            textPopNight = itemView.findViewById(R.id.text_pop_night);
            textCloudNight = itemView.findViewById(R.id.text_cloud_night);

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
    }//WeatherViewHolder - class

}//WeatherListAdapter - class
