package ru.denis.test.apps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.documentfile.provider.DocumentFile;

import java.io.*;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity  implements View.OnClickListener, View.OnLongClickListener{

	CanvasView CvsView;
	final String SPNAME = "AppConfig";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		CvsView = (CanvasView) findViewById(R.id.CanvasView);

		SharedPreferences prefs = getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		String name = prefs.getString("LastName", "note");

		Bitmap bitmap = null;
		//File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
		try (FileInputStream in = new FileInputStream(new File(getFilesDir(),name+".png"))) {
			Bitmap tmp = BitmapFactory.decodeStream(in);
			bitmap = tmp.copy(Bitmap.Config.RGBA_F16, true);
		}catch (IOException e){
			e.printStackTrace();
		}
		if(bitmap != null) {
			CvsView.init(bitmap);
		}else
		{
			CvsView.init();
		}

		//callback
		ImageButton btnDraw = (ImageButton) findViewById(R.id.btnDraw);
		ImageButton btnErase = (ImageButton) findViewById(R.id.btnErase);
		ImageButton btnMenu = (ImageButton) findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		btnDraw.setOnClickListener(this);
		btnErase.setOnClickListener(this);
		btnDraw.setOnLongClickListener(this);
		btnErase.setOnLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_new:
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
				startActivityForResult(intent, 42);

				return true;
			case R.id.menu_quit:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (resultCode == RESULT_OK) {

			Uri treeUri = resultData.getData();
			DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

			// List all existing files inside picked directory
			for (DocumentFile file : pickedDir.listFiles()) {
				Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
			}


			// Create a new file and write into it
			Bitmap bitmap = CvsView.getBitmap();
			try{
				DocumentFile newFile = pickedDir.createFile("image/png", "note.png");
				OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id){
			case R.id.btnDraw:
				CvsView.changeTool(CanvasView.TOOL_DRAW);
				break;
			case R.id.btnErase:
				CvsView.changeTool(CanvasView.TOOL_ERASE);
				break;
			case R.id.btnMenu:
				openOptionsMenu();
				break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		int id = v.getId();
		Painter p = CvsView.getPainter();
		if(p == null){
			return false;
		}
		switch (id){
			case R.id.btnDraw:
				p.changeTool(Painter.TOOL_DRAW);
				break;
			case R.id.btnErase:
				p.changeTool(Painter.TOOL_ERASE);
				break;
		}
		int size = p.getCurrentSize();
		int color = p.getCurrentColor();
		ToolSettingsDialog dlg = new ToolSettingsDialog(MainActivity.this);
		dlg.init(p,color,size);
		dlg.show();
		return true;
	}

	@Override
	protected void onStop() {
		Bitmap bitmap = CvsView.getBitmap();

		SharedPreferences prefs = getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		String name = prefs.getString("LastName", "note");

		//File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
		try (FileOutputStream out = new FileOutputStream(new File(getFilesDir(),name+".png"))) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.onStop();
	}
}
