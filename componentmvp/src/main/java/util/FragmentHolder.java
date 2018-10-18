package util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xsfdev.componentmvp.R;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

/**
 * Created by jinshuhui on 2016/2/25.
 */
public final class FragmentHolder {
    private static class SingleHolder {
        private static final FragmentHolder INSTANCE = new FragmentHolder();
    }

    private FragmentHolder() {
    }

    public static final FragmentHolder getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 新增Fragment
     *
     * @param activity
     * @param resourceId
     * @param fragment
     */
    public void addFragment(FragmentActivity activity, int resourceId, Fragment fragment) throws Exception {
        if (fragment == null) {
            return;
        }
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        addFragmentAnimation(transaction);
        transaction.add(resourceId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
//        NavUtils.getInstance().bringToTopDelay(activity, android.R.integer.config_mediumAnimTime);

//        LogService.getInstance().d("=====current addFragment ==" + fragment.getClass().getSimpleName());
    }

    public void replaceFragment(FragmentActivity activity, int resourceId, Fragment fragment, boolean isAddToBackstack) throws Exception {
        if (fragment == null) {
            return;
        }
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(resourceId, fragment, fragment.getClass().getSimpleName());
        if (isAddToBackstack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
//        LogService.getInstance().d("=====current replaceFragment ==" + fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


    public void removeFragment(FragmentActivity activity, Fragment targetFragment) {
        if (targetFragment == null) {
            return;
        }
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        addFragmentAnimation(transaction);
        transaction.remove(targetFragment);
        transaction.commitAllowingStateLoss();
    }

    private void addFragmentAnimation(FragmentTransaction transaction) {
        if (null != transaction) {
            transaction.setCustomAnimations(R.anim.sdk_slide_in_from_right, R.anim.sdk_slide_out_to_right,
                    R.anim.sdk_slide_in_from_right, R.anim.sdk_slide_out_to_right);
        }
    }

    public boolean popBackStackImmediate(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        FragmentActivity fragmentActivity = fragment.getActivity();
        if (fragmentActivity == null) {
            return false;
        }
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        if (fragmentManager != null) {
            try {
                return fragmentManager.popBackStackImmediate(fragment.getClass().getSimpleName(), POP_BACK_STACK_INCLUSIVE);
            } catch (Exception e) {
                removeFragment(fragmentActivity, fragment);
//                LogService.getInstance().d(e.toString());
//                LogService.getInstance().log2sd(e.toString());
            }
        }
        return false;
    }

    /**
     * 展示DialogFragment
     *
     * @param fragmentManager
     * @param fragment
     */
    public void showDialogFragment(FragmentManager fragmentManager, Fragment fragment) {
        if (null == fragmentManager || null == fragment) {
            return;
        }
        try {
            fragmentManager.beginTransaction().add(fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName()).commitAllowingStateLoss();
        } catch (Exception e) {
//            LogService.getInstance().log2sd("FragmentHolder.showDialogFragment exception:" + e);
        }
    }


    /**
     * 展示DialogFragment
     * 无需使用回退栈
     *
     * @param fragmentManager
     * @param fragment
     */
    public void showDialogFragment(FragmentManager fragmentManager, Fragment fragment, String tag) {
        //防止重叠
        if (null == fragmentManager || null == fragment || fragmentManager.findFragmentByTag(tag) != null) {
            return;
        }
        try {
            fragmentManager.beginTransaction().add(fragment, tag).commitAllowingStateLoss();
        } catch (Exception e) {
//            LogService.getInstance().log2sd("FragmentHolder.showDialogFragment exception:" + e);
        }
    }

}
