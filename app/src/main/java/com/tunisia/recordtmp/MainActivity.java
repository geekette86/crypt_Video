package com.tunisia.recordtmp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static int VIDEO_CAPTURED = 1;
    Button capture;
    Button play;
    Button save;
    EditText text;
    Uri videourl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capture = (Button) this
                .findViewById(R.id.CaptureVideoButton);
        play = (Button) this.findViewById(R.id.PlayVideoButton);
        save = (Button) this.findViewById(R.id.SaveVideoButton);

       text = (EditText) this.findViewById(R.id.TitleEditText);

        capture.setOnClickListener(this);
        play.setOnClickListener(this);
        save.setOnClickListener(this);

        play.setEnabled(false);
        save.setEnabled(false);
        text.setEnabled(false);
    }
    public void onClick(View v) {
        if (v == capture) {
            Intent captureVideoIntent = new Intent(
                    android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(captureVideoIntent, VIDEO_CAPTURED);
        } else if (v == play) {
            Intent playVideoIntent = new Intent(Intent.ACTION_VIEW,
                    videourl);

            Log.e("video", String.valueOf(Uri.parse(videourl.getPath().toString())));
            startActivity(playVideoIntent);
        } else if (v == save) {

            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Video.Media.LATITUDE, text.getText()
                    .toString());




            if (getContentResolver().update(videourl, values, null, null) == 1) {

                Toast t = Toast.makeText(this, "Updated "
                                + text.getText().toString(),
                        Toast.LENGTH_SHORT);
                t.show();
            } else {

                Toast t = Toast.makeText(this, "Error", Toast.LENGTH_SHORT);
                t.show();
            }
            String key = "abcdefgh12345678";
            String path=getRealPathFromURI(this, videourl);
            Log.e("path",path+".cpt");
            File inputFile = new File(path);
            File out = new File(path+".cpt");
            Log.e("input",inputFile.toString());
           // File encryptedFile = new File("document.encrypted");
            //File decryptedFile = new File("document.decrypted");

            crypto.encrypt(key, inputFile, out );
            Log.e("crypt", "ccc");
            crypto.decrypt(key, out, inputFile);
            Log.e("decrypt", "ddd");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            videourl = data.getData();
            play.setEnabled(true);
            save.setEnabled(true);
            text.setEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
