package tech.khash.weathercompare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tech.khash.weathercompare.R;
import tech.khash.weathercompare.adapter.WeatherListFragmentAdapter;
import tech.khash.weathercompare.model.Weather;

public class ForecastFragment1 extends Fragment
        implements WeatherListFragmentAdapter.ListItemClickListener{

    private final static String TAG = ForecastFragment1.class.getSimpleName();

    private ArrayList<Weather> weatherArrayList;

    private Context context;

    public ForecastFragment1 (Context context, ArrayList<Weather> weatherArrayList) {
        this.weatherArrayList = weatherArrayList;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //check for empty array
        if (weatherArrayList == null || weatherArrayList.size() < 1) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        //get recycler view
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        WeatherListFragmentAdapter adapter = new WeatherListFragmentAdapter(context, weatherArrayList, this);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a horizontal layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);

        return rootView;
    }//onCreateView

    @Override
    public void onListItemClick(int clickedItemIndex) {
    }//WeatherListFragmentAdapter
}//ForecastFragment1
