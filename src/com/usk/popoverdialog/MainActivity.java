package com.usk.popoverdialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{

		USKPopoverDialog dlg ;
		EditText et ;
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			et = new EditText(getActivity());
			et.setText("Hurix Systems Pvt Ltd");
			et.setTextSize(20);
			et.setTextColor(Color.WHITE);
			et.setPadding(5, 5, 5, 5);
			et.setBackgroundColor(Color.BLACK);
			et.setFocusableInTouchMode(true);
			if(dlg == null)
			{
				dlg = new USKPopoverDialog(getActivity());
				dlg.setAlertLayout(et);
			}
			rootView.findViewById(R.id.button1).setOnClickListener(this);
			rootView.findViewById(R.id.button2).setOnClickListener(this);
			rootView.findViewById(R.id.button3).setOnClickListener(this);
			rootView.findViewById(R.id.button4).setOnClickListener(this);
			rootView.findViewById(R.id.button5).setOnClickListener(this);
			return rootView;
		}
		

		@Override
		public void onClick(View v) 
		{
			if(dlg.isShowing())
			{
				dlg.dismiss();
			}
			else
			{
				try 
				{
					dlg.show(v);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			
		}
	}

}
