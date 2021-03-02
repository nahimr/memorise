package com.dut2.memorise.game.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.User;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private ArrayList<User> users;

    public RankAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rank,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.setItem((position+1),user.getUsername(),user.getScore());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView id;
        private final TextView username;
        private final TextView score;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id_user);
            username = itemView.findViewById(R.id.username_user);
            score = itemView.findViewById(R.id.score_user);
        }

        public void setItem(int idValue, String usernameValue, double scoreValue){
            id.setText(String.valueOf(idValue));
            username.setText(usernameValue);
            score.setText(String.valueOf(scoreValue));
        }
    }

}
