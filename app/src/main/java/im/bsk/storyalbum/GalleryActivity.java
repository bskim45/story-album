package im.bsk.storyalbum;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.GestureController;
import com.mikepenz.materialize.MaterializeBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@RuntimePermissions
public class GalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.gallery_viewpager)
    ViewPager mViewpager;
    @BindView(R.id.gallery_title)
    TextView mTvTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private List<File> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        ArrayList<String> files = getIntent().getStringArrayListExtra(Constants.EXTRA_PHOTOS);
        int select = getIntent().getIntExtra(Constants.EXTRA_PHOTO_SELECT, 0);

        if (files == null) {
            Toast.makeText(this, "사진을 가져오는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // add images
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));

            if (file.exists()) {
                mImages.add(file);
            }
        }

        // set title
        setTitle(String.format("%d/%d", select + 1, mImages.size()));

        // move down toolbar below statusbar
        UIUtil.marginForStatusBar(mToolbar);

        // move up title
        UIUtil.marginForNavBar(mTvTitle);

        // setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImagePagerAdapter adapter = new ImagePagerAdapter(mViewpager, mImages);
        adapter.setOnGesturesListener(new GestureController.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                // toggle system ui
                UIUtil.toggleSystemUI(GalleryActivity.this);
                return true;
            }
        });
        mViewpager.setAdapter(adapter);
        mViewpager.addOnPageChangeListener(this);
        mViewpager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.view_pager_margin));
        mViewpager.setCurrentItem(select);

        // additional ui settings
        new MaterializeBuilder(this)
                .withActivity(this)
                .withTransparentNavigationBar(true)
                .withFullscreen(true)
                .build();

        // listen system ui toggle
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // show
                if(!getSupportActionBar().isShowing()) {
                    mToolbar.animate().translationYBy(mToolbar.getHeight()).alpha(1.0f)
                            .withEndAction(() -> getSupportActionBar().show());
                }

                // show title
                mTvTitle.animate().alpha(1.0f).withEndAction(() ->
                        mTvTitle.setVisibility(View.VISIBLE));
            } else {
                // hide
                if(getSupportActionBar().isShowing()) {
                    mToolbar.animate().translationYBy(-mToolbar.getHeight()).alpha(0)
                            .withEndAction(() -> getSupportActionBar().hide());
                }

                // hide title
                mTvTitle.animate().alpha(0).withEndAction(() ->
                        mTvTitle.setVisibility(View.INVISIBLE));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save:
                GalleryActivityPermissionsDispatcher.saveImageWithCheck(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void saveImage() {
        File src = mImages.get(mViewpager.getCurrentItem());
        File ext = FileUtil.getExternalFile(this, src.getName());

        Single.create(singleSubscriber -> {
            try {
                FileUtil.copy(src, ext);
                singleSubscriber.onSuccess(ext);
            } catch (IOException e) {
                singleSubscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(f -> Toast.makeText(this, R.string.success_save_image, Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(this, R.string.error_save_image, Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTvTitle.setText(mImages.get(position).getName());

        setTitle(String.format("%d/%d", position + 1, mImages.size()));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // handle runtime permissions
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> request.proceed())
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForStorage() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Toast.makeText(this, R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        GalleryActivityPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
