package ru.denis.test.apps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;

public class GraphicsEditor extends AppCompatActivity {

	CanvasView m_CanvasView;
	private static final int CREATE_FILE = 1;
	private static final int MENU_OPT_SAVE = 1;
	private static final int MENU_OPT_EXIT = 2;
	private static final int MENU_OPT_PEN_SIZE = 3;
	private static final int MENU_OPT_CHANGE_TOOL = 4;
	Bitmap m_bitmap;
	String m_path;

	ToolSettingsDialog m_ToolSettingsDialog;


	public void SetBitmapAndPath(Bitmap bitmap, String path){
		m_bitmap = bitmap;
		m_path = path;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		m_CanvasView = (CanvasView) findViewById(R.id.CanvasView);
		/* Open file by name from Intent. Make bitmap  */
		/*
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String bitmap_path = extras.getString(PagerFragment.Intent_Bitmap_Path_key);
			File bitmap_file = new File(bitmap_path);
			if(bitmap_file.exists()){
				try (FileInputStream in = new FileInputStream(bitmap_file)) {
					Bitmap tmp = BitmapFactory.decodeStream(in);
					m_bitmap = tmp.copy(Bitmap.Config.ARGB_8888, true);
					m_path = bitmap_path;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			// just close?
		}
		*/

		/* get screen size */
		WindowManager manager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		/* create empty bitmap */
		m_bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
		m_bitmap.eraseColor(Color.TRANSPARENT);


		if(m_bitmap != null) {
			m_CanvasView.init(m_bitmap);
		}

		//callback
		/*
		ImageButton btnDraw = (ImageButton) findViewById(R.id.btnDraw);
		ImageButton btnErase = (ImageButton) findViewById(R.id.btnErase);
		ImageButton btnMenu = (ImageButton) findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		btnDraw.setOnClickListener(this);
		btnErase.setOnClickListener(this);
		btnDraw.setOnLongClickListener(this);
		btnErase.setOnLongClickListener(this);
		 */

		m_ToolSettingsDialog = new ToolSettingsDialog(this);
		m_ToolSettingsDialog.init(m_CanvasView.getPainter(), 0x000000, 1);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_OPT_SAVE, 0, "Save");
		menu.add(0, MENU_OPT_EXIT, 1, "Exit");
		menu.add(0, MENU_OPT_PEN_SIZE, 2, "Tool param");
		menu.add(0, MENU_OPT_CHANGE_TOOL, 3, "Swap tool");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_OPT_SAVE:
				/*
				Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/png");
				intent.putExtra(Intent.EXTRA_TITLE, "note.png");
				startActivityForResult(intent, CREATE_FILE);
				 */
				SaveBitmap();
				return true;
			case MENU_OPT_PEN_SIZE:
				m_ToolSettingsDialog.show();
				return true;
			case MENU_OPT_CHANGE_TOOL:
				m_CanvasView.getPainter().swapTool();
				return true;

			case MENU_OPT_EXIT:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
								 Intent resultData) {
		if (requestCode == CREATE_FILE
				&& resultCode == Activity.RESULT_OK) {
			// The result data contains a URI for the document or directory that
			// the user selected.
			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();
				// Perform operations on the document using its URI.
				if(uri != null)
				{
					Bitmap bitmap = m_CanvasView.getBitmap();
					try{
						OutputStream out = getContentResolver().openOutputStream(uri);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	/*
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
	 */

	@Override
	protected void onStop() {
		SaveBitmap();
		super.onStop();
	}


	private void SaveBitmap(){
		if(m_CanvasView == null){
			return;
		}

		Bitmap bitmap = m_CanvasView.getBitmap();

		if(m_path != null) {
			File bitmap_file = new File(m_path);
			//save changes to file
			try (FileOutputStream out = new FileOutputStream(bitmap_file)) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
