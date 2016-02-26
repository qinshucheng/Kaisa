package com.qsc.mydatafragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qsc.barchartfragment.BarchartFragment;
import com.qsc.linechartfragment.LinechartFragment;
import com.qsc.main.R;
 
public class mydataFragment extends Fragment {
 
    ViewPager pager = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    public String TAG = "tag";
    
//    private ImageView[] dots;
//    private int[] ids ={R.id.iv1,R.id.iv2,R.id.iv3,R.id.iv4};
    private ImageView iv1,iv2;
    private LinearLayout lay1,lay2;
    
    private LinechartFragment linechartFragment;
    private BarchartFragment barchartFragment;
	private FragmentManager linechartManager;
	private FragmentManager barchartManager;
 
    @SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mydatafragment, container, false);
//        initDots();//瀹炰緥鍖朓mageView
        
        iv1 = (ImageView) view.findViewById(R.id.iv1);
        iv2 = (ImageView) view.findViewById(R.id.iv2);
        
        lay1 = (LinearLayout) view.findViewById(R.id.lay1);
        lay2 = (LinearLayout) view.findViewById(R.id.lay2);
        
        lay1.setOnClickListener(new MyOnClickListener(0));
		lay2.setOnClickListener(new MyOnClickListener(1));
        
        pager = (ViewPager) view.findViewById(R.id.viewpager);
      //viewpager寮�濮嬫坊鍔爒iew
        viewContainter.add(LayoutInflater.from(getActivity()).inflate(R.layout.tab1, null));
        viewContainter.add(LayoutInflater.from(getActivity()).inflate(R.layout.tab2, null));
        
        pager.setAdapter(new PagerAdapter() {
 
            //viewpager涓殑缁勪欢鏁伴�??
            @Override
            public int getCount() {
                return viewContainter.size();
            }
          //婊戝姩鍒囨崲鐨勬椂鍊欓攢姣佸綋鍓嶇殑缁勪�??
            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }
          //姣忔婊戝姩鐨勬椂鍊欑敓鎴愮殑缁勪欢
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }
 
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
 
            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        });
        
        pager.setCurrentItem(0);
        System.out.println("index-->"+pager.getCurrentItem());
        /**
    	 * 椤靛崱鍒囨崲鐩戝�??
    	 */
 
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            

			@Override
            public void onPageScrollStateChanged(int arg0) {
                Log.d(TAG, "--------changed:" + arg0);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.d(TAG, "-------scrolled arg0:" + arg0);
                Log.d(TAG, "-------scrolled arg1:" + arg1);
                Log.d(TAG, "-------scrolled arg2:" + arg2);
            }
 
            @Override
            public void onPageSelected(int arg0) {
                Log.d(TAG, "------selected:" + arg0);
                switch (arg0) {
				case 0:
					iv1.setImageResource(R.drawable.ss1);
					iv2.setImageResource(R.drawable.tt2);
					System.out.println("index-->"+pager.getCurrentItem());
					
					linechartFragment = new LinechartFragment();
					linechartManager = getFragmentManager();
					
					linechartManager.beginTransaction()
							//.addToBackStack(null) 
							.replace(R.id.tab1_fragment, linechartFragment)
							.commit();
					break;
				case 1:
					iv1.setImageResource(R.drawable.ss2);
					iv2.setImageResource(R.drawable.tt1);
					System.out.println("index-->"+pager.getCurrentItem());
					
					barchartFragment = new BarchartFragment();
					barchartManager = getFragmentManager();
					
					barchartManager.beginTransaction()
							.replace(R.id.tab2_fragment, barchartFragment)
							.commit();
					break;
				default:
					break;
				}
            }
        });
        return view;
         
    }
    
    /**
	 * 椤靛崱鐐瑰嚮鐩戝�??
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
			
		}
	};
 
}
