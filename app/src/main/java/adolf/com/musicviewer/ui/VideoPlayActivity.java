package adolf.com.musicviewer.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import adolf.com.musicviewer.R;
import adolf.com.musicviewer.bean.ArtistInfo;
import adolf.com.musicviewer.bean.BaikeInfo;
import adolf.com.musicviewer.function.getRealURL;
import adolf.com.musicviewer.view.AlwaysMarqueeTextView;
import adolf.com.musicviewer.view.MyVideoView;
import adolf.com.musicviewer.view.NoScrollListView;

public class VideoPlayActivity extends Activity implements OnCompletionListener{
	private MyVideoView vv;
	private AlwaysMarqueeTextView tv_title;
	private MediaController mMediaController;
	private Uri mUri;
	private List<ArtistInfo> infoList;
	private int index;
	private static SharedPreferences appPreferences = null;
	private String mDefiniton;
	private AudioManager myAudioManager;
	private String reaLink;
	private Handler titleHandler;
	private PopupWindow popupWindow;
	private Button btn_cancel;
	private NoScrollListView lv;
	private WebView wv;
	private int rgSelection;
	private Context context;
	public static int REQUEST_CODE = 0 ;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_play);
    appPreferences=getSharedPreferences("Definition", 0);
	createView();
	initView();
}
private void createView(){
	tv_title = (AlwaysMarqueeTextView) findViewById(R.id.tv_title);
	vv = (MyVideoView) findViewById(R.id.vv);
}
private void initView(){
	titleHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			tv_title.setVisibility(View.GONE);
			super.handleMessage(msg);
		}
	};
	myAudioManager = (AudioManager)this.getSystemService(AUDIO_SERVICE); 
	mMediaController = new MediaController(VideoPlayActivity.this);
	OnClickListener prevListenter = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if (index==0) {
				index=infoList.size()-1;
			}else {
				index--;
			}
			play(index);
		}
	};
	OnClickListener nextListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if (index==infoList.size()-1) {
				index=0;
			}else {
				index++;
			}
			play(index);
		}
	};
	mMediaController.setPrevNextListeners(nextListener, prevListenter);
	vv.setMediaController(mMediaController);
	infoList = (List<ArtistInfo>) getIntent().getSerializableExtra("infoList");
	index = getIntent().getIntExtra("index", 0);
	vv.setOnCompletionListener(this);
	play(index);
}

@Override
protected void onResume() {
	super.onResume();
	play(index);
}
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	tv_title.setVisibility(View.VISIBLE);
	titleHandler.postDelayed(new Runnable() {
		
		@Override
		public void run() {
			Message msg = new Message();
			titleHandler.sendMessage(msg);
		}
	}, 2000);
	if (keyCode==KeyEvent.KEYCODE_ENTER) {
		if (vv.isPlaying()) {
			vv.stopPlayback();
		}else {
			vv.start();
		}
	}
	if (keyCode==KeyEvent.KEYCODE_MENU) {
		showPopupWindow();
	}
	if (keyCode==KeyEvent.KEYCODE_DPAD_UP) {
		myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		Toast.makeText(VideoPlayActivity.this, "当前音量大小为："+myAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), Toast.LENGTH_SHORT).show();
	}
	if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
		myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
		Toast.makeText(VideoPlayActivity.this, "当前音量大小为："+myAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), Toast.LENGTH_SHORT).show();
	}
	return super.onKeyDown(keyCode, event);
}

@Override
public void onCompletion(MediaPlayer arg0) {
	if (index==infoList.size()-1) {
		index=0;
	}else {
		index++;
	}
	play(index);
}
private void play(int i){
	tv_title.setText(infoList.get(index).getTitle());
	final Handler handler = new Handler();
	final StringBuilder str = new StringBuilder();
	str.append("http://www.flvcd.com/parse.php?kw=");
	String originLink = infoList.get(i).getLink();
	if (originLink==null||originLink.equals("")) {
		if (index==infoList.size()-1) {
			index=0;
		}else {
			index++;
		}
		play(index);
		return;
	}
	String codedLink = URLEncoder.encode(originLink);
	str.append(codedLink);
	str.append("&format=");
	mDefiniton=appPreferences.getString("Definition", null);
	str.append(mDefiniton);
	
	final Runnable getReaLink = new Runnable() {
		
		public void run() {
	        vv.setVideoURI(Uri.parse(reaLink));
	        vv.start();
		}
	};
	Thread thread = new Thread(){
		@Override
		public void run() {
			getRealURL get = new getRealURL();
	        reaLink = get.parseLink(str.toString());
			handler.post(getReaLink);
		}
	};
	thread.start();
//	new Thread(getReaLink).start();
}
private void showPopupWindow(){
	if (popupWindow!=null&&popupWindow.isShowing()) {
		popupWindow.dismiss();
	}else {
		View mView = initPopupWindow();
		popupWindow= new PopupWindow(mView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,true);
		popupWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
	}
}
private View initPopupWindow(){
	LayoutInflater inflater = LayoutInflater.from(VideoPlayActivity.this);
	View popWindow = inflater.inflate(R.layout.option_artistinfo, null);
	btn_cancel=(Button) popWindow.findViewById(R.id.btn_cancel);
	((RadioGroup)popWindow.findViewById(R.id.rg)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb1:
				rgSelection = 0 ;
				break;
			case R.id.rb2:
				
				rgSelection = 1 ;
				
				break;
			default:
				
				break;
			}
			
		}
	});
	btn_cancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (popupWindow!=null&&popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
		}
	});
	lv = (NoScrollListView) popWindow.findViewById(R.id.lv);
	wv = (WebView) popWindow.findViewById(R.id.wv_info);
	String[] artistStr = infoList.get(index).getTitle().split("-");
	String[] artistStr1 = artistStr[1].split("&"); 
	final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(VideoPlayActivity.this, android.R.layout.simple_list_item_1, artistStr1);
	lv.setAdapter(adapter);
	lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (rgSelection==0) {
				Intent intent = new Intent();
				intent.setClass(VideoPlayActivity.this, MainActivity.class);
				intent.putExtra("keyword", adapter.getItem(arg2));
				startActivity(intent);
				finish();
			}else if (rgSelection==1) {
				try {
					String mKwd = URLEncoder.encode((String) adapter.getItem(arg2), "UTF-8");
					new BaikeSearchTask().execute("http://baike.baidu.com/search?word="+mKwd);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	});
	return popWindow;
}
private class BaikeSearchTask extends AsyncTask<String, Void, BaikeInfo>{

	@Override
	protected BaikeInfo doInBackground(String... params) {
		return new getRealURL().parseBaike(params[0], 0);
	}
	@Override
	protected void onPostExecute(BaikeInfo resultInfo) {
		if (resultInfo!=null&&resultInfo.getUrlList().size()>0) {
			super.onPostExecute(resultInfo);
			final Uri uri = Uri.parse(resultInfo.getUrlList().get(0));          
			final Intent it = new Intent(Intent.ACTION_VIEW, uri);          
			startActivity(it);
		}
	}
}
}
