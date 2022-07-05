import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.AuthLoginBody;
import io.swagger.client.model.AuthSignupBody;
import io.swagger.client.model.InlineResponse2001;

import java.util.Scanner;

public class Main {
    public static final String MY_API_KEY = "16ddd7aa2436ea67ae85120472980455a46a407a";
    public static ApiClient defaultClient;
    public static DefaultApi defaultApi;
    public static AuthApi authApi;
    public static UsersApi usersApi;
    public static User currentUser;
    public static long start = 0;
    public static String tracks;
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
            currentUser = new User(username,password);
            System.out.println("You Successfully logged in");
            currentUser.token = token;
            defaultClient.setAccessToken(currentUser.token);
            OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
            bearerAuth.setAccessToken(currentUser.token);

            userMenuProcess();
            currentUser = new User(username, password);
            currentUser.token = token;
        } catch (ApiException apiException) {
            String errorResponse = apiException.getResponseBody();
            System.out.println(errorResponse);
            if (errorResponse.contains("invalid username or password")) {
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
        System.out.println(Convertor.doesStringMatchWith(password, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$"));
        if(Convertor.doesStringMatchWith(password, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")){
            try {
                AuthSignupBody authSignupBody = new AuthSignupBody();
                authSignupBody.setUsername("admin");
                authSignupBody.setPassword("1234@Sa!");
                System.out.println(authApi.signUp(authSignupBody));
            } catch (ApiException apiException) {
                String errorResponse = apiException.getResponseBody();
                System.err.println(errorResponse);
                if (errorResponse.contains("no username provided")) {
                    signUpProcess();
                }
                else if(errorResponse.contains("username already taken")){
                    signUpProcess();
                }
            }
        }
        else{
            System.err.println("password length must be at least 8 and include uppercase, lowercase and number!");
            signUpProcess();
        }
    }


    public static void userMenuProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("1-Profile\n2-Tracks");
        int choice = input.nextInt();
        if (choice == 1) {
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime - start > 20 || isFirstCall) {
                try {
                    System.out.println(usersApi.getProfileInfo().toString());
                    start = System.currentTimeMillis() / 1000;
                    int exit = input.nextInt();
                    isFirstCall = false;
                    userMenuProcess();
                } catch (ApiException apiException) {
                    System.out.println(apiException.getResponseBody());
                    isFirstCall = false;
                }
            } else {
                //cached data
                System.out.println();
            }
        } else if (choice == 2) {
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime - start > 20) {
                try {
                    tracks = usersApi.getTracksInfo().toString();
                    System.out.println(tracks);
                    start = System.currentTimeMillis() / 1000;
                } catch (ApiException apiException) {
                    System.out.println(apiException.getResponseBody());
                }
            } else {
                System.out.println(tracks);
            }
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
