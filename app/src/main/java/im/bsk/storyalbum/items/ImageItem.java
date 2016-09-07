package im.bsk.storyalbum.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.bsk.storyalbum.R;


public class ImageItem extends AbstractItem<ImageItem, ImageItem.ViewHolder> {
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
                .onlyScaleDown()
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
