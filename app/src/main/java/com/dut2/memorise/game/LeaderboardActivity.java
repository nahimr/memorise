package com.dut2.memorise.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.User;
import com.dut2.memorise.authentication.utils.UserComparator;
import com.dut2.memorise.authentication.utils.UserRepository;
import com.dut2.memorise.game.adapter.RankAdapter;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView leaderboard;
    private ProgressBar loading;
    private ArrayList<User> users;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        users = new ArrayList<>();
        loading = findViewById(R.id.loading_leaderboard);
        leaderboard = findViewById(R.id.leaderboard);
        final RankAdapter rankAdapter = new RankAdapter(users);
        leaderboard.setLayoutManager(new LinearLayoutManager(this));
        leaderboard.setAdapter(rankAdapter);

        UserRepository.getInstance().getLeaderboard(() -> loading.setVisibility(View.VISIBLE)
            , task -> {
                for(DataSnapshot dataSnapshot:
                        Objects.requireNonNull(task.getResult()).getChildren()){
                    User tempUser = dataSnapshot.getValue(User.class);
                    if(tempUser != null){
                        users.add(tempUser);
                    }
                }
                users.sort(new UserComparator());
                loading.setVisibility(View.INVISIBLE);
                rankAdapter.notifyDataSetChanged();
            });
    }
}