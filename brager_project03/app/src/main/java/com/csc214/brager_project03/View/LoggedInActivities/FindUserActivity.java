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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.csc214.brager_project03.Controller.PictureController;
import com.csc214.brager_project03.Model.Database;
import com.csc214.brager_project03.Model.Favorite;
import com.csc214.brager_project03.Model.User;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoginActivity;

import java.util.List;

public class FindUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    //region KEYS
    public static final String KEY_USER_ID = "user_id"; //Key for user_id -> used for entire app
    public static final String KEY_POST_CREATOR_ID = "post_creator_id";
    //endregion

    //region UI References
    private TextView mBarFirstNameView;
    private TextView mBarLastNameView;
    private ImageView mBarProfileImageView;
    //endregion

    //region Database
    private Database db;
    //USER ID
    private int userId;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_find_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mBarFirstNameView = findViewById(R.id.bar_first_name_view);
        mBarLastNameView = findViewById(R.id.bar_last_name_view);
        mBarProfileImageView = findViewById(R.id.bar_profile_image_view);

        Intent intent = getIntent();
        userId = intent.getIntExtra(KEY_USER_ID, -1);


        if (savedInstanceState != null) { //if a Bundle has been passed through the function
            userId = savedInstanceState.getInt(KEY_USER_ID);
        }

        db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        mBarFirstNameView.setText(db.userDao().getUserFromUserId(userId).getFirstName());
        mBarLastNameView.setText(db.userDao().getUserFromUserId(userId).getLastName());
        if (db.userDao().getUserFromUserId(userId).getProfilePicPath() != null) {
            mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(
                    db.userDao().getUserFromUserId(userId).getProfilePicPath(),
                    mBarProfileImageView.getWidth(),
                    mBarProfileImageView.getHeight()));
        }

        //create the listview and display it using the custom adapter
        ListView listView = findViewById(R.id.list);
        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * Handle item clicks within the listView
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int i, long id) {
        int postCreatorId = (int) id;

        Intent intent = new Intent(FindUserActivity.this, ViewUserActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        intent.putExtra(KEY_POST_CREATOR_ID, postCreatorId);
        Log.v(LoginActivity.TAG, "userId = " + userId + " postCreatorId = " + postCreatorId);
        startActivity(intent);

    }

    /**
     *
     * Save the data so that it can be displayed correctly when onCreate is called
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //put the values of the text into the outState
        outState.putInt(KEY_USER_ID, userId);
    }

    //region CustomAdapter Class

    /*
     * Populate the listView with the values from the database
     */
    class CustomAdapter extends BaseAdapter {

        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        //get all the users from the database
        List<User> users = db.userDao().getAll(false);

        /*
         * Get the number of users in users
         */
        @Override
        public int getCount() {
            return users.size();
        }

        /*
         * getItem returns the user at i
         */
        @Override
        public Object getItem(int i) {
            return users.get(i);
        }

        /*
         * getItemId returns the id of the user at i
         */
        @Override
        public long getItemId(int i) {
            return users.get(i).getUserId();
        }

        /*
         * Sets up the view for the listView
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.view_user, null);

            ImageView image = view.findViewById(R.id.profile_image_view);
            TextView firstName = view.findViewById(R.id.first_name_view);
            TextView lastName = view.findViewById(R.id.last_name_view);
            final ImageView favoriteButton = view.findViewById(R.id.favorite_button);

            firstName.setText(users.get(i).getFirstName());
            lastName.setText(users.get(i).getLastName());
            if (users.get(i).getProfilePicPath() != null) {
                image.setImageBitmap(PictureController.getScaledBitmap(users.get(i).getProfilePicPath(),
                        image.getWidth(), image.getHeight()));
            }


            if (db.favoriteDao().findFavoriteUserIdFromUserId(userId).contains(users.get(i).getUserId())) {
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
            } else {
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
            }
            final int j = i;

            //set the on click listener for the heart imageView within the listView.
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                            "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

                    /*
                     * if the button is clicked and the users are favorites, set the image to just the border and
                     * update the database so that it says that they aren't favorites
                     */
                    if (db.favoriteDao().findFavoriteUserIdFromUserId(userId).contains(users.get(j).getUserId())) {
                        favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                        Favorite favorite = db.favoriteDao().getFavoriteBetweenTwo(userId, users.get(j).getUserId());
                        db.favoriteDao().deleteFavorites(favorite);
                    }
                    /*
                     * if the button is clicked and the users aren't favorites, set the image to the whole heart and
                     * update the database so that it says that they are favorites
                     */
                    else {
                        favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                        Favorite favorite = new Favorite();
                        favorite.setFavoriteId(db.favoriteDao().lastId() + 1);

                        favorite.setFavoriteUserId(users.get(j).getUserId());
                        favorite.setUserId(userId);
                        db.favoriteDao().insertFavorites(favorite);
                    }
                }
            });

            return view;
        }
    }
    //endregion

    //region autogen drawer stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_find_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //up navigtion to feed activity.
            Intent intent = new Intent(FindUserActivity.this, FeedActivity.class);
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
            Intent intent = new Intent(FindUserActivity.this, FeedActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_new_post) {
            Intent intent = new Intent(FindUserActivity.this, NewPostActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_find_user) {
            //Do nothing cause you're already here!
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(FindUserActivity.this, EditProfileActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            Intent intent = new Intent(FindUserActivity.this, LoginActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_find_user);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion
}
