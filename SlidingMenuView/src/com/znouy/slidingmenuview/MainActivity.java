package com.znouy.slidingmenuview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.znouy.slidingmenuview.view.SlidingMenuView;

public class MainActivity extends Activity {

	private static final String tag = "MainActivity";
	private SlidingMenuView smv;
	private TextView tv_content;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(tag, "onCreate");
		initView();

	}

	private void initView() {
		smv = (SlidingMenuView) findViewById(R.id.smv);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_title = (TextView) findViewById(R.id.tv_title);
		findViewById(R.id.news).setSelected(true);// 默认选中头条
		// 默认显示头条叶
		tv_title.setText("头条");
		tv_content.setText("头条选项卡的内容");

	}

	/**
	 * 主内容的开关点击事件
	 */
	public void onToggle(View v) {
		smv.toggle();
	}

	/**
	 * 当开关打开时关闭主内容界面
	 * @param V
	 */
	public void closeMenu(View V) {

		if (smv.isOpen) {
			smv.closeMenu();
		}
	}

	/**
	 * 做菜单选项卡的点击事件
	 * 
	 * @param v
	 */
	public void tagClick(View v) {
		Log.d(tag, "-----------tagClick");
		// 关闭菜单
		smv.closeMenu();
		// 替换对应的标题和内容
		TextView tv = (TextView) v;
		tv_title.setText(tv.getText());
		tv_content.setText(tv.getText() + "选项卡的内容");

		smv.setItemSelected(tv);// 设置被选中的选项卡
	}

}
