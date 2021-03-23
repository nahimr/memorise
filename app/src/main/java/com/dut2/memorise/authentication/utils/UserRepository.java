package com.dut2.memorise.authentication.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.Objects;

public class UserRepository {
    public static final String FIREBASE_URL
            = "https://memorise-dut2-default-rtdb.europe-west1.firebasedatabase.app/";
    private final FirebaseDatabase mDatabase;
    private final FirebaseAuth mAuth;
    public static UserRepository instance;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        mDatabase.setPersistenceEnabled(true);
    }

    public void addUser(Activity context,
                               User user,
                        DatabaseReference.CompletionListener onCompletionListener,
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

    public static Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }


    public void deleteAccount(OnCompleteListener<Void> onCompleteListener){
        FirebaseUser tempUser =  Objects.requireNonNull(mAuth.getCurrentUser());
        String uid = tempUser.getUid();
        tempUser.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mDatabase.getReference("users").child(uid)
                        .removeValue().addOnCompleteListener(onCompleteListener).addOnFailureListener(e ->
                        Log.e("Memorise","Error Memorise when deleting account",e));
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
        if(!isCurrentUserExists()) return;
        atStart.run();
        mDatabase.getReference("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void getLeaderboard(Runnable onStart,OnCompleteListener<DataSnapshot> onCompleteListener){
        onStart.run();
        mDatabase.getReference("users").orderByChild("score").get()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e ->
                        Log.e("Memorise", "Error!",e));
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