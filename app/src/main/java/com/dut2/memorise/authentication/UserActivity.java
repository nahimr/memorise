package com.dut2.memorise.authentication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.User;
import com.dut2.memorise.authentication.utils.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import soup.neumorphism.NeumorphButton;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final ProgressBar loading = findViewById(R.id.loading_user);
        final NeumorphButton deleteAccount = findViewById(R.id.deleteAccount);
        final NeumorphButton disconnect = findViewById(R.id.disconnect);
        final ImageView userImage = findViewById(R.id.userImage);
        final TextView username = findViewById(R.id.username);
        final TextView score = findViewById(R.id.score);
        final FirebaseUser user = UserRepository.getInstance().getCurrentUser();

        UserRepository.getInstance().getCurrentUserData(()->{
            loading.setVisibility(View.VISIBLE);
        },new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User fetchedUser = snapshot.getValue(User.class);
                if(fetchedUser != null){
                    username.setText(String.format(getString(R.string.usernameValue), fetchedUser.getUsername()));
                    score.setText(String.format(getString(R.string.scoreValue), fetchedUser.getScore()));
                    loading.setVisibility(View.INVISIBLE);
                    username.setVisibility(View.VISIBLE);
                    score.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserActivity.this,R.string.connectionErrDB,Toast.LENGTH_SHORT).show();
            }
        });

        userImage.setImageURI(user.getPhotoUrl());

        disconnect.setOnClickListener(v-> {
            UserRepository.getInstance().disconnect();
            startActivity(new Intent(UserActivity.this, LoginActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        });
        deleteAccount.setOnClickListener(v-> {
            UserRepository.getInstance().deleteAccount(task -> {
                if (task.isSuccessful()){
                    startActivity(new Intent(UserActivity.this, LoginActivity.class),
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    finish();
                }else{
                    Toast.makeText(this, "Error when deleting account!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}