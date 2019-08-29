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
 * Main adapter class for Weather Bit to be used with RecyclerView in showing weather data
 */
public class WeatherListAdapterWB extends RecyclerView.Adapter<WeatherListAdapterWB.WeatherViewHolder> {


    //list of data
    private final ArrayList<Weather> weatherArrayList;
    //inflater used for creating the view
    private LayoutInflater inflater;
    //context
    private Context context;

    //This is our listener implemented as an interface, to be used in the Activity
    private WeatherListAdapterWB.ListItemClickListener itemClickListener;

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
    public WeatherListAdapterWB(Context context, ArrayList<Weather> weatherArrayList,
                                WeatherListAdapterWB.ListItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.weatherArrayList = weatherArrayList;
        this.itemClickListener = listener;
    }//constructor

    //It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    @NonNull
    @Override
    public WeatherListAdapterWB.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item_weather_wb, parent, false);

        //TODO: testing
//        //We want to make all these 4 views to be the same width
//        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
//        layoutParams.width = (int) (parent.getWidth() *0.25);
//        itemView.setLayoutParams(layoutParams);

        return new WeatherListAdapterWB.WeatherViewHolder(itemView,this);
    }//onCreateViewHolder

    /**
     * This connects the data to the view holder. This is where it creates each item
     *
     * @param holder   : the custome view holder
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
            holder.textSummary.setText("Summary");
            holder.textTempMin.setText("Min Temp");
            holder.textTempMax.setText("Max Temp");
            holder.textDew.setText("Dew Point");
            holder.textPressure.setText("Pressure");
            holder.textHumidity.setText("Humidity");
            holder.textPop.setText("POP");
            holder.textWind.setText("Wind");
            holder.textWindGust.setText("Gust");
            holder.textCloud.setText("Cloud");
            holder.textVisibility.setText("Visibility");

        } else {


            //Get the weather object
            Weather weather = weatherArrayList.get(position - 1);

            //check for null Weather
            if (weather == null) {
                return;
            }

            //set the views
            holder.textDay.setText(weather.getDate());
            holder.textSummary.setText(weather.getSummary());
            holder.textTempMin.setText(weather.getTempMin());
            holder.textTempMax.setText(weather.getTempMax());
            holder.textDew.setText(weather.getDewPoint());
            holder.textPressure.setText(weather.getPressure());
            holder.textHumidity.setText(weather.getHumidity());
            holder.textPop.setText(weather.getPop());
            holder.textWind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
            holder.textWindGust.setText(weather.getWindGust());
            holder.textCloud.setText(weather.getCloudCoverage());
            holder.textVisibility.setText(weather.getVisibility());

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
        final WeatherListAdapterWB weatherListAdapter;
        final TextView textDay, textSummary, textTempMin, textTempMax, textDew, textPressure,
        textHumidity, textPop, textWind, textWindGust, textCloud, textVisibility;

        //constructor
        private WeatherViewHolder(View itemView, WeatherListAdapterWB weatherListAdapter) {
            super(itemView);

            //find views
            textDay = itemView.findViewById(R.id.text_day);
            textSummary = itemView.findViewById(R.id.text_summary);
            textTempMin = itemView.findViewById(R.id.text_temp_min);
            textTempMax = itemView.findViewById(R.id.text_temp_max);
            textDew = itemView.findViewById(R.id.text_dew);
            textPressure = itemView.findViewById(R.id.text_pressure);
            textHumidity = itemView.findViewById(R.id.text_humidity);
            textPop = itemView.findViewById(R.id.text_pop);
            textWind = itemView.findViewById(R.id.text_wind);
            textWindGust = itemView.findViewById(R.id.text_wind_gust);
            textCloud = itemView.findViewById(R.id.text_cloud);
            textVisibility = itemView.findViewById(R.id.text_visibility);

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
}//WeatherListAdapterWB
