package com.dut2.memorise.game.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dut2.memorise.R;
import com.dut2.memorise.game.view.Block;
import java.util.*;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {

    private Context context;
    private int TIMER = 0;
    private Map<Integer, Boolean> blocs;
    private final ArrayList<Runnable> runnables;
    public BlockAdapter(Context context, Map<Integer, Boolean> blocs) {
        this.context = context;
        this.blocs = blocs;
        this.runnables = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_blocks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256),
                rnd.nextInt(256), rnd.nextInt(256));
        holder.block.setARGBColor(color);
        holder.block.setPressed(false);


        holder.lighten = (()-> holder.block.setPressed(true));

        holder.lightenOut = (()-> holder.block.setPressed(false));

        if(this.blocs.get(position)){
            // MOVING FROM RECYCLERVIEW TO STATIC WAY !
            // TIMER IN
            this.runnables.add(holder.lighten);
            this.runnables.add(holder.lightenOut);

        }
    }

    public void RunThat(){
        int i = 0;
        for (Runnable run:
             runnables) {
            Log.i("DEBUG::", "YEEP");
            i++;
            i%=2;
            new Handler(Looper.getMainLooper()).postDelayed(run,TIMER);
            TIMER+=1000*i;
        }
    }

    @Override
    public int getItemCount() {
        return this.blocs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final Block block;
        private Runnable lighten;
        private Runnable lightenOut;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            block = itemView.findViewById(R.id.block);
            block.setPressed(false);

            //block.setEnabled(false);
        }

    }
}