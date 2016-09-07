package im.bsk.storyalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.collect.Collections2;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.bsk.storyalbum.items.ImageItem;

public class EditStoryActivity extends AppCompatActivity {

    public static final int MODE_VIEW = 0;
    public static final int MODE_EDIT = 1;

    @IntDef({MODE_VIEW, MODE_EDIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode { }

    @Mode
    private int mMode = MODE_VIEW;
    private FastItemAdapter<ImageItem> mAdapter;

    // views
    @BindView(R.id.new_story_photo_number)
    TextView mTvPhotos;
    @BindView(R.id.new_story_photo_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.new_story_title)
    EditText mEtTitle;
    @BindView(R.id.new_story_memo)
    EditText mEtMemo;
    @BindView(R.id.new_story_timestamp)
    TextView mTvTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // view init
//        mTvPhotos.setText(String.format(getString(R.string.new_story_number_of_images), files.size()));
        mEtTitle.setEnabled(false);
        mEtMemo.setEnabled(false);
        mTvTimestamp.setVisibility(View.VISIBLE);

        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener((v, adapter, item, position) -> {
            ArrayList<String> paths = new ArrayList<>(Collections2.transform(
                    mAdapter.getAdapterItems(), i -> i.file.getPath()));

            Intent i = new Intent(this, GalleryActivity.class);
            i.putExtra(Constants.EXTRA_PHOTOS, paths);
            i.putExtra(Constants.EXTRA_PHOTO_SELECT, position);

            startActivity(i);
            return true;
        });

        // add images
//        for (int i = 0; i < files.size(); i++) {
//            File file = new File(files.get(i));
//
//            if (file.exists()) {
//                mAdapter.add(new ImageItem(file));
//            }
//        }

        //restore selections (this has to be done after the items were added
        mAdapter.withSavedInstanceState(savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        if (mAdapter != null)
            outState = mAdapter.saveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_story, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_edit:
                toggleMode(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleMode(MenuItem editMenu) {
        switch (mMode) {
            case MODE_VIEW:
                mMode = MODE_EDIT;
                editMenu.setTitle(R.string.action_save);
                mEtTitle.setEnabled(true);
                mEtMemo.setEnabled(true);
                break;

            case MODE_EDIT:
                mMode = MODE_VIEW;
                editMenu.setTitle(R.string.action_edit);
                mEtTitle.setEnabled(false);
                mEtMemo.setEnabled(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMode == MODE_EDIT) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_unsaved)
                    .setMessage(R.string.dialog_message_unsaved)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialog, which) ->
                            NavUtils.navigateUpFromSameTask(EditStoryActivity.this)).show();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}
