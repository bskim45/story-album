package im.bsk.storyalbum;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class NewStoryActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTOS = "im.bsk.storyalbum.PHOTOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> files = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);

        if(files == null) {

        } else {
            for (String file : files) {
                Log.d(getClass().getSimpleName(), file);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_story, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_confirm:
                // TODO: confirm
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
