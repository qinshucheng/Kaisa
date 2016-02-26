package com.qsc.barchartfragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsc.linechartfragment.ListData;
import com.qsc.main.R;
import com.qsc.swipelist.SwipeMenu;
import com.qsc.swipelist.SwipeMenuCreator;
import com.qsc.swipelist.SwipeMenuItem;
import com.qsc.swipelist.SwipeMenuListView;
import com.qsc.swipelist.SwipeMenuListView.OnMenuItemClickListener;
import com.qsc.swipelist.SwipeMenuListView.OnSwipeListener;

public class BarchartFragment extends Fragment {
	
	private View view;
	private GraphicalView chart;
	private Context context;
	
	private SwipeMenuListView mlist;
	private ListData[] listdata;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_mydata, container, false);
		context = view.getContext();
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
		
		readdb();
		givedata();
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
					madapter.notifyDataSetChanged();//通知更新list
					break;
				case 1:
					break;
				}
				return false;
			}
		});
		
//		writedb();
		
		for (int i = 0; i < rate.length; i++) {
			System.out.println(rate[i]);
		}
		
		String[] titles = new String[] { "" };
		List<double[]> values = new ArrayList<double[]>();
//		values.add(new double[] { 14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500,
//		    12600, 14000 });
//		values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500,
//		    11600, 13500 });
		 	
		double[] data = new double[rate.length]; 
		for (int i = 0; i < data.length; i++) {
			data[i] = (double)rate[i];
		}
		values.add(data);
		 
		int[] colors = new int[] { R.color.anqing};
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "My HeartRate", "", "次/分", 0.5,
		    12.5, 0, 100, Color.GRAY, Color.LTGRAY);
		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);//设置柱子上是否显示数量值
//		renderer.getSeriesRendererAt(1).setDisplayChartValues(true);//设置柱子上是否显示数量值
		renderer.setXLabels(12);//X轴的近似坐标数  
		renderer.setYLabels(10);//Y轴的近似坐标数 
		renderer.setXLabelsAlign(Align.LEFT);//刻度线与X轴坐标文字左侧对齐 
		renderer.setYLabelsAlign(Align.LEFT);//Y轴与Y轴坐标文字左对齐 
		renderer.setPanEnabled(true, false); //允许左右拖动,但不允许上下拖动.
		renderer.setZoomEnabled(false);
		renderer.setZoomRate(1.1f);//放大的倍率  
		renderer.setBarSpacing(0.5f);//柱子间宽度  
		chart = ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer,Type.DEFAULT);
		layout.addView(chart);
		    	
		return view;
	}
	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    int length = colors.length;
	    for (int i = 0; i < length; i++) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(colors[i]);
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
		    
		    renderer.setMarginsColor(getResources().getColor(R.color.lightcoral));//设置图表四边颜色
	}
	
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      CategorySeries series = new CategorySeries(titles[i]);
	      double [] v = values.get(i);
	      int seriesLength = v.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(v[k]);
	      }
	      dataset.addSeries(series.toXYSeries());
	    }
	    return dataset;
	}
	
//	private void writedb(){
//		hd = new HeartrateDB(context);
//		SQLiteDatabase dbwrite = hd.getWritableDatabase();
//        ContentValues cValues = new ContentValues();
//        
//        int heartrate =getArguments().getInt("key");
//        cValues.put("heartrate", heartrate);
//        dbwrite.insert("myheartrate", null, cValues);
//	}
	private int [] rate;
	private String[] time;
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
			rate[i] = heartrate;
			time[i] = mtime;
			i++;
			System.out.println(String.format("id=%d,height=%d",id,heartrate));
			
		}
	}
	private HeartrateDB hd;
	private void givedata() {
		int vl = rate.length;
		listdata = new ListData[vl];
		for (int i = 0; i < vl; i++) {
			listdata[i] = new ListData(R.drawable.aa2_n, rate[vl-i-1],time[vl-i-1]);
		}
		mlist = (SwipeMenuListView) view.findViewById(R.id.list);
		mlist.setAdapter(madapter);
	}

	private void delete(ListData item){//删除数据
		hd = new HeartrateDB(context);
		SQLiteDatabase dbdelete = hd.getWritableDatabase();
		dbdelete.delete("myheartrate", "time=?", new String[]{item.time});
	}
	

}
