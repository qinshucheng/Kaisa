package com.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.login.operation.Operaton;
import com.qsc.main.R;

public class LoginActivity extends Activity {
	
	
	 private Button butt1;
	 TextView butt2,butt3;
	 private EditText edit1,edit2;
	 private String username, password;
	 private ProgressDialog p;
	 
	 private SharedPreferences loginpre;
	 private boolean isnetwork = false;
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isfirstlogin();
		setContentView(R.layout.activity_login);
		init();
		initListener();
	}
	
	private boolean checknetwork(){
		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting(); 
		isnetwork = wifi||internet;
		return isnetwork;
	}
	
	private void isfirstlogin(){
		loginpre = PreferenceManager.getDefaultSharedPreferences(this);
		//读取first_time_login，若不存在，将true赋予firstTime
		boolean firstTime = loginpre.getBoolean("first_time_login", true);
		if (firstTime) {
			//第一次登陆
			System.out.println("firstTime?-->"+firstTime);
//			Editor preEditor = loginpre.edit();
//			//写入first_time_login，将false赋予first_time_login
//			preEditor.putBoolean("first_time_login", false);
//			preEditor.commit();
			
		}else {
			//不是第一次登陆
			System.out.println("firstTime?-->"+firstTime);
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, com.qsc.main.MainActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
	
	/*
	 * login页锟斤拷锟斤拷锟斤拷锟绞硷拷锟斤拷锟斤拷锟饺ogin页锟斤拷锟接︼拷锟皆拷锟�
	 */
	private void init() {
		edit1 = (EditText) this.findViewById(R.id.edittext1);
		edit2 = (EditText) this.findViewById(R.id.edittext2);
		butt1 = (Button) this.findViewById(R.id.button1);
		butt2 = (TextView) this.findViewById(R.id.button2);
		butt3 = (TextView) this.findViewById(R.id.button3);
		p=new ProgressDialog(LoginActivity.this);
		p.setTitle("上传数据中");
		p.setMessage("请稍等...");
		p.setCancelable(false);
	}
	/*
	 * 锟斤拷login锟较碉拷页锟斤拷元锟斤拷锟斤拷锟矫硷拷锟斤拷
	 */
	private void initListener() {
		this.butt1.setOnClickListener(new LoginListener());
		this.butt3.setOnClickListener(new ButtonRegister());
		this.butt2.setOnClickListener(new CancelButton());
	}
	
	private class LoginListener implements OnClickListener{
		

		@Override
		public void onClick(View arg0) {
			username=edit1.getText().toString().trim(); //trim() 函数的功能是去掉首尾空格
			if (username==null||username.length()<=0) 
			{		
				edit1.requestFocus();
				edit1.setError("用户名不能为空！");//TODO setError?
				return;
			}
			else 
			{
				username=edit1.getText().toString().trim();
			}
			password=edit2.getText().toString().trim();
			if (password==null||password.length()<=0) 
			{		
				edit2.requestFocus();
				edit2.setError("密码不能为空！");//TODO setError?
				return;
			}
			else 
			{
				password=edit2.getText().toString().trim();
			}
			checknetwork();
			if(isnetwork){  
			    //执行相关操作  
				p.show();
				new Thread(new Runnable() {					//TODO thread?

					public void run() {
						Operaton operaton=new Operaton();
						String result=operaton.login("Login", username, password);	
						System.out.println("登陆返回的result1:"+result);
						Message msg=new Message();
						msg.obj=result;
						handler.sendMessage(msg);
					}
				}).start();
				
			}else{  
			    Toast.makeText(LoginActivity.this,"亲，网络连了么？", Toast.LENGTH_LONG).show();  
			}
			
		}
	}
		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String string=(String) msg.obj;
				System.out.println("登陆返回的result2:"+string);
				
				if (string==null||string.length()<=0) {
					p.dismiss();
					Toast.makeText(LoginActivity.this, "登陆超时", Toast.LENGTH_SHORT).show();
					return;
				}
				p.dismiss();
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
				
				if(string.equals("登录成功"))
				{
					Editor editor = loginpre.edit();
					editor.putBoolean("first_time_login", false);
					editor.commit();
					
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, com.qsc.main.MainActivity.class);
					startActivity(intent);
					finish();
				}
				
				super.handleMessage(msg);
			}	
		};
		
		

	
	
	
	private class ButtonRegister implements OnClickListener{
			

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
			
			
	 }
	 private class CancelButton implements OnClickListener{
			

			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
				
			}
	}
}



