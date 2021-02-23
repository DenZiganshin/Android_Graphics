package ru.denis.test.apps;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ToolSettingsDialog extends Dialog implements View.OnClickListener {

	Activity m_activity;
	Button m_btnOk, m_btnCancel;
	EditText m_teSize, m_teColor;
	int m_size, m_color;
	Painter m_painter;

	public ToolSettingsDialog(Activity activity){
		super(activity);
		m_activity = activity;
	}

	public void init(Painter p, int color, int size){
		m_size = size;
		m_color = color;
		m_painter = p;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.toolsettingsdialog);
		m_btnOk = (Button) findViewById(R.id.btn_ok);
		m_btnCancel = (Button) findViewById(R.id.btn_cancel);
		m_teSize = (EditText) findViewById(R.id.dlgTeSize);
		m_teColor = (EditText) findViewById(R.id.dlgTeColor);

		m_teColor.setText("#" + Integer.toHexString(m_color));
		m_teSize.setText(Integer.toString(m_size));

		m_btnOk.setOnClickListener(this);
		m_btnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btn_ok){
			//save changes
			if(m_painter != null){
				m_color = Color.parseColor(m_teColor.getText().toString());
				m_size = Integer.parseInt(m_teSize.getText().toString());
				m_painter.changeCurrentTool(m_color, m_size);
			}
		}
		dismiss();
	}
}
