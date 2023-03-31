package ru.scrait.technostrelka.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.scrait.technostrelka.R;

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHistoryAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;

    private List<String> listDates;
    private List<String> listSums;
    private List<String> listCategories;
    private List<String> listTypes;


    // data is passed into the constructor
    public RecyclerViewHistoryAdapter(Context context,
                                      List<String> dates,
                                      List<String> sums,
                                      List<String> categories,
                                      List<String> types) {

        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;

        this.listDates = dates;
        this.listSums = sums;
        this.listCategories = categories;
        this.listTypes = types;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_view_history_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String date = listDates.get(position);
        String sum = listSums.get(position);
        String category = listCategories.get(position);
        String type = listTypes.get(position);

        holder.tvDate.setText(date);
        holder.tvSum.setText(sum + " руб.");
        holder.tvCategory.setText(category);
        holder.tvType.setText(type);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listDates.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate;
        TextView tvType;
        TextView tvCategory;
        TextView tvSum;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvRecyclerViewDate);
            tvType = itemView.findViewById(R.id.tvRecyclerViewType);
            tvCategory = itemView.findViewById(R.id.tvRecyclerViewCategory);
            tvSum = itemView.findViewById(R.id.tvRecyclerViewSum);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return listDates.get(id);
    }

}
