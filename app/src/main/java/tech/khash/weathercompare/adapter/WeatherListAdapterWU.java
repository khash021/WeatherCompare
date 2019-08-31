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
 * Main adapter class for Weather Unlocked to be used with RecyclerView in showing weather data
 */

public class WeatherListAdapterWU extends RecyclerView.Adapter<WeatherListAdapterWU.WeatherViewHolder> {

    //list of data
    private final ArrayList<Weather> weatherArrayList;
    //inflater used for creating the view
    private LayoutInflater inflater;
    //context
    private Context context;

    //This is our listener implemented as an interface, to be used in the Activity
    private WeatherListAdapterWU.ListItemClickListener itemClickListener;

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
    public WeatherListAdapterWU(Context context, ArrayList<Weather> weatherArrayList,
                                WeatherListAdapterWU.ListItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.weatherArrayList = weatherArrayList;
        this.itemClickListener = listener;
    }//constructor

    //It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    @NonNull
    @Override
    public WeatherListAdapterWU.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item_weather_wu, parent, false);
        return new WeatherListAdapterWU.WeatherViewHolder(itemView, this);
    }//onCreateViewHolder

    /**
     * This connects the data to the view holder. This is where it creates each item
     *
     * @param holder   : the custom view holder
     * @param position : index of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        //TODO: if position is zero: load the descriptions (name, temp, etc). Then make sure to subtract
        //1 when getting the object from arraylist
        //TODO: testing
        if (position == 0) {
            //load the header
            //set the views
            holder.textDay.setText("Day");
            holder.textTempMin.setText("Min Temp");
            holder.textTempMax.setText("Max Temp");
            holder.textHumidity.setText("Humidity");
            holder.textPop.setText("POP");
            holder.textPopTotal.setText("Total prec.");
            holder.textWind.setText("Wind");
            holder.textWindGust.setText("Gust");

        } else {


            //Get the weather object
            Weather weather = weatherArrayList.get(position - 1);

            //check for null Weather
            if (weather == null) {
                return;
            }

            //set the views
            holder.textDay.setText(weather.getDate());
            holder.textTempMin.setText(weather.getTempMin());
            holder.textTempMax.setText(weather.getTempMax());
            holder.textHumidity.setText(weather.getHumidity());
            holder.textPop.setText(weather.getPop());
            holder.textPopTotal.setText(weather.getPopTotal());
            holder.textWind.setText(weather.getWindSpeed());
            holder.textWindGust.setText(weather.getWindGust());
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
        final WeatherListAdapterWU weatherListAdapter;
        final TextView textDay, textTempMin, textTempMax, textHumidity, textPop, textPopTotal,
                textWind, textWindGust;

        //constructor
        private WeatherViewHolder(View itemView, WeatherListAdapterWU weatherListAdapter) {
            super(itemView);

            //find views
            textDay = itemView.findViewById(R.id.text_day);
            textTempMin = itemView.findViewById(R.id.text_temp_min);
            textTempMax = itemView.findViewById(R.id.text_temp_max);
            textHumidity = itemView.findViewById(R.id.text_humidity);
            textPop = itemView.findViewById(R.id.text_pop);
            textPopTotal = itemView.findViewById(R.id.text_pop_total);
            textWind = itemView.findViewById(R.id.text_wind);
            textWindGust = itemView.findViewById(R.id.text_wind_gust);

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
}//WeatherListAdapterWU
