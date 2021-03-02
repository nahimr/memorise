package com.dut2.memorise.authentication.utils;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserRepository {
    public static String FIREBASE_URL
            = "https://memorise-dut2-default-rtdb.europe-west1.firebasedatabase.app/";
    private final FirebaseDatabase mDatabase;
    private final FirebaseAuth mAuth;
    public static UserRepository instance;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        //mAuth.useEmulator("192.168.1.25", 8080);
        mDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        //mDatabase.useEmulator("192.168.1.25", 8081);
    }

    public void addUser(Activity context,
                               User user, DatabaseReference.CompletionListener onCompletionListener,
                        OnFailureListener onFailureListener){
        mAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(context,task -> {
                    if(task.isSuccessful()){
                        DatabaseReference ref = mDatabase.getReference("users").child(
                                Objects.requireNonNull(mAuth
                                        .getCurrentUser()).getUid());
                        ref.setValue(user,onCompletionListener);
                    }
                }).addOnFailureListener(onFailureListener);
    }

    public void deleteAccount(OnCompleteListener<Void> onCompleteListener){
        FirebaseUser tempUser =  Objects.requireNonNull(mAuth.getCurrentUser());
        String uid = tempUser.getUid();
        tempUser.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mDatabase.getReference("users").child(uid).removeValue().addOnCompleteListener(onCompleteListener).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Memorise","Error Memorise when deleting account",e);
                    }
                });
            }
        });
    }

    public void authUser(Activity context, String email, String password,
                         OnCompleteListener<AuthResult> onCompleteListener,
                         OnFailureListener onFailureListener){
        if(email.isEmpty() || password.isEmpty()) return;
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(context,onCompleteListener)
                .addOnFailureListener(context,onFailureListener);
    }

    public void disconnect(){
        mAuth.signOut();
    }

    public void getCurrentUserData(Runnable atStart,ValueEventListener valueEventListener){
        if(!isCurrentUserExists())return;
        atStart.run();
        mDatabase.getReference("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void getLeaderboard(Runnable onStart,OnCompleteListener<DataSnapshot> onCompleteListener){
        onStart.run();
        mDatabase.getReference("users").orderByKey().get()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> {
                    Log.e("Memorise", "Error!",e);
                });
    }

    public FirebaseUser getCurrentUser(){
        return Objects.requireNonNull(mAuth.getCurrentUser());
    }

    public boolean isCurrentUserExists(){
        return mAuth.getCurrentUser() != null;
    }

    public void updateUserScore(double newScore, OnCompleteListener<Void> onCompleteListener,
                                OnFailureListener onFailureListener){
        if(!isCurrentUserExists()) return;
        FirebaseUser tempUser =  Objects.requireNonNull(mAuth.getCurrentUser());
        String uid = tempUser.getUid();
        Map<String,Object> userData = new HashMap<>();
        userData.put("score",newScore);
        mDatabase.getReference("users").child(uid)
                .child("score")
                .setValue(newScore)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener);
    }

    public static UserRepository getInstance() {
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
}