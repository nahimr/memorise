package com.dut2.memorise.authentication.utils;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        double o1Score = o1.getScore();
        double o2Score = o2.getScore();
        if(o1Score > o2Score){
            return -1;
        } else if(o1Score == o2Score){
            return 0;
        } else {
            return 1;
        }
    }
}
