package com.login.operation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.login.network.ConnNet;

public class Operaton 
{

	public String login(String url,String username,String password) //登陆     url=login
	{    
		String result = null;     //result是登陆成功与否的提示
		ConnNet connNet=new ConnNet();
		List<NameValuePair> params=new ArrayList<NameValuePair>();//TODO NameValuePair（键值对）用法?
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		try {
			HttpEntity entity=new UrlEncodedFormEntity(params, HTTP.UTF_8);//将List对象params转换为entity
			HttpPost httpPost=connNet.gethttPost(url);
			System.out.println(httpPost.toString());
			//应该输出System.out: http://10.131.141.214:8080/LoginRegister_ser/Login
			httpPost.setEntity(entity);//entity为要发送的内容
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			//请求HttpCLient，取得HttpResponse，excute返回值为httpResponse对象
			if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) //如果请求成功
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	//将服务器的返回值赋给result		
			}
			else
			{
				result="请求失败";
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}
	public String checkusername(String url,String username)  //检查用户名是否已经存在
	{
		String result=null;
		ConnNet connNet=new ConnNet();
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		try {
			HttpEntity entity=new UrlEncodedFormEntity(params, HTTP.UTF_8);
			HttpPost httpPost=connNet.gethttPost(url);
			System.out.println(httpPost.toString());
			httpPost.setEntity(entity);
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) 
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	
				System.out.println("resu"+result);
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;  
	}

	public String UpData(String uripath,String jsonString)  //注册
	{ 
		String result = null;
		List<NameValuePair> list=new ArrayList<NameValuePair>();
//		NameValuePair nvp=new BasicNameValuePair("jsonstring", jsonString);
//		list.add(nvp);
		list.add(new BasicNameValuePair("jsonstring", jsonString));
		ConnNet connNet=new ConnNet();
		HttpPost httpPost=connNet.gethttPost(uripath);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
			//此句必须加上否则传到客户端的中文将是乱码
			httpPost.setEntity(entity);
			HttpClient client=new DefaultHttpClient();
			HttpResponse httpResponse=client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode()==200) //TODO
			{
				result=EntityUtils.toString(httpResponse.getEntity(), "utf-8");	
				System.out.println("resu"+result);
			}
			else {
				result="注册失败";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {			
			e.printStackTrace();
		} catch (ParseException e) {		
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return result;  
	}
	
	
	public String uploadFile(File file,String urlString) //传输文件
	{
		final String TAG = "uploadFile";
		final int TIME_OUT = 10*1000;   //超时时间
		final String CHARSET = "utf-8"; //设置编码
		String result = null;
		String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		String PREFIX = "--" , LINE_END = "\r\n"; 
		String CONTENT_TYPE = "multipart/form-data";   //内容类型

		try {
			ConnNet connNet=new ConnNet();
		    HttpURLConnection conn	=connNet.getConn(urlString);
			conn.setReadTimeout(TIME_OUT);
//			conn.setConnectTimeout(TIME_OUT);
//			conn.setDoInput(true);  //允许输入流
//			conn.setDoOutput(true); //允许输出流
//			conn.setUseCaches(false);  //不允许使用缓存
//			conn.setRequestMethod("POST");  //请求方式
			conn.setRequestProperty("Charset", CHARSET);  //设置编码
			conn.setRequestProperty("connection", "keep-alive");   
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY); 

			if(file!=null)
			{
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意：
				 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的   比如:abc.png  
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END); 
				sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while((len=is.read(bytes))!=-1)
				{
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码  200=成功
				 * 当响应成功，获取响应的流  
				 */
				int res = conn.getResponseCode();  
				Log.e(TAG, "response code:"+res);
				//                if(res==200)
				//                {
				Log.e(TAG, "request success");
				InputStream input =  conn.getInputStream();
				StringBuffer sb1= new StringBuffer();
				int ss ;
				while((ss=input.read())!=-1)
				{
					sb1.append((char)ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : "+ result);
				//                }
				//                else{
				//                    Log.e(TAG, "request error");
				//                }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
 
}

	
	
	