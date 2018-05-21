package chierra.hof_reporter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Unknown on 5/4/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<DetectData> feeds;
    private OnClickListener listener;
    private Context context;

    public HistoryAdapter(List<DetectData> feeds, Context context, OnClickListener listener) {
        this.feeds = feeds;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetectData feed = feeds.get(position);

        holder.bind(feed, listener);
        holder.feedsTime.setText(feed.getmTime());
        //holder.feedsDate.setText(feed.getmUrl());
    }

    @Override
    public int getItemCount() {
        try {
            return feeds.size();
        }catch (NullPointerException e){
            Toast.makeText(context,"Error Timed Out",Toast.LENGTH_SHORT);
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView feedsTime;
        TextView feedsDate;

        public ViewHolder(View itemView) {
            super(itemView);

            feedsTime = itemView.findViewById(R.id.time);}

        public void bind(final DetectData feed, final OnClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(feed);
                }
            });
        }
    }


}
