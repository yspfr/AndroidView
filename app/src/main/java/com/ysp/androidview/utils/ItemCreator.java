package com.ysp.androidview.utils;

import java.util.ArrayList;
import java.util.List;

public class ItemCreator {
    public static List<Integer> range(int start, int end){
        List<Integer> result = new ArrayList<>();
        for (int i=start;i<=end;i++){
            result.add(i);
        }
        return result;
    }
}
