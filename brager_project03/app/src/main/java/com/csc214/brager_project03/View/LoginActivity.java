package com.csc214.brager_project03.View;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoggedInActivities.EditProfileActivity;
import com.csc214.brager_project03.View.LoggedInActivities.FeedActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    public static final String TAG = "Carolina"; //Tag for Log
    public static final String KEY_USERNAME = "username"; //Key for username
    public static final String KEY_USER_ID="user_id"; //Key for user_id -> used for entire app

    // UI references.
    private EditText mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.username);

        if (savedInstanceState != null) { //if a Bundle has been passed through the function
            mUsername.setText(savedInstanceState.getString(KEY_USERNAME));
        }

        Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
     * Try logging in.
     */
    private void attemptLogin() {
        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        mUsername = findViewById(R.id.username);
        EditText mPassword = findViewById(R.id.password);

        TextView error = findViewById(R.id.error_text_view);

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.borderColor));
        ViewCompat.setBackgroundTintList(mUsername, colorStateList);
        ViewCompat.setBackgroundTintList(mPassword, colorStateList);

        if(mUsername.getText().length() == 0 ){ //check that the username field is filled
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mUsername, colorStateList);
            error.setText(R.string.error_field_required);
        }
        else if(mPassword.getText().length() == 0){ //check that password field is filled
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mPassword, colorStateList);
            error.setText(R.string.error_field_required);
        }
        else { //otherwise everything is good to go, register the user
            login();
        }
    }

    /*
     * if all of the fields have been filled out properly, check the database to try to log in
     */
    private void login(){
        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        mUsername = findViewById(R.id.username);
        EditText mPassword = findViewById(R.id.password);

        TextView error = findViewById(R.id.error_text_view);

        String username = mUsername.getText().toString();

        if(db.userDao().getPassword(username).size() == 0){
            error.setText(R.string.error_username_dne);
        }
        else if(!db.userDao().getPassword(username).get(0).equals(mPassword.getText().toString())){
            error.setText(R.string.error_invalid_login);
        }
        else{
            int userId = db.userDao().getUserIdFromUsername(username);

            Log.v(TAG, "userId = " + userId);

            if(db.userDao().getUserFromUsername(username).isJustRegistered()){
                Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                Log.v(TAG, "userId = " + userId);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                Log.v(TAG, "userId = " + userId);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onBackPressed() {
        //Don't allow the user to navigate with the back bar. Keeps user from going to logged in activity
    }

    // Save the data so that it can be displayed correctly when onCreate is called
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        //put the values of the text into the outState
        outState.putString(KEY_USERNAME, String.valueOf(mUsername.getText()));
    }
}
