package tech.khash.weathercompare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tech.khash.weathercompare.R;
import tech.khash.weathercompare.model.Loc;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * Main adapter class to be used with RecyclerView in the MainActivity
 */

public class LocListAdapter extends RecyclerView.Adapter<LocListAdapter.LocViewHolder> {

    //list of data
    private final ArrayList<Loc> locArrayList;
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
     * Public constructor
     *
     * @param context        : context of the parent activity
     * @param locArrayList : ArrayList<Loc> containing data
     */
    public LocListAdapter(Context context, ArrayList<Loc> locArrayList,
                            ListItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.locArrayList = locArrayList;
        itemClickListener = listener;
    }//constructor

    //It inflates the item layout, and returns a ViewHolder with the layout and the adapter.
    @NonNull
    @Override
    public LocListAdapter.LocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item,
                parent, false);
        return new LocViewHolder(itemView, this, context);
    }//onCreateViewHolder

    /**
     * This connects the data to the view holder. This is where it creates each item
     *
     * @param holder   : the custome view holder
     * @param position : index of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull LocListAdapter.LocViewHolder holder, int position) {
        //Get the corresponding Fence object
        Loc loc = locArrayList.get(position);
        //check for null fence
        if (loc == null) {
            return;
        }

        //set name
        holder.idTextView.setText(loc.getId());

        //code
        String code = loc.getKeyAW();
        if (!TextUtils.isEmpty(code)) {
            holder.codeTextView.setText(code);
        }
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        if (locArrayList == null) {
            return 0;
        }
        return locArrayList.size();
    }//getItemCount


    //Inner class for the view holder
    class LocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //our views
        final TextView idTextView, codeTextView;
        final LocListAdapter locListAdapter;
        private Context context;

        //constructor
        private LocViewHolder(View itemView, LocListAdapter adapter, Context context) {
            super(itemView);
            this.context = context;
            //find view
            idTextView = itemView.findViewById(R.id.list_text_id);
            codeTextView = itemView.findViewById(R.id.list_text_location_code);
            //adapter
            this.locListAdapter = adapter;
            //for click listener
            itemView.setOnClickListener(this);
        }//FenceViewHolder

        @Override
        public void onClick(View v) {
            //get the index of the item
            int position = getLayoutPosition();
            itemClickListener.onListItemClick(position);
        }//onClick
    }//LocViewHolder

}//LocListAdapter
