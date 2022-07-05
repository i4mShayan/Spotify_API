import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        System.out.println(profile.getPremiumUntil());
        return new Date().before(getTime(profile.getPremiumUntil()));
    }

    public static InlineResponse2003 profile;
    static void showProfile(){
        System.out.println("=================================");
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
    static void showTracks(){
        for(Track track: tracks){
            if(isUserPremium() != track.isIsPremium()) continue;
            System.out.println("=================================");
            System.out.println("name :" + track.getName());
            System.out.println("artist :" + track.getArtist());
            System.out.println("name :" + track.isIsPremium());
        }
    };
    public static void tracks(){
        if (canRequestServerThenDoIt()) {
            try {
                tracks = usersApi.getTracksInfo();
                showTracks();
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
            }
        } else {
            showTracks();
        }
    }

    public static void userMenuProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("1-Profile\n2-Tracks");
        int choice = input.nextInt();
        switch (choice){
            case 1: profile(); break;
            case 2: tracks(); break;
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
