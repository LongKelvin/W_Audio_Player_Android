package com.e.w_audio_player.ListSongs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.e.w_audio_player.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> implements Filterable {
    ArrayList<HashMap<String, String>> dataSong;
    ArrayList<HashMap<String, String>> dataSongFull;
    Context context;

    public SongsAdapter(ArrayList<HashMap<String, String>> playList) {
        SongsManager songsManager = new SongsManager();
        dataSong = songsManager.getPlayList();
        dataSongFull = songsManager.getPlayList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.playlist_item, parent,false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(dataSong.get(position).get("songTitle"));
    }

    @Override
    public int getItemCount() {
        return dataSong.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.songTitle);

        }

    }

    @Override
    public Filter getFilter() {
        return songFillter;
    }

    private  ArrayList<HashMap<String, String>> songFillterArr = new ArrayList<>();
    private Filter songFillter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
           // ArrayList<HashMap<String, String>> songFillterArr = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                songFillterArr.addAll(dataSongFull);
            }
            else {
                String fillterKey = constraint.toString().toLowerCase().trim();
                for (HashMap<String, String>  songs : dataSongFull){
                    if(songs.get("songTitle").toLowerCase().contains(fillterKey)){
                        songFillterArr.add(songs);
                    }

                }
            }
            FilterResults result = new FilterResults();
            result.values = songFillterArr;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            dataSong.clear();
            for (HashMap<String, String>  songs : songFillterArr){
                dataSong.add(songs);
                notifyDataSetChanged();
            }
        }
    };
}
