package im.bsk.storyalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Collections2;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewStoryActivity extends AppCompatActivity {

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

    private FastItemAdapter<ImageItem> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> files = getIntent().getStringArrayListExtra(Constants.EXTRA_PHOTOS);

        if (files == null) {
            Toast.makeText(this, "사진을 가져오는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(this);
            return;
        }

        // view init
        mTvPhotos.setText(String.format(getString(R.string.new_story_number_of_images), files.size()));
        mTvTimestamp.setVisibility(View.GONE);

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
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));

            if (file.exists()) {
                mAdapter.add(new ImageItem(file));
            }
        }

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
        getMenuInflater().inflate(R.menu.menu_new_story, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_confirm:
                // TODO: confirm
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getAdapterItemCount() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_unsaved)
                    .setMessage(R.string.dialog_message_unsaved)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        cleanTempFiles();
                        NavUtils.navigateUpFromSameTask(NewStoryActivity.this);
                    }).show();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    private void cleanTempFiles() {
        if (mAdapter == null) {
            return;
        }

        for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
            mAdapter.getAdapterItem(i).file.delete();
        }
    }

    static class ImageItem extends AbstractItem<ImageItem, ImageItem.ViewHolder> {
        private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

        public final File file;

        public ImageItem(File file) {
            this.file = file;
        }

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.fastadapter_new_story_image_id;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.item_new_story_image;
        }

        @Override
        public void bindView(final ViewHolder viewHolder, List payloads) {
            super.bindView(viewHolder, payloads);

            Picasso.with(viewHolder.itemView.getContext())
                    .load(file)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
//                    .centerCrop()
                    .into(viewHolder.image);

        }

        protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
            public ViewHolder create(View v) {
                return new ViewHolder(v);
            }
        }

        @Override
        public ViewHolderFactory<? extends ViewHolder> getFactory() {
            return FACTORY;
        }

        protected static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_new_image)
            ImageView image;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
