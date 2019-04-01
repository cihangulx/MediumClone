package com.cihangul.mediumclone;

import android.content.Intent;
import android.os.Bundle;

import com.cihangul.mediumclone.activities.AddPost;
import com.cihangul.mediumclone.activities.Login;
import com.cihangul.mediumclone.activities.PostDetail;
import com.cihangul.mediumclone.adapters.PostAdapter;
import com.cihangul.mediumclone.interfaces.ItemClickListener;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemClickListener<ParseObject>, LogOutCallback, SearchView.OnQueryTextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.email);

        userNameTextView.setText(ParseUser.getCurrentUser().getUsername());
        emailTextView.setText(ParseUser.getCurrentUser().getEmail());

        getPosts();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPosts();
    }

    private void getPosts() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Post");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));

                PostAdapter postAdapter = new PostAdapter(MainActivity.this, objects, MainActivity.this);
                recyclerView.setAdapter(postAdapter);

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout)
            ParseUser.logOutInBackground(this);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                startActivity(new Intent(this, AddPost.class));
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(ParseObject item) {

        startActivity(new Intent(this, PostDetail.class)
                .putExtra("title",item.getString("title"))
                .putExtra("image",item.getParseFile("image").getUrl())
                .putExtra("desc",item.getString("description")));

    }

    @Override
    public void done(ParseException e) {
        if (e == null && ParseUser.getCurrentUser() == null) {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((PostAdapter) ((RecyclerView) findViewById(R.id.recycler_view)).getAdapter()).getFilter().filter(newText);
        return false;
    }
}
