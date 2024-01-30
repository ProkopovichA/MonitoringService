package com.prokopovich;


import com.prokopovich.in.MainController;
import com.prokopovich.service.IntTerminalScanner;
import com.prokopovich.service.PreBuildDemoData;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
       PreBuildDemoData.build();
       MainController.start();
    }
}