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
import com.csc214.brager_project03.Model.Post;
import com.csc214.brager_project03.Model.User;
import com.csc214.brager_project03.R;
import com.csc214.brager_project03.View.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    //region KEYS
    public static final String KEY_USER_ID = "user_id"; //Key for user_id -> used for entire app
    public static final String KEY_POST_CREATOR_ID = "post_creator_id"; //Key for user_id -> used for entire app
    //endregion

    //region UI References
    private TextView mBarFirstNameView;
    private TextView mBarLastNameView;
    private ImageView mBarProfileImageView;
    //endregion

    //region Database
    private Database db;
    // USER ID
    private int userId;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_feed);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mBarFirstNameView = findViewById(R.id.bar_first_name_view);
        mBarLastNameView = findViewById(R.id.bar_last_name_view);
        mBarProfileImageView = findViewById(R.id.bar_profile_image_view);

        Intent intent = getIntent();
        userId = intent.getIntExtra(KEY_USER_ID, -1);

        db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        mBarFirstNameView.setText(db.userDao().getUserFromUserId(userId).getFirstName());
        mBarLastNameView.setText(db.userDao().getUserFromUserId(userId).getLastName());
        if (db.userDao().getUserFromUserId(userId).getProfilePicPath() != null) {
            mBarProfileImageView.setImageBitmap(PictureController.getScaledBitmap(
                    db.userDao().getUserFromUserId(userId).getProfilePicPath(),
                    mBarProfileImageView.getWidth(), mBarProfileImageView.getHeight()));
        }

        //create the list view using my custom adapter
        ListView listView = findViewById(R.id.list);
        final CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(this);
    }

    /**
     * Handle list view item clicks
     */

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int i, long id) {
        int postCreatorId = db.postDao().getUserIdFromPostId((int) id);

        Intent intent = new Intent(FeedActivity.this, ViewUserActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        intent.putExtra(KEY_POST_CREATOR_ID, postCreatorId);
        startActivity(intent);

    }


    //region CustomAdapter Class
    /*
     * Populate the listView with the values from the database
     */
    class CustomAdapter extends BaseAdapter {

        Database db = Room.databaseBuilder(getApplicationContext(), Database.class,
                "database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        List<Post> posts = getPosts();


        /**
         * Get the list of posts by only favorited users
         */
        public List<Post> getPosts() {
            //Get all posts in reverse chronological order
            List<Post> posts = db.postDao().getAllReverseChronologicalOrder();
            int size = posts.size();
            List<Post> output = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                //if the users are favorites then add it to the output list
                if (db.favoriteDao().isThereFavoriteBetweenTwo(userId, posts.get(i).getUserId())) {
                    output.add(posts.get(i));
                }
            }
            return output;
        }

        /**
         * getCount should return the size of the post
         */
        @Override
        public int getCount() {
            return posts.size();
        }

        /**
         * getItem(i) should return the post at i
         */
        @Override
        public Object getItem(int i) {
            return posts.get(i);
        }

        /*
         * getItemId(i) should return the id of the post at i
         */
        @Override
        public long getItemId(int i) {
            return posts.get(i).getPostId();
        }

        /**
         * create the view for each post(i)
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.view_post, null);

            TextView firstName = view.findViewById(R.id.post_first_name_view);
            TextView lastName = view.findViewById(R.id.post_last_name_view);
            ImageView profilePic = view.findViewById(R.id.post_profile_image_view);

            TextView title = view.findViewById(R.id.post_title_view);
            ImageView image = view.findViewById(R.id.post_image_view);
            TextView content = view.findViewById(R.id.post_content_view);

            User postCreator = db.userDao().getUserFromUserId(posts.get(i).getUserId());

            title.setText(posts.get(i).getTitle());
            content.setText(posts.get(i).getText());
            firstName.setText(postCreator.getFirstName());
            lastName.setText(postCreator.getLastName());
            if (postCreator.getProfilePicPath() != null) {
                profilePic.setImageBitmap(PictureController.getScaledBitmap(postCreator.getProfilePicPath(),
                        profilePic.getWidth(), profilePic.getHeight()));
            }
            if (posts.get(i).getImagePath() != null) {
                image.setImageBitmap(PictureController.getScaledBitmap(posts.get(i).getImagePath(),
                        image.getWidth(), image.getHeight()));
            }

            return view;
        }

    }
    //endregion


    //region auto generated drawer stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_feed);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //No else. Just stay in this activity when the back button is pressed
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view_feed) {
            //Don't do anything cause you're already there!
        } else if (id == R.id.nav_new_post) {
            Intent intent = new Intent(FeedActivity.this, NewPostActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_find_user) {
            Intent intent = new Intent(FeedActivity.this, FindUserActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(FeedActivity.this, EditProfileActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
            intent.putExtra(KEY_USER_ID, userId);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_feed);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion
}

