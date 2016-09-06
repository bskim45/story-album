package im.bsk.storyalbum;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;


public class UIUtil {
    public static final String TAG = UIUtil.class.getSimpleName();

    public static void toggleSystemUI(Activity activity) {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }


        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public static void showSystemUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(0);
    }

    public static int dpToPixels(Context c, int dp) {
        return (int) (c.getResources().getDisplayMetrics().density * dp);
    }

    public static int pixelsToDp(Context c, int pixels) {
        return (int) ((float) pixels / c.getResources().getDisplayMetrics().density);
    }


    public static void paddingForStatusBar(View view, boolean isFixedSize) {
        if (isCanHaveTransparentDecor()) {
            int height = getStatusBarHeight(view.getContext());

            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + height,
                    view.getPaddingRight(), view.getPaddingBottom());

            if (isFixedSize) {
                view.getLayoutParams().height += height;
            }
        }
    }

    public static void marginForStatusBar(View view) {
        if (isCanHaveTransparentDecor()) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).topMargin
                    += getStatusBarHeight(view.getContext());
        }
    }

    public static void paddingForNavBar(View view) {
        if (isCanHaveTransparentDecor()) {
            int height = getNavBarHeight(view.getContext());
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                    view.getPaddingRight(), view.getPaddingBottom() + height);
        }
    }

    public static void marginForNavBar(View view) {
        if (isCanHaveTransparentDecor()) {
            ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).bottomMargin
                    += getNavBarHeight(view.getContext());
        }
    }

    private static boolean isCanHaveTransparentDecor() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        return getAndroidDimenSize(context, "status_bar_height");
    }

    // A method to find height of the navigation bar
    public static int getNavBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasNavBar = !hasMenuKey && !hasBackKey;

        if (hasNavBar) {
            boolean isPortrait = context.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT;

            boolean isTablet = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;

            String key = isPortrait ? "navigation_bar_height"
                    : (isTablet ? "navigation_bar_height_landscape" : null);

            return key == null ? 0 : getAndroidDimenSize(context, key);
        } else {
            return 0;
        }
    }

    private static int getAndroidDimenSize(Context context, String key) {
        int resourceId = context.getResources().getIdentifier(key, "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }
}
