package com.csc214.brager_project03.View;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.Model.User;
import com.csc214.brager_project03.R;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String KEY_USERNAME = "username"; //Key for username

    // UI references.
    private EditText mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mUsername = findViewById(R.id.username);

        if (savedInstanceState != null) { //if a Bundle has been passed through the function
            mUsername.setText(savedInstanceState.getString(KEY_USERNAME));
        }

        Button mSignInButton =  findViewById(R.id.create_account_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        Button mRegisterButton =  findViewById(R.id.cancel_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
     * check that all of the fields have been filled out correctly. If they have, called register()
     */
    private void attemptRegistration() {
        EditText mPassword = findViewById(R.id.password);
        EditText mConfirmPassword = findViewById(R.id.confirm_password);
        mUsername = findViewById(R.id.username);
        TextView error = findViewById(R.id.error_text_view);

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.borderColor));
        ViewCompat.setBackgroundTintList(mUsername, colorStateList);
        ViewCompat.setBackgroundTintList(mPassword, colorStateList);
        ViewCompat.setBackgroundTintList(mConfirmPassword, colorStateList);

        if(mUsername.getText().length() == 0 ){ //check that the username field is filled
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mUsername, colorStateList);
            error.setText(R.string.error_field_required);
        }
        else if(mUsername.getText().length() < 5){ //check that username is longer than 5
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mUsername, colorStateList);
            error.setText(R.string.error_invalid_username);
        }
        else if(mPassword.getText().length() == 0){ //check that password field is filled
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mPassword, colorStateList);
            error.setText(R.string.error_field_required);
        }
        else if(mPassword.getText().length() < 5){ //check that password is longer than 5
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mPassword, colorStateList);
            error.setText(R.string.error_invalid_password);
        }
        else if(mConfirmPassword.getText().length() == 0){ //check that confirm password field si filled
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mConfirmPassword, colorStateList);
            error.setText(R.string.error_field_required);
        }
        //check that password matches confirmPassword
        else if(!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())){
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mConfirmPassword, colorStateList);
            error.setText(R.string.error_passwords_do_not_match);
        }
        else { //otherwise everything is good to go, register the user
            register();
        }

    }


    /*
     * after all the fields have been checked for correctness you can register the user
     */
    private void register()
    {

        EditText mPassword = findViewById(R.id.password);
        mUsername = findViewById(R.id.username);

        TextView error = findViewById(R.id.error_text_view);

        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Log.v(LoginActivity.TAG, "username count: " +  db.userDao().getUsernameCount(mUsername.getText().toString()) );
        if(db.userDao().getUsernameCount(mUsername.getText().toString())>=1){
            error.setText(R.string.error_username_taken);
            return;
        }
        User user = new User();
        user.setUserId(db.userDao().lastId()+1);
        user.setUsername(mUsername.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.setJustRegistered(true);

        db.userDao().insertUsers(user);
        Toast.makeText(CreateAccountActivity.this, R.string.registration_complete, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        //put the values of the text into the outState
        outState.putString(KEY_USERNAME, String.valueOf(mUsername.getText()));
    }
}
