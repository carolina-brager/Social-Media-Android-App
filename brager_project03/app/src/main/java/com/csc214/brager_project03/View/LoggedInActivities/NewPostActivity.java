package com.csc214.brager_project03.View.LoggedInActivities;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csc214.brager_project03.Controller.PictureController;
import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.Model.Post;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region KEYS
    public static final String KEY_USER_ID="user_id"; //Key for user_id -> used for entire app
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_POST_IMAGE="post_image";
    static final int REQUEST_TAKE_PHOTO = 1;
    //endregion

    //region UI References
    private TextView mTitle;
    private TextView mContent;
    private ImageView mPostImage;

    private TextView mBarFirstNameView;
    private TextView mBarLastNameView;
    private ImageView mBarProfileImageView;
    //endregion

    //region Database
    private Database db;
    //USER ID
    private int userId;
    private String mCurrentPhotoPath;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_new_post);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra(KEY_USER_ID, -1);

        mTitle = findViewById(R.id.title_edit_text);
        mContent = findViewById(R.id.content_edit_text);
        mPostImage = findViewById(R.id.post_image_view);

        mBarFirstNameView = findViewById(R.id.bar_first_name_view);
        mBarLastNameView =  findViewById(R.id.bar_last_name_view);
        mBarProfileImageView = findViewById(R.id.bar_profile_image_view);

        if(savedInstanceState != null){
            mTitle.setText(savedInstanceState.getString(KEY_TITLE));
            mContent.setText(savedInstanceState.getString(KEY_CONTENT));
            if(savedInstanceState.getString(KEY_POST_IMAGE) != null) {
            mPostImage.setImageBitmap(PictureController.getScaledBitmap(
                    savedInstanceState.getString(KEY_POST_IMAGE),
                    mPostImage.getWidth(), mPostImage.getHeight()));
            }
            userId = savedInstanceState.getInt(KEY_USER_ID);

        }

        db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        mBarFirstNameView.setText(db.userDao().getUserFromUserId(userId).getFirstName());
        mBarLastNameView.setText(db.userDao().getUserFromUserId(userId).getLastName());
        if(db.userDao().getUserFromUserId(userId).getProfilePicPath() != null) {
        mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(
                db.userDao().getUserFromUserId(userId).getProfilePicPath(),
                mBarLastNameView.getWidth(), mBarLastNameView.getHeight()));
        }

        Button setImageButton = findViewById(R.id.add_image_button);
        setImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage();
            }
        });

        Button cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        final Button saveChanges = findViewById(R.id.create_post_button);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Use the camera
     */
    private void setImage(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(NewPostActivity.this, "Error creating file for picture",
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

    /**
     * Return all of the vies to the way they were the last time the save button was pressed
     */
    private void cancel(){
        mTitle.setText("");
        mContent.setText("");
        mPostImage.setImageBitmap(PictureController.getScaledBitmap("", 0, 0));
        mCurrentPhotoPath = null; //clear the mCurrentPhotoPath value
    }

    /*
     * Save the post the database and then return to the feed activity to view that post
     */
    private void saveChanges(){

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.borderColor));
        ViewCompat.setBackgroundTintList(mTitle, colorStateList);

        if(mTitle.getText().length() == 0){
            colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorIncorrect));
            ViewCompat.setBackgroundTintList(mTitle, colorStateList);
            Toast.makeText(NewPostActivity.this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setPostId(db.postDao().lastId()+1);
        post.setTitle(mTitle.getText().toString());
        post.setText(mContent.getText().toString());
        post.setUserId(userId);
        post.setImagePath(mCurrentPhotoPath);

        db.postDao().insertPosts(post);
        Toast.makeText(NewPostActivity.this, R.string.post_created, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(NewPostActivity.this, FeedActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        startActivity(intent);

    }

    /*
    * On activity result needed for the camera
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(mCurrentPhotoPath != null) {
                mPostImage.setImageBitmap(PictureController.getScaledBitmap(mCurrentPhotoPath,
                        mPostImage.getWidth(), mPostImage.getHeight()));
                }
            }
        }
    }

    // Save the data so that it can be displayed correctly when onCreate is called
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //put the values of the text into the outState
        outState.putInt(KEY_USER_ID, userId);
        outState.putString(KEY_TITLE, mTitle.getText().toString());
        outState.putString(KEY_CONTENT, mContent.getText().toString());
        outState.putString(KEY_POST_IMAGE, mCurrentPhotoPath);
    }


    //region autogen drawer stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_new_post);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(NewPostActivity.this, FeedActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view_feed) {
            Intent intent = new Intent(NewPostActivity.this, FeedActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_new_post) {
            //Don't go anywhere cause you're already there!
        } else if (id == R.id.nav_find_user) {
            Intent intent = new Intent(NewPostActivity.this, FindUserActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(NewPostActivity.this, EditProfileActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            Intent intent = new Intent(NewPostActivity.this, LoginActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_new_post);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion
}
