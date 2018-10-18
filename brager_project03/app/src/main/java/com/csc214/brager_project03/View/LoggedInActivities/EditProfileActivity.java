package com.csc214.brager_project03.View.LoggedInActivities;

import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csc214.brager_project03.Controller.PictureController;
import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.Model.User;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region KEYS
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_HOMETOWN = "hometown";
    public static final String KEY_BIRTH_DATE = "birth_date";
    public static final String KEY_BIO = "bio";
    public static final String KEY_USER_ID = "user_id"; //Key for user_id -> used for entire app
    public static final String KEY_IMAGE_PATH = "image_path";
    static final int REQUEST_TAKE_PHOTO = 1;
    //endregion

    //region UI references.
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mHometownEditText;
    private EditText mBirthDateEditText;
    private EditText mBioEditText;

    private TextView mBarFirstNameView;
    private TextView mBarLastNameView;
    private ImageView mBarProfileImageView;

    private ImageView mProfilePicImageView;
    //endregion

    //region Database
    private Database db;

    //Information for Database
    private User user;
    private int userId;
    private String mCurrentPhotoPath;
    //endregion

    //region DatePicker stuff
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout_edit_profile);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mFirstNameEditText = findViewById(R.id.first_name_edit_text);
        mLastNameEditText = findViewById(R.id.last_name_edit_text);
        mHometownEditText = findViewById(R.id.hometown_edit_text);
        mBirthDateEditText = findViewById(R.id.birth_date_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mProfilePicImageView = findViewById(R.id.profile_pic_image_view);

        mBarFirstNameView = findViewById(R.id.bar_first_name_view);
        mBarLastNameView = findViewById(R.id.bar_last_name_view);
        mBarProfileImageView = findViewById(R.id.bar_profile_image_view);

        mBirthDateEditText.setInputType(InputType.TYPE_NULL);
        mBirthDateEditText.requestFocus();

        if (savedInstanceState != null) { //if a Bundle has been passed through the function
            mFirstNameEditText.setText(savedInstanceState.getString(KEY_FIRST_NAME));
            mLastNameEditText.setText(savedInstanceState.getString(KEY_LAST_NAME));
            mHometownEditText.setText(savedInstanceState.getString(KEY_HOMETOWN));
            mBirthDateEditText.setText(savedInstanceState.getString(KEY_BIRTH_DATE));
            mBioEditText.setText(savedInstanceState.getString(KEY_BIO));
            mCurrentPhotoPath = savedInstanceState.getString(KEY_IMAGE_PATH);
        }

        db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Intent intent = getIntent();
        userId = intent.getIntExtra(KEY_USER_ID, -1);

        user = db.userDao().getUserFromUserId(userId);

        Log.v(LoginActivity.TAG, "userId = " + userId);

        mFirstNameEditText.setText(user.getFirstName());
        mLastNameEditText.setText(user.getLastName());
        mHometownEditText.setText(user.getHometown());
        mBirthDateEditText.setText(user.getDob());
        mBioEditText.setText(user.getBio());
        if (user.getProfilePicPath() != null) { // we want the default picture of the orca to appear if there is no picture for the user
            mProfilePicImageView.setImageBitmap(PictureController.getScaledBitmap(user.getProfilePicPath(),
                    mProfilePicImageView.getWidth(), mProfilePicImageView.getHeight()));
        }

        if (!user.isJustRegistered()) { //Keep the profile bar as firstname lastname until the user registers
            mBarFirstNameView.setText(user.getFirstName());
            mBarLastNameView.setText(user.getLastName());
            if (user.getProfilePicPath() != null) {
                mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(user.getProfilePicPath(),
                        mBarProfileImageView.getWidth(), mBarProfileImageView.getHeight()));
            }
        }

        Button mUpdateProfilePicButton = findViewById(R.id.update_profile_pic_button);
        mUpdateProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfilePic();
            }
        });

        Button mSaveChangesButton = findViewById(R.id.save_changes_button);
        mSaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        Button mCancelButton = findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        if (user.isJustRegistered()) { //if user is not just registered then the birthdate editText is unchangeable
            Calendar newCalendar = Calendar.getInstance();

            mBirthDateEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    datePickerDialog.show();
                }
            });

            datePickerDialog = new DatePickerDialog(this, new android.app.DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    mBirthDateEditText.setText(dateFormatter.format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            mBirthDateEditText.setEnabled(false);
        }

    }


    private void setUpProfile() {
        //make sure that all of the required fields are filled in
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.borderColor));
        ViewCompat.setBackgroundTintList(mFirstNameEditText, colorStateList);
        ViewCompat.setBackgroundTintList(mLastNameEditText, colorStateList);
        ViewCompat.setBackgroundTintList(mBirthDateEditText, colorStateList);
        ViewCompat.setBackgroundTintList(mHometownEditText, colorStateList);

        colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));

        if (mFirstNameEditText.getText().length() == 0) { //check that the first name field is filled
            ViewCompat.setBackgroundTintList(mFirstNameEditText, colorStateList);
            Toast.makeText(EditProfileActivity.this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
        } else if (mLastNameEditText.getText().length() == 0) { //check that last name field is filled
            ViewCompat.setBackgroundTintList(mLastNameEditText, colorStateList);
            Toast.makeText(EditProfileActivity.this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
        } else if (mBirthDateEditText.getText().length() == 0) { //check that birth date password field is filled
            ViewCompat.setBackgroundTintList(mBirthDateEditText, colorStateList);
            Toast.makeText(EditProfileActivity.this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
        } else if (mHometownEditText.getText().length() == 0) { //check that the hometown field is filled
            ViewCompat.setBackgroundTintList(mHometownEditText, colorStateList);
            Toast.makeText(EditProfileActivity.this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
        } else {
            user.setJustRegistered(false);
            updateProfile();
        }
    }

    /**
     * Populate the database with the data gathered from the user
     */
    private void updateProfile() {
        user.setFirstName(mFirstNameEditText.getText().toString());
        user.setLastName(mLastNameEditText.getText().toString());
        user.setHometown(mHometownEditText.getText().toString());
        user.setBio(mBioEditText.getText().toString());
        user.setDob(mBirthDateEditText.getText().toString());
        user.setProfilePicPath(mCurrentPhotoPath);

        mBarFirstNameView.setText(user.getFirstName());
        mBarLastNameView.setText(user.getLastName());
        if (mCurrentPhotoPath != null) {
            mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(mCurrentPhotoPath,
                    mBarProfileImageView.getWidth(),
                    mBarProfileImageView.getHeight()));
        }

        db.userDao().updateUsers(user);

        Toast.makeText(EditProfileActivity.this, R.string.profile_update_complete, Toast.LENGTH_SHORT).show();
    }

    /*
     * Open up the camera
     */
    private void updateProfilePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(EditProfileActivity.this, "Error creating file for picture",
                        Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.csc214.brager_project03.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*
     * Create the image file for the camera
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //String imageFileName = "jpg_" + UUID.randomUUID();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",          /* suffix */
                storageDir              /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*
     * Save the changes made based on whether the user just registered or not.
     */
    private void saveChanges() {
        if (user.isJustRegistered()) {
            setUpProfile();
        } else {
            updateProfile();
        }
    }

    /*
     * Cancel the changes made since the last save
     */
    private void cancel() {

        if (user.isJustRegistered()) {
            mFirstNameEditText.setText("");
            mLastNameEditText.setText("");
            mHometownEditText.setText("");
            mBioEditText.setText("");
            mBirthDateEditText.setText("");
            mProfilePicImageView.setImageDrawable(getResources().getDrawable(R.drawable.orca));
        } else {
            mFirstNameEditText.setText(user.getFirstName());
            mLastNameEditText.setText(user.getLastName());
            mHometownEditText.setText(user.getHometown());
            mBirthDateEditText.setText(user.getDob());
            mBioEditText.setText(user.getBio());
            if (user.getProfilePicPath() != null) {
                mProfilePicImageView.setImageBitmap(PictureController.getScaledBitmap(
                        user.getProfilePicPath(), mProfilePicImageView.getWidth(),
                        mProfilePicImageView.getHeight()));
            }
        }
    }

    /*
     * Save the data so that it can be displayed correctly when onCreate is called
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //put the values of the text into the outState
        outState.putString(KEY_FIRST_NAME, mFirstNameEditText.getText().toString());
        outState.putString(KEY_LAST_NAME, mLastNameEditText.getText().toString());
        outState.putString(KEY_HOMETOWN, mHometownEditText.getText().toString());
        outState.putString(KEY_BIRTH_DATE, mBirthDateEditText.getText().toString());
        outState.putString(KEY_BIO, mBioEditText.getText().toString());
        outState.putString(KEY_IMAGE_PATH, mCurrentPhotoPath);
    }

    /*
     * On activity result needed for the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (mCurrentPhotoPath != null) {
                    Log.v(LoginActivity.TAG, "Width = " + mProfilePicImageView.getWidth() + ", Height = " + mProfilePicImageView.getHeight());
                    mProfilePicImageView.setImageBitmap(PictureController.getScaledBitmap(mCurrentPhotoPath,
                            mProfilePicImageView.getWidth(), mProfilePicImageView.getHeight()));
                }
            }
        }
    }


    //region autogen drawer stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout_edit_profile);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (user.isJustRegistered()) {
                Toast.makeText(EditProfileActivity.this, R.string.error_profile_incomplete, Toast.LENGTH_LONG).show();
            } else {
                //up navigtion to feed activity.
                Intent intent = new Intent(EditProfileActivity.this, FeedActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                startActivity(intent);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (user.isJustRegistered()) {
            DrawerLayout drawer =  findViewById(R.id.drawer_layout_edit_profile);
            drawer.closeDrawer(GravityCompat.START);
            Toast.makeText(EditProfileActivity.this, R.string.error_profile_incomplete, Toast.LENGTH_LONG).show();
            return true;
        } else {
            if (id == R.id.nav_view_feed) {
                Intent intent = new Intent(EditProfileActivity.this, FeedActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                startActivity(intent);
            } else if (id == R.id.nav_new_post) {
                Intent intent = new Intent(EditProfileActivity.this, NewPostActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                startActivity(intent);
            } else if (id == R.id.nav_find_user) {
                Intent intent = new Intent(EditProfileActivity.this, FindUserActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                startActivity(intent);
            } else if (id == R.id.nav_edit_profile) {
                //Do nothing cause you're here!
            } else if (id == R.id.nav_log_out) {
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.putExtra(KEY_USER_ID, userId);
                startActivity(intent);
            }
            DrawerLayout drawer =  findViewById(R.id.drawer_layout_edit_profile);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
    //endregion
}