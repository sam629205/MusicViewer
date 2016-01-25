package adolf.com.musicviewer.ui;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import adolf.com.musicviewer.R;
import adolf.com.musicviewer.adapter.QueryAdapter;
import adolf.com.musicviewer.bean.ArtistInfo;
import adolf.com.musicviewer.function.getRealURL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class YueListLoadActivity extends Activity{
    private List<ArtistInfo> infoList = new ArrayList<ArtistInfo>();
	private ImageLoader imageLoader = null;
	private	DisplayImageOptions options = null;
	private int mStatus=0;
    private FrameLayout[] flList= new FrameLayout[20];
    private ImageView[] ivList=new ImageView[20];
    private TextView[] tvList=new TextView[20];
    private String mDefiniton;
    private String urlStr;
    private int pageNum=1;
    private TextView tv_next;
    private PullToRefreshListView ptrl;
	private ProgressDialog waitBar;
	private QueryAdapter adapter;
	public static String URL = "url";
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.fragment_main);
	createView();
	initView();
	
}

private void createView() {
	ptrl = (PullToRefreshListView) findViewById(R.id.ptrl);
	
}

private void initView() {
	urlStr = getIntent().getStringExtra(URL);
	ptrl.setOnRefreshListener(new MyRefreshListener(ptrl));
	ptrl.setMode(Mode.BOTH);
	ptrl.getRefreshableView().setItemsCanFocus(true);
	new GetDataTask().execute();
}
private class GetDataTask extends AsyncTask<String, Void, List<ArtistInfo>> {
	@Override
	protected void onPreExecute() {
			waitBar = ProgressDialog.show(YueListLoadActivity.this, "",
					"正在拼命解析中，请稍候");
					waitBar.setCancelable(true);
	};
	@Override
	protected List<ArtistInfo> doInBackground(String... params) {
		getRealURL get = new getRealURL();
		String url;
		url = urlStr;
		infoList=get.parseWeb(url.toString(), 3);
        return infoList;
		
	}

	@Override
	protected void onPostExecute(final List<ArtistInfo> resultList) {
		// 关闭等待对话框
		if (waitBar != null) {
			waitBar.dismiss();
			waitBar = null;
		}
		if (resultList.size()>0) {
		super.onPostExecute(resultList);
		if (pageNum==1) {
			adapter = new QueryAdapter(YueListLoadActivity.this,resultList);
			ptrl.setAdapter(adapter);
		}else {
			adapter.addData(resultList);
		}
		pageNum++;
		ptrl.onRefreshComplete();
	}else {
		Toast.makeText(YueListLoadActivity.this, "没有查询到数据，重新设置查询条件", Toast.LENGTH_SHORT).show();
	}
}
}
private class MyRefreshListener implements OnRefreshListener2<ListView>{

	private PullToRefreshListView mPtflv;

	public MyRefreshListener(PullToRefreshListView lv_result) {
		this.mPtflv = lv_result;
	}
	
	@Override
	public void onPullDownToRefresh(
			PullToRefreshBase<ListView> refreshView) {
		pageNum=1;
		new GetDataTask().execute("");
		
	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<ListView> refreshView) {
		new GetDataTask().execute("");
		
	}
	
}
}
