package im.bsk.storyalbum.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import im.bsk.storyalbum.BuildConfig;

public class ImagePagerAdapter extends RecyclePagerAdapter<ImagePagerAdapter.ViewHolder> {

    private final ViewPager viewPager;
    private final List<File> images;
    private GestureController.OnGestureListener gestureListener;

    public ImagePagerAdapter(ViewPager pager, List<File> images) {
        this.viewPager = pager;
        this.images = images;
    }

    public void setOnGesturesListener(GestureController.OnGestureListener l) {
        this.gestureListener = l;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        ViewHolder holder = new ViewHolder(container);
        holder.image.getController().enableScrollInViewPager(viewPager);
        if (gestureListener != null)
            holder.image.getController().setOnGesturesListener(gestureListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.getController().getSettings()
                .setMaxZoom(3f)
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(false)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

        Picasso picasso = Picasso.with(holder.itemView.getContext());
        if(BuildConfig.DEBUG) {
            picasso.setIndicatorsEnabled(true);
            picasso.setLoggingEnabled(true);
        }

        picasso.load(images.get(position))
                .noFade()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .fit()
                .centerInside()
                .into(holder.image);
    }

    public static GestureImageView getImage(RecyclePagerAdapter.ViewHolder holder) {
        return ((ViewHolder) holder).image;
    }


    static class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        final GestureImageView image;

        ViewHolder(ViewGroup container) {
            super(new GestureImageView(container.getContext()));
            image = (GestureImageView) itemView;
        }
    }

}
