package adolf.com.musicviewer.view;

import java.io.Serializable;
import java.util.List;
import java.util.Random;







import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import adolf.com.musicviewer.R;
import adolf.com.musicviewer.bean.ArtistInfo;
import adolf.com.musicviewer.common.DensityUtil;
import adolf.com.musicviewer.common.Preference;
import adolf.com.musicviewer.ui.MainActivity;
import adolf.com.musicviewer.ui.RanksActivity;
import adolf.com.musicviewer.ui.VideoPlayActivity;

/**
 * UI界面
 * 
 * */
public class LancherLayout extends LinearLayout implements ShadowCallBack, View.OnClickListener, View.OnFocusChangeListener {

	private String TAG = "SettingLayout";

	private Context context;

	private ScaleAnimEffect animEffect;
	private ImageView[] shadowBackgrounds = new ImageView[3];
	private FrameLayout[] frameLayoutViews = new FrameLayout[3];
	public ImageView[] imageViews = new ImageView[3];
	private AutoScrollTextView[] tvs = new AutoScrollTextView[3];
	private int listIndex;//列表位置索引
	private ImageView whiteBorder;// 白色边界
	private List<ArtistInfo> infoList;
	private int randomTemp = -1;
	private ImageLoader imageLoader;
	private	DisplayImageOptions options = null;
	private ImageLoadingListener imageLoadingListener;
	private int listCount;
	ImageView[] arrayOfImageView = new ImageView[3];// 倒影
	loadMore mListener;
	int[] loc,loc1,loc2;
	public LancherLayout(Context paramContext , int listIndex , List<ArtistInfo> infoList) {
		super(paramContext);
		this.context = paramContext;
		this.animEffect = new ScaleAnimEffect();
		this.listIndex = listIndex;
		this.infoList = infoList;
		if (infoList.size()%3==0) {
			listCount=infoList.size()/3;
		}else {
			listCount=infoList.size()/3+1;
		}
		width = DensityUtil.dip2px(context, 310);// 放大前的�?
		height = DensityUtil.dip2px(context, 190);// 放大前的�?
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setGravity(Gravity.CENTER_HORIZONTAL);
		setOrientation(LinearLayout.VERTICAL);
		addView(LayoutInflater.from(this.context).inflate(R.layout.layout_lancher, null));
		initView();
	}
	public interface loadMore{
		public void loadMoreClick();
	}
	/**
	 * 随机变化item的背景色
	 * */
	private int createRandom(int paramInt) {
		Random localRandom = new Random();
		for (int i = localRandom.nextInt(paramInt);; i = localRandom.nextInt(paramInt)) {
			if (i != this.randomTemp) {
				this.randomTemp = i;
				return i;
			}
		}
	}

	/**
	 * 白色焦点框飞动�?移动、变�?
	 * 
	 * @param width
	 *                白色框的�?非放大后�?
	 * @param height
	 *                白色框的�?非放大后�?
	 * @param paramFloat1
	 *                x坐标偏移量，相对于初始的白色框的中心�?
	 *                y坐标偏移量，相对于初始的白色框的中心�?
	 * */
	private void flyWhiteBorder(int width, int height, float paramFloat1, float paramFloat2) {
		if ((this.whiteBorder != null)) {
			this.whiteBorder.setVisibility(View.VISIBLE);
			int mWidth = this.whiteBorder.getWidth();
			int mHeight = this.whiteBorder.getHeight();
			if (mWidth == 0 || mHeight == 0) {
				mWidth = 1;
				mHeight = 1;
			}
			ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder.animate();
			localViewPropertyAnimator.setDuration(150L);
			localViewPropertyAnimator.scaleX((float) (width * 1.105) / (float) mWidth);
			localViewPropertyAnimator.scaleY((float) (height * 1.105) / (float) mHeight);
			localViewPropertyAnimator.x(paramFloat1);
			localViewPropertyAnimator.y(paramFloat2);
			localViewPropertyAnimator.start();
		}
	}

	/**
	 * 失去焦点的的动画动作
	 * 
	 * @param paramInt
	 *                失去焦点的item
	 * */
	private void showLooseFocusAinimation(int paramInt) {
		this.animEffect.setAttributs(1.105F, 1.0F, 1.105F, 1.0F, 100L);
		this.imageViews[paramInt].startAnimation(this.animEffect.createAnimation());
		this.shadowBackgrounds[paramInt].setVisibility(View.GONE);
	}

	/**
	 * 获得焦点的item的动画动�?
	 * 
	 * @param paramInt
	 *                获得焦点的item
	 * */
	private void showOnFocusAnimation(final int paramInt) {
		this.frameLayoutViews[paramInt].bringToFront();
		this.animEffect.setAttributs(1.0F, 1.105F, 1.0F, 1.105F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		localAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				shadowBackgrounds[paramInt].startAnimation(animEffect.alphaAnimation(0.0F, 1.0F, 150L, 0L));
				shadowBackgrounds[paramInt].setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		this.imageViews[paramInt].startAnimation(localAnimation);
	}

	private OnClickListener onClickListener;

	public void destroy() {
	}

	/**
	 * 注册OnClickListener监听
	 * */
	public void initListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void initListener() {
	}

	/**
	 * 初始化视图
	 * */
	public void initView() {
		//image option
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.nothing)
		.showImageOnFail(R.drawable.nothing)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.build();
		
		int[] arrayOfInt = new int[7];
		arrayOfInt[0] = R.drawable.blue_no_shadow;
		arrayOfInt[1] = R.drawable.dark_no_shadow;
		arrayOfInt[2] = R.drawable.green_no_shadow;
		arrayOfInt[3] = R.drawable.orange_no_shadow;
		arrayOfInt[4] = R.drawable.pink_no_shadow;
		arrayOfInt[5] = R.drawable.red_no_shadow;
		arrayOfInt[6] = R.drawable.yellow_no_shadow;
		
		arrayOfImageView[0] = ((ImageView) findViewById(R.id.set_refimg_1));
		arrayOfImageView[1] = ((ImageView) findViewById(R.id.set_refimg_2));
		arrayOfImageView[2] = ((ImageView) findViewById(R.id.set_refimg_3));
		
		int i = arrayOfInt.length;
		this.frameLayoutViews[0] = ((FrameLayout) findViewById(R.id.layout1));
		this.frameLayoutViews[1] = ((FrameLayout) findViewById(R.id.layout2));
		this.frameLayoutViews[2] = ((FrameLayout) findViewById(R.id.layout3));

		this.shadowBackgrounds[0] = ((ImageView) findViewById(R.id.app_shadow));
		this.shadowBackgrounds[1] = ((ImageView) findViewById(R.id.game_shadow));
		this.shadowBackgrounds[2] = ((ImageView) findViewById(R.id.setting_shadow));

		
		
		this.imageViews[0] = ((ImageView) findViewById(R.id.iv1));
		this.imageViews[1] = ((ImageView) findViewById(R.id.iv2));
		this.imageViews[2] = ((ImageView) findViewById(R.id.iv3));
		
		this.tvs[0] = (AutoScrollTextView) findViewById(R.id.tv1);
		this.tvs[1] = (AutoScrollTextView) findViewById(R.id.tv2);
		this.tvs[2] = (AutoScrollTextView) findViewById(R.id.tv3);
		
		if(3*listIndex<infoList.size()){
			tvs[0].setText(infoList.get(3*listIndex).getTitle());
			tvs[0].init(((Activity)context).getWindowManager());
//			tvs[0].startScroll();
			imageLoader.displayImage(infoList.get(3*listIndex).getImg(), imageViews[0], options,new ImageLoadingListener() {
				

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1,
						Bitmap arg2) {
					arrayOfImageView[0].setImageBitmap(ImageReflect.createCutReflectedImage(ImageReflect.convertViewToBitmap(frameLayoutViews[0]), 0));
					
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1,
						FailReason arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		if (3*listIndex+1<infoList.size()) {
			tvs[1].setText(infoList.get(3*listIndex+1).getTitle());
			imageLoader.displayImage(infoList.get(3*listIndex+1).getImg(), imageViews[1], options,new ImageLoadingListener() {

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1,
						Bitmap arg2) {
					arrayOfImageView[1].setImageBitmap(ImageReflect.createCutReflectedImage(ImageReflect.convertViewToBitmap(frameLayoutViews[1]), 0));
					
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1,
						FailReason arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
				

			});
		}
		if (3*listIndex+2<infoList.size()) {
			tvs[2].setText(infoList.get(3*listIndex+2).getTitle());
			imageLoader.displayImage(infoList.get(3*listIndex+2).getImg(), imageViews[2], options,new ImageLoadingListener() {

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1,
						Bitmap arg2) {
					arrayOfImageView[2].setImageBitmap(ImageReflect.createCutReflectedImage(ImageReflect.convertViewToBitmap(frameLayoutViews[2]), 0));
					
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1,
						FailReason arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
				

			});
		}
		
		tvs[1].init(((Activity)context).getWindowManager());
		tvs[2].init(((Activity)context).getWindowManager());
		
//		tvs[1].startScroll();
//		tvs[2].startScroll();

		
		this.whiteBorder = ((ImageView) findViewById(R.id.white_boder));

		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(340, 120);
		layoutparams.leftMargin = 168;
		layoutparams.topMargin = 20;
		whiteBorder.setLayoutParams(layoutparams);

		for (int j = 0; j < 3; j++) {
			this.shadowBackgrounds[j].setVisibility(View.GONE);
			// 随机产生背景
			imageViews[j].setBackgroundResource(arrayOfInt[createRandom(i)]);
			imageViews[j].setOnFocusChangeListener(this);
			imageViews[j].setOnClickListener(this);
			
		}
	}

	
	@Override
	public void onClick(View paramView) {
//			onClickListener.onClick(paramView);
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			switch (paramView.getId()) {
			case R.id.iv1:
				intent.setClass(context, VideoPlayActivity.class);
				bundle.putSerializable("infoList", (Serializable) infoList);
				bundle.putInt("index", 3*listIndex);
				intent.putExtras(bundle);
				context.startActivity(intent);
				break;
			case R.id.iv2:
				
				intent.setClass(context, VideoPlayActivity.class);
				bundle.putSerializable("infoList", (Serializable) infoList);
				bundle.putInt("index", 3*listIndex+1);
				intent.putExtras(bundle);
				context.startActivity(intent);
				break;
			case R.id.iv3:
				intent.setClass(context, VideoPlayActivity.class);
				bundle.putSerializable("infoList", (Serializable) infoList);
				bundle.putInt("index", 3*listIndex+2);
				intent.putExtras(bundle);
				context.startActivity(intent);
				break;
			
			default:
				break;
			}
	}

	private float x = 0.0F;
	private float y = 0.0F;
	private int width = 0;// 放大前的�?
	private int height = 0;// 放大前的�?

	private OnFocusChangeListener onFocusChangeListener;

	/**
	 * 注册焦点监听的动�?
	 * */
	public void initListener(OnFocusChangeListener onFocusChangeListener) {
		this.onFocusChangeListener = onFocusChangeListener;
	}
	
	@Override
	public void onFocusChange(View paramView, boolean paramBoolean) {
		if (onFocusChangeListener != null) {
			onFocusChangeListener.onFocusChange(paramView, paramBoolean);
		}
		if (context.getClass().equals(MainActivity.class)) {
			if (listIndex==listCount-1) {
				((MainActivity)context).loadMore(true);
			}else {
				((MainActivity)context).loadMore(false);
			}
		}
		if (context.getClass().equals(RanksActivity.class)) {
			if (listIndex==listCount-1) {
				((RanksActivity)context).loadMore(true);
			}else {
				((RanksActivity)context).loadMore(false);
			}
		}
		int i = -1;
		int activityHeight = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
		double rate = 1080/(double)activityHeight;
		int yoff = (int) (130/rate);
		loc = new int[2];
		loc1 = new int[2];
		loc2 = new int[2];
		if (listIndex==0) {
			imageViews[0].getLocationInWindow(loc);
			imageViews[1].getLocationInWindow(loc1);
			imageViews[2].getLocationInWindow(loc2);
			Preference.putInt("borderX", loc[0]);
			Preference.putInt("borderX1", loc1[0]);
			Preference.putInt("borderX2", loc2[0]);
		}else {
			loc[0] = Preference.getInt("borderX");
			loc1[0] = Preference.getInt("borderX1");
			loc2[0] = Preference.getInt("borderX2");
		}
		int offset = (whiteBorder.getWidth()-imageViews[0].getWidth())/2;
		switch (paramView.getId()) {
		case R.id.iv1:
			i = 0;
			// 此处设置不同大小的item的长�?,下面同理省去，同样大小，就未设置,直接初始
			x = loc[0]-offset;
			y = yoff;
			// 此处可封装出数学算法，计算偏移量的x,y值�?我懒得算了，直接设置�?
			break;
		case R.id.iv2:
			i = 1;
			x = loc1[0]-offset;
			y = yoff;
			break;
		case R.id.iv3:
			i = 2;
			x = loc2[0]-offset;
			y = yoff;
			break;
		}
		if (paramBoolean) {
//			showOnFocusAnimation(i);
//			Message message = new Message();
//			message.what = 0;
//			mHandler.sendMessage(message);
			switch (i) {
			case 0:
				((LinearLayout)findViewById(R.id.borderLL1)).setBackgroundResource(R.drawable.border_bg);
				tvs[0].startScroll();
				break;
			case 1:
				((LinearLayout)findViewById(R.id.borderLL2)).setBackgroundResource(R.drawable.border_bg);
				tvs[1].startScroll();
				break;
			case 2:
				((LinearLayout)findViewById(R.id.borderLL3)).setBackgroundResource(R.drawable.border_bg);
				tvs[2].startScroll();
				break;
			default:
				break;
			}
			
		} else {
			showLooseFocusAinimation(i);
			switch (i) {
			case 0:
				((LinearLayout)findViewById(R.id.borderLL1)).setBackgroundColor(getResources().getColor(R.color.transparent));
				tvs[0].stopScroll();
				break;
			case 1:
				((LinearLayout)findViewById(R.id.borderLL2)).setBackgroundColor(getResources().getColor(R.color.transparent));
				tvs[1].stopScroll();
				break;
			case 2:
				((LinearLayout)findViewById(R.id.borderLL3)).setBackgroundColor(getResources().getColor(R.color.transparent));
				tvs[2].stopScroll();
				break;
			default:
				break;
			}
			this.whiteBorder.setVisibility(View.INVISIBLE);
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			flyWhiteBorder(width, height, x, y);
		}
	};

	public void updateData() {
	}


}