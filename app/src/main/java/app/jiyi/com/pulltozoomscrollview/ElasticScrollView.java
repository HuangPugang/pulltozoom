package app.jiyi.com.pulltozoomscrollview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Date 2015/3/25 16:50
 * PackageName demo.hpg.org.pauldemo.tanxingscrollview
 */

/**
 * ElasticScrollView有弹性的ScrollView
 */
public class ElasticScrollView extends ScrollView {
    //第一个子view
    private View mFirstChild;
    //按下Y轴坐标
    private float mDownY;

    private Rect normal = new Rect();

    private boolean isAnimationFinish = true;

    public ElasticScrollView(Context context) {
        super(context);
    }

    public ElasticScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            mFirstChild = getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mFirstChild == null) {//如果没有子控件，使用默认操作
            return super.onTouchEvent(ev);
        } else {
            commOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    public void commOnTouchEvent(MotionEvent ev) {
        if (isAnimationFinish) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = ev.getY();
                    super.onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_UP:
                    mDownY = 0;
                    if (isNeedAnimation()) {
                        animation();
                    }
                    super.onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float preY = mDownY == 0 ? ev.getY() : mDownY;
                    float currentY = ev.getY();
                    int deltaY = (int) (preY - currentY);
                    // 滚动
                    mDownY = currentY;
                    // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                    if (isNeedMove()) {
                        if (normal.isEmpty()) {
                            Log.e("HPG",mFirstChild.getLeft()+"  "+mFirstChild.getTop()+"  "+mFirstChild.getRight()+"  "+mFirstChild.getRight());
                            // 保存正常的布局位置
                            normal.set(mFirstChild.getLeft(), mFirstChild.getTop(), mFirstChild.getRight(), mFirstChild.getBottom());
                        }
                        // 移动布局(关键)
                        mFirstChild.layout(mFirstChild.getLeft(), mFirstChild.getTop() - deltaY / 3, mFirstChild.getRight(), mFirstChild.getBottom() - deltaY / 3);
//                        mFirstChild.scrollBy(0,deltaY);
                    } else {
                        super.onTouchEvent(ev);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 开启动画移动

    public void animation() {
        // 开启移动动画
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, normal.top - mFirstChild.getTop());
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationFinish = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFirstChild.clearAnimation();
                // 设置回到正常的布局位置
                mFirstChild.layout(normal.left, normal.top, normal.right, normal.bottom);
                normal.setEmpty();
                isAnimationFinish = true;
            }
        });
        mFirstChild.startAnimation(animation);
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove() {
        //getMeasuredHeight() 控件的原始高度  getHeight() 界面的实际高度
        int offset = mFirstChild.getMeasuredHeight() - getHeight();
//        Log.e("TAG","getMeasuredHeight():"+mFirstChild.getMeasuredHeight()+"  getHeight():"+getHeight());
        //getScrollY() 控件顶部到屏幕顶部的高度
        int scrollY = getScrollY();
//        Log.e("TAG",scrollY+"");
        //判断到达顶部还是底部scrollY=0 到达顶部  scrollY=offset到达底部
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

}