package ru.denis.test.apps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity  implements View.OnClickListener, View.OnLongClickListener{

	CanvasView CvsView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		CvsView = (CanvasView) findViewById(R.id.CanvasView);

		Bitmap bitmap = null;
		try (FileInputStream in = new FileInputStream(new File(getFilesDir(),"note.png"))) {
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
		btnDraw.setOnClickListener(this);
		btnErase.setOnClickListener(this);
		btnDraw.setOnLongClickListener(this);
		btnErase.setOnLongClickListener(this);
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
		try (FileOutputStream out = new FileOutputStream(new File(getFilesDir(),"note.png"))) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.onStop();
	}
}
