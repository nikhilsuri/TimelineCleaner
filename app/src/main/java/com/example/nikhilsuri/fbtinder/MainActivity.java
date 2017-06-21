package com.example.nikhilsuri.fbtinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.nikhilsuri.fbtinder.R.id.login_button;

public class MainActivity extends FragmentActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private String firstName, lastName, email, birthday, gender;
    private URL profilePicture;
    private String userId;
    private String TAG = "LoginActivity";
    JSONArray postsArray;
    private FirebaseAuth fireAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Log.e("Main", "create");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        fireAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> callback = getFaceBookCallBack();
        boolean fbLogin = isLoggedIn();
        //if user is not logged in from facebook then trigger fb login
        //else check if it exixts in fire base or not
        if (fbLogin == false) {
            setContentView(R.layout.activity_main);
            loginButton = (LoginButton) findViewById(login_button);
            loginButton.setReadPermissions("email", "user_birthday", "user_posts");
            loginButton.registerCallback(callbackManager, callback);

        } else {
            saveFacebookCredentialsInFirebase(AccessToken.getCurrentAccessToken());
        }


    }

    private FacebookCallback<LoginResult> getFaceBookCallBack() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e(TAG, object.toString());
                        Log.e(TAG, response.toString());
                        try {
                            userId = object.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //store user in firebase data base if not exists and calls to swipe activity with proper user present
                        saveFacebookCredentialsInFirebase(loginResult.getAccessToken());

//                            profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
//                            Log.e(TAG, profilePicture.toString());
//                            if (object.has("first_name"))
//                                firstName = object.getString("first_name");
//                            if (object.has("last_name"))
//                                lastName = object.getString("last_name");
//

                        // getPosts();
                        // finish();

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                signOut();
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        };
    }





    @Override
    public void onStart() {
        super.onStart();
        //check if user is signed it or not
        //if signed in then call swipe activity customized
        //  Log.e(TAG, "start");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    private void saveFacebookCredentialsInFirebase(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        fireAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fireAuth.getCurrentUser();
                            Log.d(TAG, user.toString());
                            //start Swipe activity
                            Intent main = new Intent(MainActivity.this, SwipeActivity.class);
                            startActivity(main);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            signOut();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    public void signOut() {
        fireAuth.signOut();
        LoginManager.getInstance().logOut();

    }
}
