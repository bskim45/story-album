package im.bsk.storyalbum.items;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.bsk.storyalbum.R;


public class HeaderItem extends AbstractItem<HeaderItem, HeaderItem.ViewHolder> implements IExpandable<HeaderItem, IItem> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String header;

    private List<IItem> mSubItems;
    private boolean mExpanded = false;
    private boolean mIsDraggable = true;

    public HeaderItem withHeader(String header) {
        this.header = header;
        return this;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public HeaderItem withIsExpanded(boolean expaned) {
        mExpanded = expaned;
        return this;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }

    @Override
    public List<IItem> getSubItems() {
        return mSubItems;
    }

    public HeaderItem withSubItems(List<IItem> subItems) {
        this.mSubItems = subItems;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_list_header_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.item_list_header;
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

        UIUtils.setBackground(viewHolder.itemView,
                FastAdapterUIUtils.getSelectableBackground(context, Color.RED, true));
        viewHolder.text.setText(header);
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
        @BindView(R.id.item_list_header_text)
        TextView text;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}