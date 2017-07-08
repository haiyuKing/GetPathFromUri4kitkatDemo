package com.why.project.getpathfromuri4kitkatdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.why.project.getpathfromuri4kitkatdemo.utils.GetPathFromUri4kitkat;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	private Button btn_openFile;
	private TextView tv_filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		initEvents();

	}

	private void initViews() {
		btn_openFile = (Button) findViewById(R.id.btn_openFile);
		tv_filePath = (TextView) findViewById(R.id.tv_filePath);
	}

	private void initEvents() {

		btn_openFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				String IMAGE_UNSPECIFIED = "*/*";
				innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
				Intent wrapperIntent = Intent.createChooser(innerIntent, "File Browser");
				MainActivity.this.startActivityForResult(wrapperIntent, 1111);
			}
		});
	}

	/*=========================================实现打开文件管理器功能==============================================*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.w(TAG, "{onActivityResult}resultCode=" + resultCode);
		Log.w(TAG, "{onActivityResult}requestCode=" + requestCode);
		if (resultCode == Activity.RESULT_OK) {
			//调用文件管理器选择文件的回调
			if (requestCode == 1111) {
				Uri result = data == null ? null : data.getData();
				Log.w(TAG, "{onActivityResult}result=" + result);

				tv_filePath.setText("Uri："+result.toString());
				String pathStr = GetPathFromUri4kitkat.getPath(MainActivity.this,result);
				Log.w(TAG, "{onActivityResult}pathStr=" + pathStr);
				tv_filePath.setText(tv_filePath.getText() + "\n\n" + "Path："+pathStr);
			}
		}
	}
}
