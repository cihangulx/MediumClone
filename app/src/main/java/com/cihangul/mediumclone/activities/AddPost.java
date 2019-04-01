package com.cihangul.mediumclone.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.github.mthli.knife.KnifeText;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cihangul.mediumclone.R;
import com.cihangul.mediumclone.tools.FileUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class AddPost extends AppCompatActivity implements View.OnClickListener {
    private static final int STORAGE_PERMISSION = 456;
    private static final int SELECT_IMAGE = 560;
    private static final int IMAGE_HEIGHT = 300;
    private KnifeText knifeText;
    private Uri imgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        knifeText = findViewById(R.id.knife);
        ((TextInputEditText) findViewById(R.id.title)).addTextChangedListener(textWatcher);
        ((TextInputEditText) findViewById(R.id.category)).addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bold:
                knifeText.bold(!knifeText.contains(KnifeText.FORMAT_BOLD));
                break;
            case R.id.italic:
                knifeText.italic(!knifeText.contains(KnifeText.FORMAT_ITALIC));
                break;
            case R.id.underline:
                knifeText.underline(!knifeText.contains(KnifeText.FORMAT_UNDERLINED));
                break;
            case R.id.strikethrough:
                knifeText.strikethrough(!knifeText.contains(KnifeText.FORMAT_STRIKETHROUGH));
                break;
            case R.id.bullet:
                knifeText.bullet(!knifeText.contains(KnifeText.FORMAT_BULLET));
                break;
            case R.id.quote:
                knifeText.quote(!knifeText.contains(KnifeText.FORMAT_QUOTE));
                break;
            case R.id.link:
                showLinkDialog();
                break;
            case R.id.clear:
                knifeText.clearFormats();
                break;
            case R.id.select_image:
                selectImage();
                break;

        }
    }

    private void selectImage() {
        if (isStoragePermissionGranted()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), SELECT_IMAGE);
        }
    }

    private void showLinkDialog() {
        final int start = knifeText.getSelectionStart();
        final int end = knifeText.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.link_dialog, null, false);
        final EditText editText = view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(getString(R.string.add_link));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }
                knifeText.link(link, start, end);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save:
                save();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.imgUri = data.getData();
            ((ImageView) findViewById(R.id.select_image)).setImageURI(data.getData());
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                return false;
            }
        } else {
            return true;
        }
    }

    private void save() {
        // TODO: 31.03.2019 Add Progress Dialog
        if (TextUtils.isEmpty(((TextInputEditText) findViewById(R.id.title)).getText())) {
            ((TextInputLayout) findViewById(R.id.title_il)).setError(getString(R.string.title_error_text));
            return;
        }
        if (TextUtils.isEmpty(((TextInputEditText) findViewById(R.id.category)).getText())) {
            ((TextInputLayout) findViewById(R.id.category_il)).setError(getString(R.string.category_error_text));
            return;
        }
        if (TextUtils.isEmpty(knifeText.getText())) {
            knifeText.setError(getString(R.string.description_error_text));
            return;
        }

        ParseObject parseObject = new ParseObject("Post");

        if (imgUri != null) {
            File file = new File(new FileUtils(this).getPath(imgUri));
            File resizedFile = new FileUtils(this).getResizedFile(file,IMAGE_HEIGHT);
            ParseFile parseFile = new ParseFile(resizedFile);
            parseObject.put("image", parseFile);
        }

        parseObject.put("title", ((TextInputEditText) findViewById(R.id.title)).getText().toString().trim());
        parseObject.put("category", ((TextInputEditText) findViewById(R.id.category)).getText().toString().trim());
        parseObject.put("description", knifeText.toHtml());
        parseObject.put("userName", ParseUser.getCurrentUser().getUsername());

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    onBackPressed();
                }
            }
        });


    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            findViewById(R.id.tools).setVisibility(View.GONE);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            TextInputLayout title= findViewById(R.id.title_il);
            TextInputLayout category= findViewById(R.id.category_il);

            if (title.getError() != null)
                title.setError(null);
            if (category.getError() != null)
                category.setError(null);
            if (knifeText.getError() != null)
                knifeText.setError(null);

        }

        @Override
        public void afterTextChanged(Editable editable) {
            findViewById(R.id.tools).setVisibility(View.VISIBLE);
        }
    };
}
