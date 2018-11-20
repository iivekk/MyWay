package hr.franjkovic.ivan.myway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.database.Track;

import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.*;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.MyViewHolder> {

    private List<Track> trackList;
    private LayoutInflater mLayoutInflater;
    private OnClickItemListener mOnClickItemListener;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item
                , parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.tvTrackListName.setText(track.getTrackName());
        holder.tvTrackListDate.setText(track.getTrackDate());
        holder.tvTrackListLength.setText(distanceToString(track.getTrackLength(), "/"));
        holder.tvTrackListDuration.setText(track.getTrackDuration());
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvTrackListName;
        public TextView tvTrackListDate;
        public TextView tvTrackListLength;
        public TextView tvTrackListDuration;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTrackListName = itemView.findViewById(R.id.tvTrackListName);
            tvTrackListDate = itemView.findViewById(R.id.tvTrackListDate);
            tvTrackListLength = itemView.findViewById(R.id.tvTrackListLength);
            tvTrackListDuration = itemView.findViewById(R.id.tvTrackListDuration);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    public TrackListAdapter(Context context, List<Track> trackList) {
        this.mLayoutInflater = mLayoutInflater.from(context);
        this.trackList = trackList;
    }

    public void updateList(final List<Track> trackList) {
        this.trackList = trackList;
        notifyDataSetChanged();
    }

    public void setClickListener(OnClickItemListener listener) {
        this.mOnClickItemListener = listener;
    }

    public Track getTrack(int index) {
        return trackList.get(index);
    }

}
