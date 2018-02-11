package com.example.nikhilsuri.fbtinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.nikhilsuri.fbtinder.R.id.login_button;

public class MainActivity extends FragmentActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String TAG = "LoginActivity";
    private FirebaseAuth fireAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    static final int SWIPE_ACTIVITY_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookInitialization();
        boolean fbLogin = isLoggedIn();
        if (fbLogin == false) {
            loginWithFacebook();
        } else {
            saveFacebookCredentialsInFirebase(AccessToken.getCurrentAccessToken());
        }


    }

    private void loginWithFacebook() {
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(login_button);
        loginButton.setReadPermissions("email", "user_birthday", "user_posts");
        loginButton.registerCallback(callbackManager, getFaceBookCallBack());
    }

    private void facebookInitialization() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        fireAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }


    private FacebookCallback<LoginResult> getFaceBookCallBack() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                findViewById(login_button).setVisibility(View.INVISIBLE);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e(TAG, object.toString());
                        Log.e(TAG, response.toString());
                        saveFacebookCredentialsInFirebase(loginResult.getAccessToken());
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (SWIPE_ACTIVITY_REQUEST_CODE == requestCode) {
            //logout
            if (resultCode == -1) {
                signOut();
            }

        }
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
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            signOut();
                        } else {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final Profile profile = Profile.getCurrentProfile();
                            final FirebaseUser user = fireAuth.getCurrentUser();
                            myRef.child(Constants.USERSTAG).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        // user already exists
                                        startActivity();
                                    } else {
                                        //get fb details and store user
                                        Map<String, Object> userValues = new HashMap<>();
                                        userValues.put(Constants.DISPLAY_NAME, user.getDisplayName());
                                        userValues.put(Constants.PROFILE_PIC, profile.getProfilePictureUri(1000, 1000).toString());
                                        SwipedOutPosts swipedOutPosts = new SwipedOutPosts();
                                        SwipedOutPosts deletedPosts = new SwipedOutPosts();
                                        swipedOutPosts.getSwipedOutPostHashMap().put("Random", new SwipedOutPost("DUMMY", new Date()));
                                        userValues.put(Constants.CHECKED_OUT_POSTS, swipedOutPosts);
                                        deletedPosts.getSwipedOutPostHashMap().put("Random", new SwipedOutPost("DUMMY", new Date()));
                                        userValues.put(Constants.DELETED_POSTS, deletedPosts);
                                        myRef.child(Constants.USERSTAG).child(user.getUid()).setValue(userValues);
                                        startActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

    }

    public void startActivity() {
        Intent main = new Intent(MainActivity.this, SwipeActivity.class);
        startActivityForResult(main, SWIPE_ACTIVITY_REQUEST_CODE);
    }


    public void signOut() {
        fireAuth.signOut();
        LoginManager.getInstance().logOut();
        loginWithFacebook();

    }
}
