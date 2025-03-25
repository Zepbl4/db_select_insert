package com.example;

import com.example.models.User;
import com.example.controller.DataBaseWorker;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/mydatabase";
        String username = "admin";
        String password = "admin";

        DataBaseWorker dbWorker = new DataBaseWorker(url, username, password);

        User user = dbWorker.getUserByLogin("user5");
        System.out.println("Found user: " + user);

        User newUser = new User(
                "new_user",
                "new_password",
                new Date(),
                "new_user@testmail.ru"
        );
        int rowsAffected = dbWorker.insertUser(newUser);
        System.out.println("Rows affected: " + rowsAffected);
    }
}