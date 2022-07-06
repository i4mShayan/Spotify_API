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
import java.util.*;

public class Main {
    public static final String MY_API_KEY = "16ddd7aa2436ea67ae85120472980455a46a407a";
    public static ApiClient defaultClient;
    public static DefaultApi defaultApi;
    public static AuthApi authApi;
    public static UsersApi usersApi;
    public static User currentUser;
    public static long start = 0;
    public static boolean isFirstCall = true;
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String BLUE = "\033[0;34m";

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

    public static void clearScreen()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (Exception e)
        {
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
        System.out.println("Enter username:");
        String username = input.next();
        System.out.println("Enter password:");
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
            System.out.println("===============================================");
            System.out.println(GREEN +"You Successfully logged in" + RESET);
            System.out.println("===============================================");
            userMenuProcess();
        } catch (ApiException apiException) {
            String errorResponse = apiException.getResponseBody();
            if (errorResponse.contains("invalid username or password")) {
                System.out.println("===============================================");
                System.out.println(RED + "invalid username or password" + RESET);
                System.out.println("===============================================");
                loginProcess();
            }
        }
    }

    public static void signUpProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = input.next();
        System.out.println("Enter password:");
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
                System.out.println("===============================================");
                System.out.println(GREEN + "You Successfully signed in" + RESET);
                System.out.println("===============================================");
                userMenuProcess();
            } catch (ApiException apiException) {
                String errorResponse = apiException.getResponseBody();
                if (errorResponse.contains("no username provided")) {
                    System.out.println("===============================================");
                    System.out.println(RED + "no username provided" + RESET);
                    System.out.println("===============================================");
                    signUpProcess();
                }
                else if(errorResponse.contains("username already taken")){
                    System.out.println("===============================================");
                    System.out.println(RED + "username already taken" + RESET);
                    System.out.println("===============================================");
                    signUpProcess();
                }
            }
        }
        else{
            System.out.println("===============================================");
            System.out.println(RED + "password length must be at least 8 and include uppercase, lowercase and number!" + RESET);
            System.out.println("===============================================");
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
        InlineResponse2003 profile = null;
        try {
            profile = usersApi.getProfileInfo();
        } catch (ApiException apiException) {
            System.err.println(apiException.getResponseBody());
        }
        if(profile==null) return false;
        if(profile.getPremiumUntil() == null) return false;
        return new Date().before(getTime(profile.getPremiumUntil()));
    }

    public static InlineResponse2003 profile;
    static void showProfile(){
        if(profile==null){
            System.out.println("profile hasn't gotten yet!");
        }else{
            System.out.println("username: " + profile.getUsername());
            System.out.println("premium until: " + (isUserPremium() ? profile.getPremiumUntil():"not premium"));
        }
        System.out.println("===============================================");
    };
    public static void profile(){
        Scanner input = new Scanner(System.in);
        if (canRequestServerThenDoIt()) {
            try {
                profile=usersApi.getProfileInfo();
                start = System.currentTimeMillis() / 1000;
                showProfile();
            } catch (ApiException apiException) {
                System.err.println(apiException.getResponseBody());
            }
        } else {
            showProfile();
        }
        userMenuProcess();
    }

    public static Tracks tracks=null;
    static void showTracks(Tracks tracks, String startingString){
        if(tracks==null){
            System.out.println("tracks is empty!");
            System.out.println(startingString + "===============================================");
            return;
        }
        for(Track track: tracks){
            if(!isUserPremium() && track.isIsPremium()) continue;
            System.out.println(startingString + "id: " + track.getId());
            System.out.println(startingString + "name: " + track.getName());
            System.out.println(startingString + "artist: " + track.getArtist());
            System.out.println(startingString + "isPremium: " + track.isIsPremium());
            System.out.println(startingString + "===============================================");
        }
    }

    static void showTracks(List<Track> tracks, String startingString){
        if(tracks==null){
            System.out.println("tracks is empty!");
            System.out.println(startingString + "===============================================");
            return;
        }
        for(Track track: tracks){
            if(track.isIsPremium()==null || (!isUserPremium() && track.isIsPremium())) continue;
            System.out.println(startingString + "*******************************");
            System.out.println(startingString + "id: " + track.getId());
            System.out.println(startingString + "name: " + track.getName());
            System.out.println(startingString + "artist: " + track.getArtist());
            System.out.println(startingString + "isPremium: " + track.isIsPremium());
//            System.out.println(startingString + "===============================================");
        }
        System.out.println(startingString + "*******************************");
    }

    public static void tracks(){
        if (canRequestServerThenDoIt()) {
            try {
                tracks = usersApi.getTracksInfo();
                showTracks(tracks, "");
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.err.println(apiException.getResponseBody());
            }
        } else {
            showTracks(tracks, "");
        }
        userMenuProcess();
    }


    public static void showPlaylists(Playlists playlists){
        if(playlists==null) {
            System.out.println("Playlists is empty");
            return;
        }
        for (Playlist playlist: playlists){
            System.out.println("playlist id: " + playlist.getId());
            System.out.println("playlist name: " + playlist.getName());
            System.out.println("playlist tracks: ");
            showTracks(playlist.getTracks(), "\t");
            System.out.println();
        }
    }
    public static Playlists playlists=null;
    public static void playlists() {
        if(canRequestServerThenDoIt()){
            try {
                playlists=usersApi.getPlaylistsInfo();
                start = System.currentTimeMillis() / 1000;
                showPlaylists(playlists);
            } catch (ApiException apiException) {
                System.err.println(apiException.getResponseBody());
            }
        }else{
            showPlaylists(playlists);
        }
        System.out.println("===============================================");
        userMenuProcess();
    }


    public static void makeNewPlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist name: ");
        String playlistName=in.next();
        try {
            PlaylistsBody playlistsBody = new PlaylistsBody();
            playlistsBody.setName(playlistName);
            usersApi.createPlaylist(playlistsBody);
            System.out.println("===============================================");
            System.out.println(GREEN + "Playlist successfully added:)" + RESET);
            System.out.println("===============================================");
            userMenuProcess();
        } catch (ApiException apiException) {
            if(apiException.getResponseBody().contains("no name provided")){
                System.out.println(RED + "no name provided" + RESET);
            }
            System.out.println("===============================================");
            makeNewPlaylist();
        }
    }

    public static void deletePlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist id you want to delete: ");
        int playlistID=in.nextInt();
        try {
            usersApi.deletePlaylist(playlistID);
            System.out.println("===============================================");
            System.out.println(GREEN + "Playlist successfully deleted:)" + RESET);
            System.out.println("===============================================");
            userMenuProcess();
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println(RED + "no playlist_id provided" + RESET);
            }else if(response.contains("playlist not found")){
                System.out.println(RED + "playlist not found" + RESET);
            }
            System.out.println("===============================================");
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
            System.out.println("===============================================");
            System.out.println(GREEN + "Track successfully added to the playlist:)" +RESET);
            System.out.println("===============================================");
            userMenuProcess();
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println(RED + "no playlist_id provided" + RESET);
            }else if(response.contains("track not found")){
                System.out.println(RED + "track not found" + RESET);
            }else if(response.contains("track already exists in playlist")){
                System.out.println(RED + "track already exists in playlist" + RESET);
            }
            System.out.println("===============================================");
            addTrackToPlaylist();
        }
    }

    public static void removeTrackFromPlaylist(){
        Scanner in=new Scanner(System.in);
        System.out.println("Enter playlist id you want to remove track from: ");
        int playlistID=in.nextInt();
        System.out.println("Enter track id you want to remove: ");
        String trackID=in.next();
        try {
            usersApi.removeTrackFromPlaylist(playlistID, trackID);
            System.out.println("===============================================");
            System.out.println(GREEN + "Track successfully removed from the playlist :)" + RESET);
            System.out.println("===============================================");
            userMenuProcess();
        } catch (ApiException apiException) {
            String response=apiException.getResponseBody();
            if(response.contains("no playlist_id provided")){
                System.out.println(RED + "no playlist_id provided" + RESET);
            }else if(response.contains("track does not exist in playlist")){
                System.out.println(RED + "track does not exist in playlist" + RESET);

            }
            System.out.println("===============================================");
            removeTrackFromPlaylist();
        }
    }

    public static void upgradeToPremium(){
        if(isUserPremium()){
            System.out.println("You are already a premium user!");
            System.out.println("===============================================");
        }
        else{
            try {
                InlineResponse2005 upgradeResponse = usersApi.upgradeToPremium();
                usersApi.getProfileInfo().setPremiumUntil(upgradeResponse.getPremiumUntil());
                System.out.println("Congrats! now you are premium:)");
                System.out.println("===============================================");
            } catch (ApiException apiException) {
                String response = apiException.getResponseBody();
                if(response.contains("try again")){
                    System.out.println(RED + "try again! maybe this time you'll be lucky!" + RESET);
                }
                System.out.println("===============================================");
            }
        }
        userMenuProcess();
    }


    public static List<String> friends=new ArrayList<>();
    public static void getFriends(){
        if(isUserPremium()){
            if(canRequestServerThenDoIt()){
                PremiumUsersApi premiumUsersApi = new PremiumUsersApi(defaultClient);
                try {
                    friends = premiumUsersApi.getFriends();
                    start = System.currentTimeMillis() / 1000;
                    System.out.println("Friends list: ");
                    for(String friend: friends){
                        System.out.println("- " + friend);
                    }
                } catch (ApiException apiException) {
                    System.err.println(apiException.getResponseBody());
                }
            }else{
                System.out.println("Friends list: ");
                for(String friend: friends){
                    System.out.println("- " + friend);
                }
            }
        }else{
            System.out.println("You are not a premium user!");
        }
        System.out.println("===============================================");
        userMenuProcess();
    }

    public static List<String> requests=new ArrayList<>();
    public static void friendRequests(){
        if(isUserPremium()){
            if(canRequestServerThenDoIt()){
                PremiumUsersApi premiumUsersApi = new PremiumUsersApi(defaultClient);
                try {
                    requests = premiumUsersApi.getFriendRequests();
                    start = System.currentTimeMillis() / 1000;
                    System.out.println("Friend requests: ");
                    System.out.println("*******************************");
                    for(String req: requests){
                        System.out.println("- " + req);
                    }
                } catch (ApiException apiException) {
                    System.err.println(apiException.getResponseBody());
                }
            }
            else{
                System.out.println("Friend requests: ");
                System.out.println("*******************************");
                for(String req: requests){
                    System.out.println("- " + req);
                }
            }
        }else{
            System.out.println("You are not a premium user!");
        }
        System.out.println("===============================================");
        userMenuProcess();
    }

    public static void addFriend(){
        if(isUserPremium()){
            Scanner in=new Scanner(System.in);
            System.out.println("Enter the username you want to add friend: ");
            String username = in.nextLine();
            PremiumUsersApi premiumUsersApi = new PremiumUsersApi(defaultClient);
            try {
                InlineResponse2006 response = premiumUsersApi.addFriend(username);
                if(response.getMessage()==null){
                    System.out.println("you are already friends");
                }
                else{
                    System.out.println(response.getMessage());
                }
            } catch (ApiException apiException) {
                String reponse = apiException.getResponseBody();
                if(reponse.contains("invalid friend_username")){
                    System.out.println(RED + "invalid friend_username" + RESET);
                }
            }
        }
        else{
            System.out.println("You are not a premium user!");
        }
        System.out.println("===============================================");
        userMenuProcess();
    }


    public static HashMap<String, Playlists> friendsPlaylists=new HashMap<>();
    public static void friendPlaylist(){
        if(isUserPremium()){
            Scanner in=new Scanner(System.in);
            System.out.println("Enter the friend username you want to see its playlists: ");
            String username = in.nextLine();
            if(canRequestServerThenDoIt()){
                PremiumUsersApi premiumUsersApi = new PremiumUsersApi(defaultClient);
                try {
                    Playlists playlists = premiumUsersApi.getFriendPlaylists(username);
                    start = System.currentTimeMillis() / 1000;
                    friendsPlaylists.put(username, playlists);
                    System.out.println(username + " playlists are: ");
                    System.out.println("*******************************");
                    showPlaylists(playlists);
                    System.out.println("===============================================");
                    userMenuProcess();
                } catch (ApiException apiException) {
                    String response = apiException.getResponseBody();
                    if(response.contains("invalid friend_username")){
                        System.out.println(RED + "invalid friend_username" + RESET);
                    }
                    else if(response.contains("friend not found")){
                        System.out.println(RED + "friend not found" + RESET);
                    }
                    System.out.println("===============================================");
                    friendPlaylist();
                }
            }
            else{
                System.out.println(username + " playlists are: ");
                System.out.println("*******************************");
                showPlaylists(playlists);
                System.out.println("===============================================");
                userMenuProcess();
            }
        }else{
            System.out.println("You are not a premium user!");
            System.out.println("===============================================");
            userMenuProcess();
        }

    }

    public static void userMenuProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("1-Profile\n2-Tracks\n3-Playlists\n4-Make a playlist\n" +
                "5-Delete a playlist\n6-Add track to a playlist\n7-Remove track from a playlist\n8-Upgrade to premium\n" +
                "9-Friends list\n10-Friend requests\n11-Add friend\n12-Friend playlist\n13-Log out");
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
            case 10: friendRequests(); break;
            case 11: addFriend(); break;
            case 12: friendPlaylist(); break;
            case 13:
                System.out.println("===============================================");
                main(null);
                break;
            default:
                System.out.println("===============================================");
                System.out.println(RED + "wrong input! do it again!" + RESET);
                System.out.println("===============================================");
                userMenuProcess();
        }
    }

    public static void main(String[] args) {
        authAPIKey();
        Scanner input = new Scanner(System.in);
        System.out.println(BLUE + "Welcome to Spotify!" + RESET);
        System.out.println("===============================================");
        System.out.println("1-Login\n2-Signup");
        System.out.println("===============================================");
        System.out.print("-> ");
        int choice = input.nextInt();
        switch (choice){
            case 1: loginProcess(); break;
            case 2: signUpProcess(); break;
        }
    }
}
