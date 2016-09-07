package im.bsk.storyalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.HeaderHelper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.bsk.storyalbum.items.HeaderItem;
import im.bsk.storyalbum.items.StoryItem;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_searchview)
    SearchView mSearchView;
    @BindView(R.id.main_recyclerview)
    RecyclerView mRecyclerView;
    private ItemAdapter mItemAdapter;
    private FastAdapter<IItem> mFastAdapter;
    private ActionModeHelper mActionModeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFastAdapter = new FastAdapter<>();
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(true);
        mItemAdapter = new ItemAdapter<>();

        mActionModeHelper = new ActionModeHelper(mFastAdapter, R.menu.menu_cab, mActionModeCallback);

        mFastAdapter.withOnPreClickListener((v, adapter, item, position) -> {
            Boolean handled = mActionModeHelper.onClick(item);
            return handled != null ? handled : false;
        });

        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            ActionMode actionMode = mActionModeHelper.getActionMode();

            if (actionMode != null) {
                actionMode.setTitle(String.format("%d개 선택", mFastAdapter.getSelections().size()));
//                    Toast.makeText(v.getContext(), "SelectedCount: " + mFastAdapter.getSelections().size() + " ItemsCount: " + mFastAdapter.getSelectedItems().size(), Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        mFastAdapter.withOnPreLongClickListener((v, adapter, item, position) -> {
            ActionMode actionMode = mActionModeHelper.onLongClick(MainActivity.this, position);

            if (actionMode != null) {
                // color cab
//                    findViewById(R.id.action_mode_bar)
//                            .setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(MainActivity.this,
//                                    R.attr.colorPrimary, R.color.colorPrimary));
            }

            // consume only if we have an actionMode
            return actionMode != null;
        });

        mRecyclerView.setAdapter(mItemAdapter.wrap(mFastAdapter));
//        mRecyclerView.setItemAnimator(new SlideDownAlphaAnimator());
//        mRecyclerView.getItemAnimator().setAddDuration(500);
//        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        final HeaderHelper<IItem, IItem> headerHelper = new HeaderHelper<>(mItemAdapter, (currentItem, nextItem, currentPosition) -> {
            if (currentItem == null) {
                return new HeaderItem().withHeader(((StoryItem) nextItem).getDateString()).withSelectable(false);
            } else if (nextItem != null) {
                if (!((StoryItem) currentItem).getDateString().equals(((StoryItem) nextItem).getDateString())) {
                    return new HeaderItem().withHeader(((StoryItem) nextItem).getDateString()).withSelectable(false);
                }
            }
            return null;
        });

        headerHelper.setComparator((f1, f2) -> {
            if (f1 instanceof StoryItem && f2 instanceof StoryItem) {
                return ((StoryItem) f1).mDate.compareTo(((StoryItem) f2).mDate);
            } else {
                return f1.toString().compareTo(f2.toString());
            }
        });

        //add some dummy data
        headerHelper.apply(createDummy());

        //restore selections (this has to be done after the items were added
        mFastAdapter.withSavedInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // TODO: launch camera
                startActivity(new Intent(this, CameraActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    List<StoryItem> createDummy() {
        ArrayList<StoryItem> list = new ArrayList<>();
        Calendar today = Calendar.getInstance();

        for (int i = 0; i < 20; i++) {
            if (i % 4 == 0)
                today.add(Calendar.DAY_OF_MONTH, -1);

            StoryItem item = new StoryItem().withTitle("Test " + i).withDate(new Date(today.getTimeInMillis()));
            list.add(item);
        }

        return list;
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_all:
                    mFastAdapter.select(true);
                    mode.setTitle(String.format("%d개 선택", mItemAdapter.getAdapterItemCount()));
                    return true;
                case R.id.action_delete:
//                    mUndoHelper.remove(findViewById(android.R.id.content), "Item removed", "Undo",
//                    Snackbar.LENGTH_LONG, mFastAdapter.getSelections());
                    mode.finish();
                    return true;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("스토리 선택");
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    };
}
