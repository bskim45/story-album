package im.bsk.storyalbum.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialize.util.UIUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.bsk.storyalbum.R;

public class StoryItem extends AbstractItem<StoryItem, StoryItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String mImagePath;
    public String mTitle;
    public Date mDate;
    public String mPeek;

    public StoryItem withImage(String imagePath) {
        this.mImagePath = imagePath;
        return this;
    }

    public StoryItem withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public StoryItem withDate(Date date) {
        this.mDate = date;
        return this;
    }

    public String getDateString() {
        return new SimpleDateFormat("yyyy년 MM월 dd일").format(mDate);
    }

    public String getDateTimeString() {
        return new SimpleDateFormat("yyyy년 MM월 dd일 h시 m분").format(mDate);
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_list_main_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.item_list_main;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context context = viewHolder.itemView.getContext();

        viewHolder.title.setText(mTitle);
        viewHolder.date.setText(getDateTimeString());
//        viewHolder.imageDescription.setText(mDescription);
//        viewHolder.imageView.setImageBitmap(null);

        //set the background for the item
        int color = UIUtils.getThemeColor(context, R.attr.colorPrimary);
        viewHolder.itemView.setBackground(FastAdapterUIUtils.getSelectablePressedBackground(context,
                FastAdapterUIUtils.adjustAlpha(color, 100), 50, true));

        //load glide
//        Glide.clear(viewHolder.imageView);
//        Glide.with(ctx).load(mImageUrl).animate(R.anim.alpha_on).into(viewHolder.imageView);
    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_main_image)
        protected ImageView thumbnail;
        @BindView(R.id.item_list_main_title)
        protected TextView title;
        @BindView(R.id.item_list_main_date)
        protected TextView date;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}