package com.znouy.slidingmenuview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.znouy.slidingmenuview.R;

public class SlidingMenuView extends ViewGroup {

	private static final String tag = "SlidingMenuView";

	private View mLeft_menu;
	private View mMain_content;
	private int mLeftmenu_width;
	private float downX;
	private float downY;
	private float mDownX;
	private Scroller mScroller;
	public boolean isOpen = false;// 菜单默认是关
	private long downT;

	public SlidingMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(getContext());
	}

	public SlidingMenuView(Context context) {
		this(context, null);
	}

	/** 2.子组件测量完成后进行布局 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int dx = 0;// x位置的偏移量

		// 确定主内容位置
		int mainContentLeft = 0 + dx;
		int mainContentTop = 0;
		int mainContentRight = mMain_content.getMeasuredWidth() + dx;
		int mainContentBottom = mMain_content.getMeasuredHeight();
		mMain_content.layout(mainContentLeft, mainContentTop, mainContentRight,
				mainContentBottom);
		// 确定左菜单位置
		int leftMenuleft = -mLeft_menu.getMeasuredWidth() + dx;
		int leftMenuTop = 0;
		int leftMenuRight = 0 + dx;
		int leftMenuBottom = mLeft_menu.getMeasuredHeight();
		mLeft_menu.layout(leftMenuleft, leftMenuTop, leftMenuRight,
				leftMenuBottom);
	}

	/** 2.子组件测量 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 主内容测量
		mMain_content.measure(widthMeasureSpec, heightMeasureSpec);
		// 左菜单测量-对宽度的模式测量-精确
		int leftWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mLeftmenu_width,
				MeasureSpec.EXACTLY);
		mLeft_menu.measure(leftWidthMeasureSpec, heightMeasureSpec);
		// 设置测量后的值（不带模式）
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	/** 1.获取子孩子 */
	@Override
	protected void onFinishInflate() {
		mLeft_menu = getChildAt(0);
		mMain_content = getChildAt(1);

		// 对左菜单进行精确测量
		LayoutParams layoutParams = mLeft_menu.getLayoutParams();
		mLeftmenu_width = layoutParams.width;
		super.onFinishInflate();
	}

	/** ViewGroup的事件分发 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		// 按默认方式处理-会到其onInterceptTouchEvent中判断是否将事件拦截
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		// 判断是横向滑动还是纵向滑动，横向滑动事件拦截，执行自己的onTouchEvent，使横向滑动时scrollView也可滑动，
		// 纵向滑动放行，执行子容器的disPatchTouchEvent，就不执行onTouchEvent，不会横向滑动屏幕
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = ev.getX();
			downY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = ev.getX();
			float moveY = ev.getY();

			float dx = moveX - downX;
			float dy = moveY - downY;
			if (Math.abs(dx) > Math.abs(dy)) {// 横向滑动拦截
				return true;
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		// 处理触摸事件
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();

			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getX();
			// 获取View在x轴的位移
			// int dx = Math.round(mDownX - moveX);// 为负数表示向左平移了View(画布)
			int dx = -Math.round(moveX - mDownX);// 移动的量，-表示向左移动
			// 获取平移前View在屏幕左边界的x坐标
			int scrollX = getScrollX();
			if (scrollX + dx < -mLeft_menu.getMeasuredWidth()) {// 左边界越界
				// 完全显示左侧菜单
				scrollTo(-mLeft_menu.getMeasuredWidth(), 0);
			} else if (scrollX + dx > 0) {// 右边界越界
				// 完全显示主内容
				scrollTo(0, 0);
			} else {// 没有越界
				scrollBy(dx, 0);
			}

			mDownX = moveX;// 移动过程中不断改变按下的位置
			break;
		case MotionEvent.ACTION_UP:
			// 获取屏幕松开时viewgroup左边界的位置（在屏幕左边界的位置）
			int scrollX2 = getScrollX();
			if (scrollX2 < -mLeft_menu.getMeasuredWidth() / 2) {
				scrollAnimation();
				// 完全显示左侧菜单,带有动画
				// scrollTo(-mLeft_menu.getMeasuredWidth(), 0);
				isOpen = true;
			} else {
				// scrollTo(0, 0);
				isOpen = false;
			}
			scrollAnimation();
			break;

		default:
			break;
		}
		return true;// 自己消费事件
	}

	/**
	 * 移动动画
	 */
	private void scrollAnimation() {
		Log.d(tag, "isOpenLeftMenu==" + isOpen);
		// 获取view在屏幕左边的开始坐标和结束坐标
		int startX = getScrollX();
		int endX = 0;
		if (isOpen) {
			endX = -mLeft_menu.getMeasuredWidth();
		} else {

		}
		int dx = endX - startX;
		int dy = 0;
		int duration = Math.abs(dx) * 2;
		mScroller.startScroll(startX, 0, dx, dy, duration);

		invalidate();// 刷新界面
	}

	@Override
	public void computeScroll() {
		// 判断动画是否结束
		if (mScroller.computeScrollOffset()) {// 返回true表示没有完成
			int currX = mScroller.getCurrX();
			scrollTo(currX, 0);

			invalidate();
		}
		super.computeScroll();
	}

	/**
	 * 打开或者关闭左侧菜单 如果之前是开则关闭
	 */
	public void toggle() {
		if (!isOpen) {// 如果是关，则点击开关就打开菜单
			openMenu();
		} else {
			closeMenu();
		}
	}

	/**
	 * 打开菜单
	 */
	public void openMenu() {
		isOpen = true;
		scrollAnimation();
	}

	/**
	 * 关闭菜单
	 */
	public void closeMenu() {
		isOpen = false;
		scrollAnimation();
	}

	/**
	 * 设置左侧菜单选项卡选中
	 * 
	 * @param tv
	 */
	public void setItemSelected(TextView tv) {
		// 还原所有状态
		findViewById(R.id.news).setSelected(false);
		findViewById(R.id.bbs).setSelected(false);
		findViewById(R.id.blog).setSelected(false);
		findViewById(R.id.my).setSelected(false);
		findViewById(R.id.question).setSelected(false);
		Log.d(tag, tv.getClass().getSimpleName());
		// 设置选中的状态
		tv.setSelected(true);

	}

}
