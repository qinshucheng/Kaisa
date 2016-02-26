package com.qsc.heartratefragment;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qsc.barchartfragment.HeartrateDB;
import com.qsc.linechartfragment.LinechartFragment;
import com.qsc.main.R;


/**
 * This class extends Activity to handle a picture preview, process the preview
 * for a red values and determine a heart beat.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
@SuppressWarnings("deprecation")
public class HeartRateFragment extends Fragment {

    private static final String TAG = "HeartRateFragment";
    private static final AtomicBoolean processing = new AtomicBoolean(false);//TODO

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView txtHeartBeatNumber = null,txtcovercam = null,txtouch = null,txthistory = null;
    private static ImageView imgHistory; 
    private static ProgressBar progressBar;

    private static WakeLock wakeLock = null;   
    
    
    private static int index = 0;

    public static enum TYPE {
        GREEN, RED
    };
    private static TYPE currentType = TYPE.GREEN;
    public static TYPE getCurrent() {
        return currentType;
    }

    private static int myrate = 0;
    
    private static final int REFLESHRATE = 1;
    @SuppressLint("HandlerLeak")
	private Handler handler =new Handler(){
    	public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFLESHRATE:
		        if (camera!=null) {
		        	camera.setPreviewCallback(null);
		            camera.stopPreview();
		            camera.release();
		            camera = null;
				}
		        progressBar.setVisibility(View.GONE);
		        txthistory.setVisibility(View.VISIBLE);
		        imgHistory.setVisibility(View.VISIBLE);
		        txthistory.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Fragment viewPagerFragment = new  LinechartFragment();
						FragmentManager viewpagerFragmentManager = getFragmentManager();
						viewpagerFragmentManager.beginTransaction()
								.addToBackStack(null)   
								.replace(R.id.view_content, viewPagerFragment)
								.commit();
						
					}
				});
		        
		        writedb();
		        
				break;
			default:
				break;
			}
    	};
    };
    
    private void writedb(){
		hd = new HeartrateDB(getActivity());
		SQLiteDatabase dbwrite = hd.getWritableDatabase();
        ContentValues cValues = new ContentValues();
     
        cValues.put("heartrate", myrate);
        dbwrite.insert("myheartrate", null, cValues);
	}
	private HeartrateDB hd;

    /**
     * {@inheritDoc}
     */
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	System.out.println("--->onCreateView");
		final View view = inflater.inflate(R.layout.fragment_heartrate, container, false);
		image = view.findViewById(R.id.image);
		txtouch = (TextView) view.findViewById(R.id.txtouch);
		txtouch.setText(R.string.touch);
		txtcovercam = (TextView) view.findViewById(R.id.txtcovercam);
        txtHeartBeatNumber = (TextView) view.findViewById(R.id.heartbeatnumbertext);
        
        txthistory = (TextView) view.findViewById(R.id.txtHistory);
        imgHistory = (ImageView) view.findViewById(R.id.imgHistory);
        txthistory.setVisibility(View.GONE);
        imgHistory.setVisibility(View.GONE);
        
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "font/lcd.ttf");
        txtHeartBeatNumber.setTypeface(tf);    
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        
        preview = (SurfaceView) view.findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "--->onResume()");
		camera = Camera.open();
		Log.i(TAG, "--->Camera.open()");
        wakeLock.acquire();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "--->onPause()");
        wakeLock.release(); 
        if (camera!=null) {
        	camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
		}
    }
    int[] heart = new int[100];
    int[] nowtime = new int[100];
    int[] toptime = new int[20];
    private   PreviewCallback previewCallback = new PreviewCallback() {
		/**
         * {@inheritDoc}
         */
    	
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();
            if (!processing.compareAndSet(false, true)) return; 
            nowtime[index] = (int) (System.currentTimeMillis() % 1000000);//获取当前图像时间
            int width = size.width;
            int height = size.height;
            int imgsum = ImageProcessing_c.decodeYUV420SPtoY(data.clone(), height, width);//计算图片灰度和
            heart[index] = imgsum;
            Log.i(TAG, "灰度和 "+index+"="+heart[index]);
            Log.i(TAG, "时间点 "+index+"="+nowtime[index]);
            index++;
            if (index<100) {
            	processing.set(false);
				return;
			}
            Log.i(TAG, heart[0]+","+heart[1]+","+heart[2]+","+heart[3]+","+heart[4]+","+heart[5]+","+heart[6]+","+heart[7]+","+heart[8]+","+heart[9]+","+heart[10]+","+heart[11]+","+heart[12]+","+heart[13]+","+heart[14]+","+heart[15]+","+heart[16]+","+heart[17]+","+heart[18]+","+heart[19]+","+heart[20]+","+heart[21]+","+heart[22]+","+heart[23]+","+heart[24]+","+heart[25]+","+heart[26]+","+heart[27]+","+heart[28]+","+heart[29]+","+heart[30]+","+heart[31]+","+heart[32]+","+heart[33]+","+heart[34]+","+heart[35]+","+heart[36]+","+heart[37]+","+heart[38]+","+heart[39]+","+heart[40]+","+heart[41]+","+heart[42]+","+heart[43]+","+heart[44]+","+heart[45]+","+heart[46]+","+heart[47]+","+heart[48]+","+heart[49]+","+heart[50]+","+heart[51]+","+heart[52]+","+heart[53]+","+heart[54]+","+heart[55]+","+heart[56]+","+heart[57]+","+heart[58]+","+heart[59]+","+heart[60]+","+heart[61]+","+heart[62]+","+heart[63]+","+heart[64]+","+heart[65]+","+heart[66]+","+heart[67]+","+heart[68]+","+heart[69]+","+heart[70]+","+heart[71]+","+heart[72]+","+heart[73]+","+heart[74]+","+heart[75]+","+heart[76]+","+heart[77]+","+heart[78]+","+heart[79]+","+heart[80]+","+heart[81]+","+heart[82]+","+heart[83]+","+heart[84]+","+heart[85]+","+heart[86]+","+heart[87]+","+heart[88]+","+heart[89]+","+heart[90]+","+heart[91]+","+heart[92]+","+heart[93]+","+heart[94]+","+heart[95]+","+heart[96]+","+heart[97]+","+heart[98]+","+heart[99]);
            index = 0;
            //寻找序列heart[100]的峰值并计数
            int rate = 0;
            
            for (int i = 6; i < 95; i++) {
				for (int j = 1; j < 4; j++) {
					if (heart[i]>heart[i-j]&&heart[i]>heart[i+j]) {
						if (j<3) {
							continue;
						}else {
							rate++;
							toptime[rate-1] = nowtime[i];//获取峰值对应的时间
							Log.i(TAG, "峰值 "+i+"="+heart[i]);
							Log.i(TAG, "峰值时间 "+i+"="+nowtime[i]);
							}
					}else {
						break;
					}
				}
			}
            long timesum = toptime[rate-1] - toptime[0];//最后一个峰值对应时间-第一个峰值对应时间
            myrate = (int) (60000 / timesum * (rate-1));
            if (myrate < 30 || myrate > 180) {
            	txtcovercam.setText(R.string.discription);
            	txtHeartBeatNumber.setText("00");
                myrate = 0;
                heart = null;
                nowtime = null;
                toptime = null;
                System.gc();
                processing.set(false);
                return;
            }
            txtcovercam.setText(null);
            heart = null;
            nowtime = null;
            toptime = null;
            System.gc();
            txtHeartBeatNumber.setText(String.valueOf(myrate));
            handler.sendEmptyMessage(REFLESHRATE);//发送停止预览信号
            
        }
        
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {//TODO

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			final Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            progressBar.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    				camera.setParameters(parameters);
    				camera.startPreview();
    				progressBar.setEnabled(false);
    				txtouch.setText(null);
    				
    			}
    		});
            
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        	if (camera!=null)
            {
                camera.stopPreview();
            }
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }
}
