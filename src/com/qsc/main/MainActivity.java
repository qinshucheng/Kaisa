/*
 * Copyright (C) 2014 Chris Renke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qsc.main;

import static android.view.Gravity.START;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qsc.barchartfragment.BarchartFragment;
import com.qsc.heartratefragment.HeartRateFragment;
import com.qsc.linechartfragment.LinechartFragment;
import com.qsc.mydatafragment.mydataFragment;

public class MainActivity extends Activity {

  private DrawerArrowDrawable drawerArrowDrawable;
  private float offset;
  private boolean flipped;
  private RelativeLayout[] lay= new RelativeLayout[5];
  private TextView tvout;
  private DrawerLayout drawer;
  private TextView title;

  @Override 
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home_view);
    
    if (savedInstanceState == null) {
		getFragmentManager().beginTransaction()
				.add(R.id.view_content, new HeartRateFragment())
				.commit();
		
	}
    
    title = (TextView) findViewById(R.id.title);
    title.setText(R.string.title_rate);
    drawer = (DrawerLayout) findViewById(R.id.drawer);//抽屉layout
    final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);//箭头图标
    final Resources resources = getResources();

    drawerArrowDrawable = new DrawerArrowDrawable(resources);
    drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
    imageView.setImageDrawable(drawerArrowDrawable);

    drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        offset = slideOffset;

        // Sometimes slideOffset ends up so close to but not quite 1 or 0.
        if (slideOffset >= .995) {
          flipped = true;
          drawerArrowDrawable.setFlip(flipped);
        } else if (slideOffset <= .005) {
          flipped = false;
          drawerArrowDrawable.setFlip(flipped);
        }

        drawerArrowDrawable.setParameter(offset);
      }
    });

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (drawer.isDrawerVisible(START)) {
          drawer.closeDrawer(START);
        } else {
          drawer.openDrawer(START);
        }
      }
    });

//    final TextView styleButton = (TextView) findViewById(R.id.indicator_style);//箭头样式
//    styleButton.setOnClickListener(new View.OnClickListener() {
//      boolean rounded = false;
//
//      @Override public void onClick(View v) {
//        styleButton.setText(rounded //
//            ? resources.getString(R.string.rounded) //
//            : resources.getString(R.string.squared));
//
//        rounded = !rounded;
//
//        drawerArrowDrawable = new DrawerArrowDrawable(resources, rounded);
//        drawerArrowDrawable.setParameter(offset);
//        drawerArrowDrawable.setFlip(flipped);
//        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
//
//        imageView.setImageDrawable(drawerArrowDrawable);
//      }
//    });
    
    lay[0] = (RelativeLayout) findViewById(R.id.lay1);
	lay[1] = (RelativeLayout) findViewById(R.id.lay2);
	lay[2] = (RelativeLayout) findViewById(R.id.lay3);
	lay[3] = (RelativeLayout) findViewById(R.id.lay4);
	lay[3].setBackgroundColor(getResources().getColor(R.color.mred));
	lay[4] = (RelativeLayout) findViewById(R.id.lay5);
	
	tvout = (TextView) findViewById(R.id.tvout);
    layclick();
  }
  
  private int index = 0;
  private int[] titleid = new int[]{R.string.title_data,R.string.title_route,
		  							R.string.title_line,R.string.title_rate,R.string.title_bar};
  private void setlaybackground(int pos){
	  for (int i = 0; i < lay.length; i++) {
			if (i == pos) {
				lay[i].setBackgroundColor(getResources().getColor(R.color.mred));
			}else {
				lay[i].setBackgroundColor(getResources().getColor(R.color.lightyellow));
			}
		}
  }
  private void layclick(){
		
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v.equals(lay[0])) {
					Fragment viewpagerFragment = new mydataFragment();
					FragmentManager viewpagerManager = getFragmentManager();
					viewpagerManager.beginTransaction()
							.addToBackStack(null)  
							.replace(R.id.view_content, viewpagerFragment)
							.commit();
					index = 0;
					setlaybackground(index);
					title.setText(titleid[index]);
					drawer.closeDrawer(START);					
				}else if(v.equals(lay[1])){
//					Fragment secondFragment = new SecondFragment();
//					FragmentManager secondManager = getFragmentManager();
//					secondManager.beginTransaction()
//							.addToBackStack(null)  
//							.replace(R.id.view_content, secondFragment)
//							.commit();
					Intent intent=new Intent();
					intent.setClass(MainActivity.this, com.qsc.maplocationfragment.MaplocationActivity.class);
					startActivity(intent);
					finish();
					index = 1;
					setlaybackground(index);
					title.setText(titleid[index]);
					drawer.closeDrawer(START);
				}else if(v.equals(lay[2])){
					Fragment thirdFragment = new LinechartFragment();
					FragmentManager thirdManager = getFragmentManager();
					thirdManager.beginTransaction()
							.addToBackStack(null)   
							.replace(R.id.view_content, thirdFragment)
							.commit();
					index = 2;
					setlaybackground(index);
					title.setText(titleid[index]);
					drawer.closeDrawer(START);
				}else if(v.equals(lay[3])){
					Fragment heartrateFragment = new HeartRateFragment();
					FragmentManager heartManager = getFragmentManager();
					heartManager.beginTransaction()
							.addToBackStack(null)   
							.replace(R.id.view_content, heartrateFragment)
							.commit();
					index = 3;
					setlaybackground(index);
					title.setText(titleid[index]);
					drawer.closeDrawer(START);
				}else if(v.equals(lay[4])){
					Fragment mydataFragment = new BarchartFragment();
					FragmentManager dataManager = getFragmentManager();
					dataManager.beginTransaction()
							.addToBackStack(null)   
							.replace(R.id.view_content, mydataFragment)
							.commit();
					index = 4;
					setlaybackground(index);
					title.setText(titleid[index]);
					drawer.closeDrawer(START);
				}else if(v.equals(tvout)){
					finish();

				}
			}
		};
		lay[0].setOnClickListener(onClickListener);
		lay[1].setOnClickListener(onClickListener);
		lay[2].setOnClickListener(onClickListener);
		lay[3].setOnClickListener(onClickListener);
		lay[4].setOnClickListener(onClickListener);
		tvout.setOnClickListener(onClickListener);
	}
}
