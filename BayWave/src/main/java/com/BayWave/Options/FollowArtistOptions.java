package com.BayWave.Options;

import com.BayWave.Tables.FollowArtistTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class FollowArtistOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("FOLLOW_ARTIST Options:");
        System.out.println("1. Print FOLLOW_ARTIST table");
        System.out.println("2. Set USER_ as follower of ARTIST");
        System.out.println("3. Remove USER_ as follower of ARTIST");
        System.out.println("4. Check if user follows artist");
        System.out.println("5. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String user;
            String artist;
            switch (input) {
                case "1":
                    FollowArtistTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    FollowArtistTable.register(connection, user, artist);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    FollowArtistTable.delete(connection, user, artist);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Contains: " + FollowArtistTable.contains(connection, user, artist));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
