package com.qsc.linechartfragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsc.barchartfragment.HeartrateDB;
import com.qsc.main.R;
import com.qsc.swipelist.SwipeMenu;
import com.qsc.swipelist.SwipeMenuCreator;
import com.qsc.swipelist.SwipeMenuItem;
import com.qsc.swipelist.SwipeMenuListView;
import com.qsc.swipelist.SwipeMenuListView.OnMenuItemClickListener;
import com.qsc.swipelist.SwipeMenuListView.OnSwipeListener;

public class LinechartFragment extends Fragment {
	
	private Context context;
	private View view;
	private GraphicalView chart;
	private SwipeMenuListView mlist;
	private ListData[] listdata;
	private int [] rate;
	private String[] time;
	
	private BaseAdapter madapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ListData item = getItem(position);
			holder.iv_icon.setImageResource(item.icon);
			holder.tv_name.setText(String.valueOf(item.heartrate));
			holder.tvtime.setText(item.time);
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;
			TextView tvtime;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tvtime = (TextView) view.findViewById(R.id.tvtime);
				view.setTag(this);
			}
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public ListData getItem(int position) {
			// TODO Auto-generated method stub
			return listdata[position];
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listdata.length;
		}
	};
	private int dp2px(int dp) {//dp转换为px
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	private LinearLayout layout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_mydata, container, false);
		context = view.getContext();
		layout = (LinearLayout) view.findViewById(R.id.chart);

		readdb();           //查询数据库,提取数据
		givedata();         //向ListData类添加内容，并添加到Adapter
		SwipeMenuCreator creator = new SwipeMenuCreator() {//创建滑动构造器

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(//创建另外一个菜单按钮
						getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mlist.setMenuCreator(creator);//为listView添加菜单按钮生成器
		
		
		// set SwipeListener，滑动事件监听
		mlist.setOnSwipeListener(new OnSwipeListener() {
			
			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}
			
			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});
		
		
		PointStyle[] styles = new PointStyle[]{PointStyle.DIAMOND};
		int[] colors = new int[] { R.color.anqing};
		renderer = buildRenderer(colors,styles,true);
		setChartSettings(renderer, "My HeartRate", "", "次/分", 0.5,12.5, 0, 100, Color.GRAY, Color.LTGRAY);
		
		setchartdata();           //添加图表数据
		chart = ChartFactory.getLineChartView(context, dataset, renderer);
		layout.addView(chart);
		
		mlist.setOnMenuItemClickListener(new OnMenuItemClickListener() {//为菜单选项添加事件监听
			@Override//监听菜单按钮
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				ListData item = listdata[position];
				switch (index) {
				case 0:
					// delete
					delete(item);
					System.out.println("position-->"+position);
					readdb();
					givedata();
					handler.sendEmptyMessage(EMS);
					break;
				case 1:
					break;
				}
				return false;
			}
		});
		return view;
	}
	private static final int EMS = 1;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case EMS:
//				setchartdata();
//				System.out.println("series-length-->"+series.getItemCount());
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				for (int i = 0; i < series.getItemCount(); i++) {
//					System.out.println(series.getX(i)+","+series.getY(i));
//				}
//				dataset.clear();
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				dataset.addSeries(series);
//				System.out.println("dataset-length-->"+dataset.getSeriesCount());
//				chart.invalidate();
				
				madapter.notifyDataSetChanged();//通知更新list
				break;

			default:
				break;
			}
		}
	};
	
//	private void updatechart(){
//		dataset.removeSeries(series);
//		series.clear();
//	}
	private HeartrateDB hd;
	private void readdb(){
		
		int i = 0,k= 0;
		hd = new HeartrateDB(context);
		SQLiteDatabase dbread = hd.getReadableDatabase();
        Cursor c = dbread.query("myheartrate", null, null, null, null, null, null);
        while (c.moveToNext()) {
        	k++;			
		}
        rate = new int[k];
        time = new String[k];
        SQLiteDatabase dbread2 = hd.getReadableDatabase();
        Cursor c2 = dbread2.query("myheartrate", null, null, null, null, null, null);
        while (c2.moveToNext()) {
        	int id = c2.getInt(c2.getColumnIndex("id"));
			int heartrate = c2.getInt(c.getColumnIndex("heartrate"));
			String mtime = c2.getString(c.getColumnIndex("time"));
			System.out.println(mtime);
			rate[i] = heartrate;
			time[i] = mtime;
			i++;
			System.out.println(String.format("id=%d,height=%d",id,heartrate));
			
		}
	}
	
	private void givedata() {
		int vl = rate.length;
		listdata = new ListData[vl];
		for (int i = 0; i < vl; i++) {
			listdata[i] = new ListData(R.drawable.aa1_n, rate[vl-i-1],time[vl-i-1]);
		}
		mlist = (SwipeMenuListView) view.findViewById(R.id.list);
		mlist.setAdapter(madapter);
	}

	private void delete(ListData item){//删除数据
		hd = new HeartrateDB(context);
		SQLiteDatabase dbdelete = hd.getWritableDatabase();
		dbdelete.delete("myheartrate", "time=?", new String[]{item.time});
	}
	
	private String[] titles = new String[] { "" };              //titles
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer renderer;
	private XYSeries series = new XYSeries(titles[0]); // 根据每条线的名称创建(此处只有一条线)
	private void setchartdata() {
		
		List<double[]> x = new ArrayList<double[]>();       //x
		double[] ex = new double[rate.length];
		for (int i = 0; i < ex.length; i++){
			ex[i]= i+1; 
		}
		x.add(ex);
		List<double[]> values = new ArrayList<double[]>();  //values
		double[] data = new double[rate.length]; 
		for (int i = 0; i < data.length; i++) {
			data[i] = (double)rate[i];
		}
		values.add(data);
		dataset.removeSeries(series);
		series.clear();
		System.out.println("series-length-->"+series.getItemCount());
		System.out.println("dataset-length-->"+dataset.getSeriesCount());
		dataset = buildDatset(titles,x,values);
		
		
	}
	private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
			List<double[]> yValues) {
		// 用于数据的存放
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // 有几条线
		for (int i = 0; i < length; i++) {
			//XYseries对象,用于提供绘制的点集合的数据
			double[] xV = (double[]) xValues.get(i); // 获取第i条线的数据
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length; // 有几个点

			for (int k = 0; k < seriesLength; k++) // 每条线里有几个点
			{
				series.add(xV[k], yV[k]);
			}

			dataset.addSeries(series);
		}

		return dataset;
	}
	
	private XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill) {
		//曲线图的格式，包括颜色，值的范围，点和线的形状等等 
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(); 
        int length = colors.length; 
        for (int i = 0; i < length; i++) 
        { 
            XYSeriesRenderer r = new XYSeriesRenderer(); 
            r.setColor(colors[i]); 
            r.setPointStyle(styles[i]); 
            r.setFillPoints(fill); 
            renderer.addSeriesRenderer(r); 
        } 
        return renderer; 
	}
	
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
		      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
		      int labelsColor) {
		    renderer.setChartTitle(title);
		    renderer.setXTitle(xTitle);
		    renderer.setYTitle(yTitle);
		    renderer.setXAxisMin(xMin);
		    renderer.setXAxisMax(xMax);
		    renderer.setYAxisMin(yMin);
		    renderer.setYAxisMax(yMax);
		    renderer.setAxesColor(axesColor);
		    renderer.setLabelsColor(labelsColor);
		    renderer.setXLabels(12);//X轴的近似坐标数  
			renderer.setYLabels(10);//Y轴的近似坐标数 
			renderer.setShowGrid(true);// 是否显示网格
			renderer.setXLabelsAlign(Align.LEFT);//刻度线与X轴坐标文字左侧对齐 
			renderer.setYLabelsAlign(Align.LEFT);//Y轴与Y轴坐标文字左对齐 
			renderer.setPanEnabled(true, false); //允许左右拖动,但不允许上下拖动.
			renderer.setZoomEnabled(false);
			renderer.setZoomButtonsVisible(false);// 设置缩放按钮是显示状态
			// 整个图表属性设置
	        // -->start
	        renderer.setAxisTitleTextSize(16);// 设置轴标题文字的大小
	        renderer.setApplyBackgroundColor(true);//应用背景颜色
	        renderer.setBackgroundColor(getResources().getColor(R.color.lightcoral));//设置背景颜色
	        renderer.setChartTitleTextSize(25);// 设置整个图表标题文字的大小
	        renderer.setLabelsTextSize(15);// 设置轴刻度文字的大小
	        renderer.setLegendTextSize(15);// 设置图例文字大小
	        renderer.setPointSize(5f);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
	        renderer.setMargins(new int[] { 30, 35, 0, 10 });// 设置图表的外边框(上/左/下/右)
	        // -->end
		    renderer.setMarginsColor(getResources().getColor(R.color.lightcoral));//设置图表四边颜色
	}
	
	
}
