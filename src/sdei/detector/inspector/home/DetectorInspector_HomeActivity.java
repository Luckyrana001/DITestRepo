package sdei.detector.inspector.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.List;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.detector.inspector.lib.model.Inspection;

public class DetectorInspector_HomeActivity extends sdei.detector.inspector.BaseActivity {

	public static final String TAG    = DetectorInspector_HomeActivity.class.getSimpleName();
	public static final String ACTION = Const.ACTIVITY_NAME.HOME_ACTIVITY;

	private static final int ICON_POSITION_LIST     = 0;
	private static final int ICON_POSITION_CALENDAR = 1;

	private static final int ICON_POSITION_SYNC     = 2;

	private static final int ICON_POSITION_FEEDBACK = 3;
	// private static final int ICON_POSITION_SETTINGS = 5;
	private static final int ICON_POSITION_SIGNOUT  = 4;
	List<Inspection> mList;

	private GridView mHomeGrid;
	private HomeGridAdapter mHomeGridAdpater;
	private Calendar mCalendarNow;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String mDate;
	private UserProfile mProfile;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);
		mCalendarNow = Calendar.getInstance();
		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		mProfile.setLoginSucess(false);
		setContentView(R.layout.activity_home); 
	}

	@Override
	public void getView() {
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.app_name));
		findViewById(R.id.home_btn).setVisibility(View.GONE);
		
		findViewById(R.id.sign_out_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Utils.GoToLoginScreen(DetectorInspector_HomeActivity.this);
					}
				});
		
//		int x  = 0;
//		int y = 5/x;
		mHomeGrid = (GridView) findViewById(R.id.gridview);
		mHomeGridAdpater = new HomeGridAdapter(this);

		mHomeGrid.setAdapter(mHomeGridAdpater);

		mHomeGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				switch (position) {
				case ICON_POSITION_LIST:
					Intent history_intent = new Intent();
					history_intent.setAction(DetectorInspector_History.ACTION);
					history_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(history_intent);
					break;
				case ICON_POSITION_CALENDAR:
					Utils.GoToBookingActivity(DetectorInspector_HomeActivity.this);
					break;

				case ICON_POSITION_SYNC:
					Intent sync_intent = new Intent();
					sync_intent	.setAction(DetectorInspector_SynchActivity.ACTION);
					sync_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(sync_intent);
					break;

				case ICON_POSITION_FEEDBACK:
					Intent contact_intent = new Intent();
					contact_intent.setAction(DetectorInspectorContactUs.ACTION);
					contact_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(contact_intent);
					break;

				case ICON_POSITION_SIGNOUT:
					mProfile.getSettings().date = "date";
					mProfile.getSettings().use_pre_text = false;
					Utils.savePreference(DetectorInspector_HomeActivity.this,mProfile.getSettings());
					mProfile.setLoginSucess(true);
					mProfile.setCancelTimer(DetectorInspector_LoginActivity.alaram);
					Utils.GoToLoginScreen(DetectorInspector_HomeActivity.this);
					break;
				}
			}
		});
	}

//	protected void fetchDataBase() {
//		// TODO Auto-generated method stub
//		try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();
//
//            if (sd.canWrite()) {
//                String currentDBPath = "/data/" + getPackageName() + "/databases/detectorinspector.db";
//                String backupDBPath = "detectorinspector.db";
//                File currentDB = new File(currentDBPath);
//                File backupDB = new File(sd, backupDBPath);
//                
//                System.out.println("CHECK");
//                if (currentDB.exists()) {
//                	System.out.println("DATABASE EXISTS");
//                    FileChannel src = new FileInputStream(currentDB).getChannel();
//                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                    dst.transferFrom(src, 0, src.size());
//                    src.close();
//                    dst.close();
//                }
//            }
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

	class HomeGridAdapter extends BaseAdapter {
		private Context mContext;

		public HomeGridAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) mContext	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.activity_home_item, null);

			ImageView iv = (ImageView) v.findViewById(R.id.home_icon);
			iv.setImageResource(mThumbIds[position]);

			TextView tv = (TextView) v.findViewById(R.id.home_icon_desp);
			tv.setText(mContext.getResources().getString(mStringIds[position]));

			return v;
		}

		// references to our images
		private Integer[] mThumbIds = { 
				R.drawable.im_menu_icon_inspectionlist,
				R.drawable.im_menu_icon_calendar, 
				R.drawable.im_menu_icon_sync,
				R.drawable.im_menu_icon_contact,
				R.drawable.im_menu_icon_signout };

		// references to our images
		private Integer[] mStringIds = { 
				R.string.ic_insp_list_desp,
				R.string.ic_calendar_desp, 
				R.string.ic_sync_desp,
				R.string.ic_feedback_desp, 
				R.string.sign_out };
	}
}