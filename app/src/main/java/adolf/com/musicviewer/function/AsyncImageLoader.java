package adolf.com.musicviewer.function;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	   //Ϊ�˼ӿ��ٶȣ������˻��棨��ҪӦ�����ظ�ͼƬ�϶�ʱ������ͬһ��ͼƬҪ��α����ʣ�������ListViewʱ���ع�����   
    private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();  
  
     /** 
     * 
     * @param imageUrl     ͼ��url��ַ 
     * @param callback     �ص��ӿ� 
     * @return     �����ڴ��л����ͼ�񣬵�һ�μ��ط���null 
     */  
    public Drawable loadDrawable(final String imageUrl, final ImageCallback callback) {  
        //�����ʹӻ�����ȡ�����   
        if (imageCache.containsKey(imageUrl)) {  
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);  
            if (softReference.get() != null) {  
                return softReference.get();  
            }  
        }  
  
        final Handler handler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) {  
                callback.imageLoaded((Drawable) msg.obj);  
            }  
        };  
        new Thread() {  
            public void run() {  
                Drawable drawable = loadImageFromUrl(imageUrl);  
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));  
                handler.sendMessage(handler.obtainMessage(0, drawable));  
  
            }  
  
        }.start();  
        /* 
        ����ע�͵���δ�����Handler��һ�ִ��淽�� 
         */  
//        new AsyncTask() {   
//            @Override   
//            protected Drawable doInBackground(Object... objects) {   
//                  Drawable drawable = loadImageFromUrl(imageUrl);   
//                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));   
//                return  drawable;   
//            }   
//   
//            @Override   
//            protected void onPostExecute(Object o) {   
//                  callback.imageLoaded((Drawable) o);   
//            }   
//        }.execute();   
        return null;  
    }  
  
    protected Drawable loadImageFromUrl(String imageUrl) {  
        try {  
            return Drawable.createFromStream(new URL(imageUrl).openStream(), "src");  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
    //����翪�ŵĻص��ӿ�   
    public interface ImageCallback {  
        public void imageLoaded(Drawable imageDrawable);  
    }  
}  

