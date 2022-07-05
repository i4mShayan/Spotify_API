import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.PremiumUsersApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String MY_API_KEY = "16ddd7aa2436ea67ae85120472980455a46a407a";
    public static ApiClient defaultClient;
    public static DefaultApi defaultApi;
    public static AuthApi authApi;
    public static UsersApi usersApi;
    public static User currentUser;
    public static long start = 0;
    public static boolean isFirstCall = true;

    public static void authAPIKey() {
        defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
        ApiKeyAuth.setApiKey(MY_API_KEY);
        defaultApi = new DefaultApi();
        authApi = new AuthApi(defaultClient);
        usersApi = new UsersApi(defaultClient);
    }

    public static void reset(){
        try {
            InlineResponse2001 result = defaultApi.reset();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#reset");
            e.printStackTrace();
        }
    }

    public static boolean canRequestServer(){
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime - start > 20 || isFirstCall;
    }

    public static boolean canRequestServerThenDoIt(){
        long currentTime = System.currentTimeMillis() / 1000;
        boolean ans=currentTime - start > 20 || isFirstCall;
        isFirstCall=false;
        return ans;
    }


    public static void loginProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username");
        String username = input.next();
        System.out.println("Enter password");
        String password = input.next();

        String token = "";
        try {
            AuthLoginBody authLoginBody = new AuthLoginBody();
            authLoginBody.setUsername(username);
            authLoginBody.setPassword(password);
            token = (authApi.login(authLoginBody).getToken());
            currentUser = new User(username,password,token);
            defaultClient.setAccessToken(currentUser.token);
            OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
            bearerAuth.setAccessToken(currentUser.token);
            System.out.println("You Successfully logged in");
            userMenuProcess();
        } catch (ApiException apiException) {
            String errorResponse = apiException.getResponseBody();
            if (errorResponse.contains("invalid username or password")) {
                System.err.println("invalid username or password");
                loginProcess();
            }
        }
    }

    public static void signUpProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username");
        String username = input.next();
        System.out.println("Enter password");
        String password = input.next();
        if(Convertor.doesStringMatchWith(password, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")){
            try {
                AuthSignupBody authSignupBody = new AuthSignupBody();
                authSignupBody.setUsername(username);
                authSignupBody.setPassword(password);
                String token = (authApi.signUp(authSignupBody).getToken());
                currentUser = new User(username,password,token);
                defaultClient.setAccessToken(currentUser.token);
                OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
                bearerAuth.setAccessToken(currentUser.token);
                System.out.println("You Successfully signed in");
                userMenuProcess();
            } catch (ApiException apiException) {
                String errorResponse = apiException.getResponseBody();
                if (errorResponse.contains("no username provided")) {
                    System.err.println("no username provided");
                    signUpProcess();
                }
                else if(errorResponse.contains("username already taken")){
                    System.err.println("username already taken");
                    signUpProcess();
                }
            }
        }
        else{
            System.err.println("password length must be at least 8 and include uppercase, lowercase and number!");
            signUpProcess();
        }
    }


    static Date getTime(String time){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean isUserPremium(){
        if(profile==null) return false;
        if(profile.getPremiumUntil() == null) return false;
        return new Date().before(getTime(profile.getPremiumUntil()));
    }

    public static InlineResponse2003 profile;
    static void showProfile(){
        if(profile==null){
            System.out.println("null");
        }else{
            System.out.println("username: " + profile.getUsername());
            System.out.println("premium until: " + (isUserPremium() ? profile.getPremiumUntil():"not premium"));
        }
        System.out.println("=================================");

    };
    public static void profile(){
        Scanner input = new Scanner(System.in);
        if (canRequestServerThenDoIt()) {
            try {
                profile=usersApi.getProfileInfo();
                start = System.currentTimeMillis() / 1000;
                showProfile();
                userMenuProcess();
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
            }
        } else {
            showProfile();
        }
    }

    public static Tracks tracks;
    static void showTracks(Tracks tracks, String startingString){
        for(Track track: tracks){
            if(!isUserPremium() && track.isIsPremium()) continue;
            System.out.println(startingString + "id :" + track.getId());
            System.out.println(startingString + "name :" + track.getName());
            System.out.println(startingString + "artist :" + track.getArtist());
            System.out.println(startingString + "isPremium :" + track.isIsPremium());
            System.out.println(startingString + "===============================================");
        }
    }

    static void showTracks(List<Track> tracks, String startingString){
        for(Track track: tracks){
            if(!isUserPremium() && track.isIsPremium()) continue;
            System.out.println(startingString + "id :" + track.getId());
            System.out.println(startingString + "name :" + track.getName());
            System.out.println(startingString + "artist :" + track.getArtist());
            System.out.println(startingString + "isPremium :" + track.isIsPremium());
            System.out.println(startingString + "===============================================");
        }
    }

    public static void tracks(){
        if (canRequestServerThenDoIt()) {
            try {
                tracks = usersApi.getTracksInfo();
                showTracks(tracks, "");
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
            }
        } else {
            showTracks(tracks, "");
        }
    }


    public static void showPlaylists(Playlists playlists){
        for (Playlist playlist: playlists){
            System.out.println("playlist id: " + playlist.getId());
            System.out.println("playlist name: " + playlist.getName());
            System.out.println("playlist tracks: ");
            System.out.println("*******************************");
            showTracks(playlist.getTracks(), "\t");
        }
    }
    public static Playlists playlists;
    public static void playlists() {
        if(canRequestServerThenDoIt()){
            try {
                playlists=usersApi.getPlaylistsInfo();
                start = System.currentTimeMillis() / 1000;
                showPlaylists(playlists);
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
            }
        }else{
            showPlaylists(playlists);
        }
    }


    public static void makeNewPlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist name: ");
        String playlistName=in.next();
        try {
            PlaylistsBody playlistsBody = new PlaylistsBody();
            playlistsBody.setName(playlistName);
            usersApi.createPlaylist(playlistsBody).getId();
            System.out.println("Playlist successfully added :)");
        } catch (ApiException apiException) {
            if(apiException.getResponseBody().contains("no name provided")){
                System.out.println("no name provided");
            }
            makeNewPlaylist();
        }
    }

    public static void deletePlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist id you want to delete: ");
        int playlistID=in.nextInt();
        try {
            usersApi.deletePlaylist(playlistID);
            System.out.println("Playlist successfully deleted :)");
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println("no playlist_id provided");
            }else if(response.contains("playlist not found")){
                System.out.println("playlist not found");
            }
            deletePlaylist();
        }
    }

    public static void addTrackToPlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist id you want to add track to: ");
        int playlistID=in.nextInt();
        System.out.println("Enter track id you want to add: ");
        String trackID=in.next();
        try {
            usersApi.addTrackToPlaylist(playlistID, trackID);
            System.out.println("Track successfully added to the playlist :)");
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println("no playlist_id provided");
            }else if(response.contains("track not found")){
                System.out.println("track not found");
            }else if(response.contains("track already exists in playlist")){
                System.out.println("track already exists in playlist");
            }
            addTrackToPlaylist();
        }
    }

    public static void removeTrackFromPlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist id you want to add track to: ");
        int playlistID=in.nextInt();
        System.out.println("Enter track id you want to add: ");
        String trackID=in.next();
        try {
            usersApi.removeTrackFromPlaylist(playlistID, trackID);
            System.out.println("Track successfully removed from the playlist :)");
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println("no playlist_id provided");
            }else if(response.contains("track does not exist in playlist")){
                System.out.println("track does not exist in playlist");
            }
            removeTrackFromPlaylist();
        }
    }

    public static void upgradeToPremium(){
        if(isUserPremium()) System.out.println("You are already a premium user!");
        else{
            try {
                InlineResponse2005 upgradeResponse = usersApi.upgradeToPremium();
                usersApi.getProfileInfo().setPremiumUntil(upgradeResponse.getPremiumUntil());
                System.out.println("Congrats! now you are premium :)");
            } catch (ApiException apiException) {
                String response = apiException.getResponseBody();
                if(response.contains("try again")){
                    System.out.println("try again! maybe this time you'll be lucky!");
                }
            }
        }
        System.out.println("===============================================");
        userMenuProcess();
    }



    public static void getFriends(){
        if(isUserPremium()){
            if(canRequestServerThenDoIt()){
                PremiumUsersApi premiumUsersApi = new PremiumUsersApi(defaultClient);
                try {
                    List<String> friends = premiumUsersApi.getFriends();
                    start = System.currentTimeMillis() / 1000;
                    System.out.println("Friends list: ");
                    System.out.println("*******************************");
                    for(String friend: friends){
                        System.out.println("- " + friend);
                    }
                } catch (ApiException apiException) {
                    System.out.println(apiException.getResponseBody());
                }
            }
        }else{
            System.out.println("You are not a premium user!");
        }
        System.out.println("===============================================");
        userMenuProcess();
    }

    public static void userMenuProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("1-Profile\n2-Tracks\n3-Playlists\n4-Make a playlist\n5-Delete a playlist\n6-Add track to a playlist\n7-Remove track from a playlist\n8-Upgrade to premium\n9-Friends list");
        System.out.println("===============================================");
        System.out.print("-> ");
        int choice = input.nextInt();
        switch (choice){
            case 1: profile(); break;
            case 2: tracks(); break;
            case 3: playlists(); break;
            case 4: makeNewPlaylist(); break;
            case 5: deletePlaylist(); break;
            case 6: addTrackToPlaylist(); break;
            case 7: removeTrackFromPlaylist(); break;
            case 8: upgradeToPremium(); break;
            case 9: getFriends(); break;
        }
    }

    public static void main(String[] args) {
        authAPIKey();
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome\n1-Login\n2-Signup");

        int choice = input.nextInt();
        switch (choice){
            case 1: loginProcess(); break;
            case 2: signUpProcess(); break;
        }
    }
}
