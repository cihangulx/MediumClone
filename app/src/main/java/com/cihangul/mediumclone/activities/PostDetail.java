package com.cihangul.mediumclone.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cihangul.mediumclone.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

public class PostDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getStringExtra("title") != null){
            setTitle(getIntent().getStringExtra("title"));
        }
        if (getIntent().getStringExtra("image") != null){
            Picasso.get().load(getIntent().getStringExtra("image")).into(((ImageView) findViewById(R.id.image)));

        }
        if (getIntent().getStringExtra("desc") != null){
            ((TextView) findViewById(R.id.description)).setText(Html.fromHtml(getIntent().getStringExtra("desc")));
        }

    }
}
