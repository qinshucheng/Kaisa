package com.qsc.maplocationfragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.qsc.main.R;

/**
 * 此demo用来展示如何结合定位SDK实现定位,并使用MyLocationOverlay绘制定位位置,展示如何使用自定义图标,记录轨迹,记录里程
 * 
 */
@SuppressLint("SdCardPath")
public class MaplocationActivity extends Activity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	// UI相关
	MapView mMapView;
	BaiduMap mBaiduMap;

	boolean isFirstLoc = true;// 是否首次定位
	boolean isRecording = false;//是否在记录 
	boolean isFirstRecord = true;//是否首次记录
	//定位模式下设置当前位置
	private LatLng local;
	//记录模式下设置当前定位位置和上一次定位位置
	private LatLng nowlocal=null; //当前
	private LatLng lastlocal=null; //上一次
	private double distance = 0.0; //总距离
	//截图路径
	private File file = new File("/mnt/sdcard/recordShot.png");
	
	//设置显示路程View
	private TextView distanceView;
	private ImageView map_topbar;
	//设置记录按钮
	private ImageView btnStartRecord,btnStopRecord,btnClear,btnScreenShot,btnScreenShotShare;
	//设置定位间隔时间(Ms)
	private static final int UPDATE_TIME = 3000;
    //设置Handler消息
	private static final int DISTANCE_UNEQUAL_ZERO = 1;
	private static final int DISTANCE_EQUAL_ZERO = 0;
	@SuppressLint("HandlerLeak")
	private Handler Handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DISTANCE_UNEQUAL_ZERO:
				distanceView.setText("我走了"+(int)distance+"米");
				break;
			case DISTANCE_EQUAL_ZERO:
				distanceView.setText("我走了0米");
			default:
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maplocation);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(UPDATE_TIME); //定位时间间隔
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mMapView.refreshDrawableState();  //刷新
		
		distanceView = (TextView) findViewById(R.id.distanceView);
		distanceView.setVisibility(View.GONE);
		distanceView.setTextColor(Color.GREEN);
		distanceView.setTextSize(20);
		
		map_topbar = (ImageView) findViewById(R.id.map_topbar);
		btnStartRecord  = (ImageView) findViewById(R.id.btnStartRecord);
		btnStopRecord   = (ImageView) findViewById(R.id.btnStopRecord);
		btnClear        = (ImageView) findViewById(R.id.btnClear);               
		btnScreenShot   = (ImageView) findViewById(R.id.btnScreenShot);
		btnScreenShotShare = (ImageView) findViewById(R.id.btnScreenShotShare);
		btnStopRecord.setVisibility(View.GONE);
		btnClear.setVisibility(View.GONE);
		btnScreenShot.setVisibility(View.GONE);
		btnScreenShotShare.setVisibility(View.GONE);
		OnClickListener onClickListener =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v.equals(btnStartRecord)) {
					isRecording = true;
					distanceView.setVisibility(View.VISIBLE);
					btnStartRecord.setVisibility(View.GONE);
					btnStopRecord.setVisibility(View.VISIBLE);
					btnClear.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.GONE);
				}else if (v.equals(btnStopRecord)) {
					isRecording = false;
					isFirstRecord = true;
					btnStopRecord.setVisibility(View.GONE);
					btnStartRecord.setVisibility(View.VISIBLE);
					btnClear.setVisibility(View.VISIBLE);
					btnScreenShot.setVisibility(View.VISIBLE);
					btnScreenShotShare.setVisibility(View.GONE);
				}else if (v.equals(btnClear)) {
					clearRecord();
					isRecording = false;
					isFirstRecord = true;
					distanceView.setVisibility(View.GONE);
					btnClear.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.GONE);
					distance = 0.0;
					Handler.sendEmptyMessage(DISTANCE_EQUAL_ZERO);
				}else if (v.equals(btnScreenShot)) {
					btnScreenShot.setVisibility(View.GONE);
					btnScreenShotShare.setVisibility(View.VISIBLE);
					//截取轨迹图并保存到SD卡
					mBaiduMap.snapshot(new SnapshotReadyCallback() {
						@SuppressLint("SdCardPath")
						@Override
						public void onSnapshotReady(Bitmap snapshot) {
							
							FileOutputStream out;
							try {
								out = new FileOutputStream(file);
								if (snapshot.compress(Bitmap.CompressFormat.PNG, 100, out)) {
									out.flush();
									out.close();
								}
								Toast.makeText(MaplocationActivity.this, 
										"截图成功，图片保存在："+file.toString(),
										Toast.LENGTH_SHORT).show();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}catch (IOException e) {
								e.printStackTrace();
							}
							
						}
					});	
					Toast.makeText(MaplocationActivity.this,
							"正在截取图片...", 
							Toast.LENGTH_SHORT).show();
				}else if (v.equals(btnScreenShotShare)) {
					btnScreenShotShare.setVisibility(View.GONE);
					btnScreenShot.setVisibility(View.VISIBLE);
					//分享截图
					shareMsg(file.toString() );
				}else if (v.equals(map_topbar)) {
					Intent intent=new Intent();
					intent.setClass(MaplocationActivity.this, com.qsc.main.MainActivity.class);
					startActivity(intent);
					finish();
				}				
			}
		};
		btnStartRecord.setOnClickListener(onClickListener);
		btnStopRecord.setOnClickListener(onClickListener);
		btnClear.setOnClickListener(onClickListener);
		btnScreenShot.setOnClickListener(onClickListener);
		btnScreenShotShare.setOnClickListener(onClickListener);
		map_topbar.setOnClickListener(onClickListener);
	  
	}
	
	/**  
	  * 分享功能  
	  * @param imgPath 图片路径，不分享图片则传null  
	  */  
	 public void shareMsg(String imgPath) {  
		 Intent intent = new Intent(Intent.ACTION_SEND);  
		 if (imgPath == null || imgPath.equals("")) {  
			 intent.setType("text/plain"); // 纯文本  
		 } else {  
			 File f = new File(imgPath);  
			 if (f != null && f.exists() && f.isFile()) {  
				 intent.setType("image/*");  
				 Uri u = Uri.fromFile(f);  
				 intent.putExtra(Intent.EXTRA_STREAM, u); 
			 }  
		 }  
		 intent.putExtra(Intent.EXTRA_TEXT, "我已经走了"+distance+"米");
		 startActivity(Intent.createChooser(intent, "分享到"));
	 } 
	
	 /**
	  * 清除轨迹记录
	  */
	public void clearRecord() {
		// 清除所有图层
		mBaiduMap.clear();
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		
		//private LocationMode mCurrentMode = LocationMode.FOLLOWING;
		//private BitmapDescriptor mCurrentMarker=BitmapDescriptorFactory.fromResource(R.drawable.loc1_m);;

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不再处理新接收的位置
			if (location == null || mMapView == null)
				return;
			//第一次定位时
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng firstlocal = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(firstlocal);
				mBaiduMap.animateMapStatus(u); //将当前位置显示到地图中心
				
			}
			//非第一次定位
			
			MyLocationData locData = new MyLocationData.Builder()//如果不显示定位精度圈，将accuracy赋值为0即可 
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData); //更新定位数据  
			mMapView.refreshDrawableState();
			//自定义定位图标
			//mBaiduMap
			//.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
			
			//获取当前位置的经纬度
			local = new LatLng(location.getLatitude(),
					location.getLongitude());
			
			//按下记录按钮后记录轨迹并计算距离
			if (isRecording) {
				if (isFirstRecord) {
					lastlocal = local;
					isFirstRecord = false;
				}
				//当前位置赋予nowlocal
				nowlocal = local;
				//求距离并画线
				getRecord();
				lastlocal = nowlocal;
			}
			
		}
		
		//记录轨迹并计算距离函数
		private void getRecord(){
			//测距
			double dis = DistanceUtil.getDistance(nowlocal, lastlocal);
			//计算总距离
			distance +=dis;
			Handler.sendEmptyMessage(DISTANCE_UNEQUAL_ZERO);
		/*	new AlertDialog.Builder(MainActivity.this)
			      .setTitle("我已经走了：")
			      .setMessage(distance+"米")
			      .setPositiveButton("OK", null)
			      .show();
		*/	
			//画线
			List<LatLng> points = new ArrayList<LatLng>();
			points.add(nowlocal);
			points.add(lastlocal);
			OverlayOptions polyline = new PolylineOptions().width(4).color(0xAAFF0000).points(points);
			mBaiduMap.addOverlay(polyline);
		}
		
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
