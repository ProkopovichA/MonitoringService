package com.prokopovich;


import com.prokopovich.in.MainController;
import com.prokopovich.service.PreBuildDemoData;

public class Main {
    public static void main(String[] args) {
        PreBuildDemoData.build();
        MainController.Start();
    }
}