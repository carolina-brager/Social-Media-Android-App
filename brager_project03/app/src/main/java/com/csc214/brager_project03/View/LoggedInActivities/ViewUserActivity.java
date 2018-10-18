package com.csc214.brager_project03.View.LoggedInActivities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.csc214.brager_project03.Controller.BirthDate;
import com.csc214.brager_project03.Controller.PictureController;
import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.Model.Favorite;
import com.csc214.brager_project03.Model.Post;
import com.csc214.brager_project03.Model.User;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoginActivity;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.util.List;


public class ViewUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region KEYS
    public static final String KEY_USER_ID="user_id"; //Key for user_id -> used for entire app
    public static final String KEY_POST_CREATOR_ID="post_creator_id"; //Key for user_id -> used for entire app
    //endregion

    //regionUI References
    private ImageView mProfilePicImageView;
    private ImageView mFavoriteButton;
    private TextView mFirstNameView;
    private TextView mLastNameView;
    private TextView mHometownView;
    private TextView mAgeView;
    private TextView mBioView;

    private TextView mBarFirstNameView;
    private TextView mBarLastNameView;
    private ImageView mBarProfileImageView;
    //endregion


    //region Database
    private Database db;
    //USER ID
    private int userId;
    //USER ID OF CURRENT PROFILE
    private int thisUserId;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_view_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra(KEY_USER_ID, -1);
        thisUserId = intent.getIntExtra(KEY_POST_CREATOR_ID, -1);

        db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        User thisUser = db.userDao().getUserFromUserId(thisUserId);

        mProfilePicImageView = findViewById(R.id.profile_pic_image_view);
        mFirstNameView = findViewById(R.id.first_name_text_view);
        mLastNameView = findViewById(R.id.last_name_text_view);
        mAgeView = findViewById(R.id.birth_date_text_view);
        mHometownView = findViewById(R.id.hometown_text_view);
        mBioView = findViewById(R.id.bio_text_view);
        mFavoriteButton = findViewById(R.id.profile_favorite_button);

        mBarFirstNameView = findViewById(R.id.bar_first_name_view);
        mBarLastNameView =  findViewById(R.id.bar_last_name_view);
        mBarProfileImageView = findViewById(R.id.bar_profile_image_view);

        if(thisUser.getProfilePicPath()!=null) {
            mProfilePicImageView.setImageBitmap(PictureController.getScaledBitmap(
                    thisUser.getProfilePicPath(), mProfilePicImageView.getWidth(),
                    mProfilePicImageView.getHeight()));
        }
        mFirstNameView.setText(thisUser.getFirstName());
        mLastNameView.setText(thisUser.getLastName());
        mHometownView.setText(thisUser.getHometown());

        mBarFirstNameView.setText(db.userDao().getUserFromUserId(userId).getFirstName());
        mBarLastNameView.setText(db.userDao().getUserFromUserId(userId).getLastName());
        if(db.userDao().getUserFromUserId(userId).getProfilePicPath()!=null) {
        mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(
                db.userDao().getUserFromUserId(userId).getProfilePicPath(),
                mBarProfileImageView.getWidth(),
                mBarProfileImageView.getHeight()));
        }


        if(db.favoriteDao().findFavoriteUserIdFromUserId(userId).contains(thisUserId)){
            mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }
        else{
            mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
        }

        //Calculate the birthdate using the BirthDate class
        BirthDate birthDate = parseDob(thisUser.getDob());

        //use LocalDate from joda time to make age calculations
        LocalDate birthday = new LocalDate(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay());
        LocalDate now = LocalDate.now();
        Years age = Years.yearsBetween(birthday, now);
        String ageText = "Age: " + (age.getYears());
        mAgeView.setText(ageText);

        mBioView.setText(thisUser.getBio());
        ListView listView = findViewById(R.id.list);
        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        //set the on click listeneter for the heart imageView
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                        "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

                if(db.favoriteDao().findFavoriteUserIdFromUserId(userId).contains(thisUserId)){
                    mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                    Favorite favorite = db.favoriteDao().getFavoriteBetweenTwo(userId, thisUserId);
                    db.favoriteDao().deleteFavorites(favorite);
                }
                else{
                    mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                    Favorite favorite = new Favorite();
                    favorite.setFavoriteId(db.favoriteDao().lastId()+1);
                    favorite.setFavoriteUserId(thisUserId);
                    favorite.setUserId(userId);
                    db.favoriteDao().insertFavorites(favorite);
                }
            }
        });

    }

    /*
     * Return a birthdate from the given string
     */
    private BirthDate parseDob(String dob){
        //FORMAT = dd-MM-yyyy
        String day = dob.substring(0,2);
        String month = dob.substring(3,5);
        String year = dob.substring(6);

        Log.v(LoginActivity.TAG, "Day = " + day + "; Month = " + month + "; Year = " + year);

        BirthDate output = new BirthDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return output;
    }

    //region CustomAdapter Class
    /*
     * Populate the listView with the values from the database
     */
    class CustomAdapter extends BaseAdapter {

        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        //get the posts from the given user in reverse chronological order
        List<Post> posts = db.postDao().findPostsForUserReverse(thisUserId);

        /**
         * getCount should return the size of the post
         */
        @Override
        public int getCount() {
            return posts.size();
        }

        /*
         * getItemI(i) doesn't need to return anything because it isn't used
         */
        @Override
        public Object getItem(int i) {
            return null;
        }

        /*
         * getItemId doesn't need to return anything special because it isn't used
         */
        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * create the view for each post(i)
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.view_post, null);

            User postCreator = db.userDao().getUserFromUserId(thisUserId);

            TextView firstName = view.findViewById(R.id.post_first_name_view);
            TextView lastName = view. findViewById(R.id.post_last_name_view);
            ImageView profilePic = view.findViewById(R.id.post_profile_image_view);

            TextView title = view.findViewById(R.id.post_title_view);
            ImageView image = view.findViewById(R.id.post_image_view);
            TextView content = view.findViewById(R.id.post_content_view);

            title.setText(posts.get(i).getTitle());
            content.setText(posts.get(i).getText());
            firstName.setText(postCreator.getFirstName());
            lastName.setText(postCreator.getLastName());
            if(postCreator.getProfilePicPath()!=null) {
                profilePic.setImageBitmap(PictureController.getScaledBitmap(
                        postCreator.getProfilePicPath(),
                        profilePic.getWidth(), profilePic.getHeight()));
            }
            if(posts.get(i).getImagePath()!= null) {
                image.setImageBitmap(PictureController.getScaledBitmap(posts.get(i).getImagePath(),
                        image.getWidth(), image.getHeight()));
            }
            return view;
        }
    }
    //endregion


    //region autogen drawer stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_view_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(ViewUserActivity.this, FeedActivity.class);
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
            Intent intent = new Intent(ViewUserActivity.this, FeedActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_new_post) {
            Intent intent = new Intent(ViewUserActivity.this, NewPostActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_find_user) {
            Intent intent = new Intent(ViewUserActivity.this, FindUserActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(ViewUserActivity.this, EditProfileActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            Intent intent = new Intent(ViewUserActivity.this, LoginActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_view_user);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion
}
