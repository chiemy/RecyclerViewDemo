package com.chiemy.recyclerviewdemo.itemanimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Created: chiemy
 * Date: 16/6/28
 * Description:
 */
public class SlideAlphaItemAnimator extends SimpleItemAnimator{
    private ArrayList<RecyclerView.ViewHolder> mPendingAddHolders =
            new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemoveHolders =
            new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mAddAnimtions = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();

    private ArrayList<MoveInfo> mPendingMoveHolders = new ArrayList<>();
    private ArrayList<MoveInfo> mMoveAnimtions = new ArrayList<>();

    private static final long DEFUALT_DURATION = 300;
    private static final float MAX_ALPHA = 1.f;
    private static final float MIN_ALPHA = 0.f;

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        mPendingRemoveHolders.add(holder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setAlpha(holder.itemView, MIN_ALPHA);
        mPendingAddHolders.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        fromY += view.getTranslationY();
        int delta = toY - fromY;
        view.setTranslationY(-delta);
        MoveInfo info = new MoveInfo(holder, fromX, fromY, toX, toY);
        mPendingMoveHolders.add(info);
        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        boolean isRemove = !mPendingRemoveHolders.isEmpty();
        boolean isAdd = !mPendingAddHolders.isEmpty();
        boolean isMove = !mPendingMoveHolders.isEmpty();

        if(!isRemove && !isAdd && !isMove) return;

        // first remove
        if(isRemove) {
            for(RecyclerView.ViewHolder holder : mPendingRemoveHolders) {
                animateRemoveImpl(holder);
            }
            mPendingRemoveHolders.clear();
        }

        // last add
        if(isAdd) {
            ArrayList<RecyclerView.ViewHolder> holders = new ArrayList<>();
            holders.addAll(mPendingAddHolders);
            mPendingAddHolders.clear();
            for(RecyclerView.ViewHolder holder : holders) {
                animateAddImpl(holder);
            }
            holders.clear();
        }

        if(isMove) {
            ArrayList<MoveInfo> infos = new ArrayList<>();
            infos.addAll(mPendingMoveHolders);
            mPendingMoveHolders.clear();
            for(MoveInfo info : infos) {
                animateMoveImpl(info);
            }
            infos.clear();
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return !(mPendingAddHolders.isEmpty()
                && mPendingRemoveHolders.isEmpty()
                && mAddAnimtions.isEmpty()
                && mRemoveAnimations.isEmpty());
    }

    // 执行添加动画
    private void animateAddImpl(final RecyclerView.ViewHolder holder) {
        mAddAnimtions.add(holder);
        final View item = holder.itemView;
        playAnimatorSet(item, DEFUALT_DURATION, MIN_ALPHA, MAX_ALPHA, item.getHeight(), item.getTranslationY(),
                new DecelerateInterpolator(), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                item.setAlpha(MAX_ALPHA);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(holder);
                mAddAnimtions.remove(holder);
                if (!isRunning()) {
                    dispatchAnimationsFinished();
                }
            }
        });
    }

    // 执行移出动画
    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        mRemoveAnimations.add(holder);
        final View item = holder.itemView;
        playAnimatorSet(item, DEFUALT_DURATION, MAX_ALPHA, MIN_ALPHA, item.getTranslationY(), item.getHeight(),
                new AccelerateInterpolator(), new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRemoveAnimations.remove(holder);
                        item.setAlpha(MAX_ALPHA);
                        dispatchRemoveFinished(holder);
                        if (!isRunning()) {
                            dispatchAnimationsFinished();
                        }
                    }
                });
    }

    private AnimatorSet playAnimatorSet(View view, long duration, float fromAlpha, float toAlpha, float fromY, float toY,
                                        TimeInterpolator interpolator, Animator.AnimatorListener listener) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        Animator alphaAnimator = getAlphaAnimator(view, duration, fromAlpha, toAlpha);
        Animator translateAnimator = getTranslateYAnimator(view, duration, fromY, toY);
        set.addListener(listener);
        set.setInterpolator(interpolator);
        set.playTogether(alphaAnimator, translateAnimator);
        set.start();
        return set;
    }

    private Animator getAlphaAnimator(final View item, long duration, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(item, "alpha", from, to);
        animator.setDuration(duration);
        return animator;
    }

    private Animator getTranslateYAnimator(View item, long duration, float fromY, float toY) {
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(item,
                "translationY", fromY, toY);
        translateAnim.setDuration(duration);
        return translateAnim;
    }

    // 执行移动动画
    private void animateMoveImpl(final MoveInfo info) {
        mMoveAnimtions.remove(info);
        final View view = info.holder.itemView;
        Animator animator = getTranslateYAnimator(view, DEFUALT_DURATION, view.getTranslationY(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchMoveStarting(info.holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchMoveFinished(info.holder);
                mMoveAnimtions.remove(info.holder);
                if(!isRunning()) {
                    dispatchAnimationsFinished();
                }
            }
        });
        animator.start();
    }

    private ItemAnimatorListener animatorListener;
    public void setAnimatorListener(ItemAnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
    }

    public interface ItemAnimatorListener {
        void onAnimationFinished(int type);
    }

    class MoveInfo {
        private RecyclerView.ViewHolder holder;
        private int fromX;
        private int fromY;
        private int toX;
        private int toY;

        public MoveInfo(RecyclerView.ViewHolder holder,
                        int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }
}
