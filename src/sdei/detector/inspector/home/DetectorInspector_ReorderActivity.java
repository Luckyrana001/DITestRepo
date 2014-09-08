package sdei.detector.inspector.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.DatabaseUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.widget.DragNDropListView;
import com.detector.inspector.lib.widget.DropListener;
import com.detector.inspector.lib.widget.RemoveListener;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class DetectorInspector_ReorderActivity extends sdei.detector.inspector.BaseActivity implements
		DropListener, RemoveListener {

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_ReorderActivity";

	UserProfile mProfile;
	List<Inspection> mInspectionsList;
	private ReorderAdapter mPropertyLayoutAdapter;
	private DragNDropListView mPropertyLayoutListView;
	private Button mHomeBtn;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);

		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		mInspectionsList = mProfile.getInspectionList();
		setContentView(R.layout.activity_sortorder);
	}

	@Override
	public void getView() {
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.reorder_list));
		mHomeBtn = (Button) findViewById(R.id.home_btn);
		mHomeBtn.setBackgroundResource(R.drawable.button_selector_save_btn);
		mHomeBtn.setText("Save");
		mPropertyLayoutListView = (DragNDropListView) findViewById(R.id.inspection_list_today);
		updatePropertyLayout(mInspectionsList);

	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			UpdateListData();
			break;

		default:
			break;
		}
	}

	private void UpdateListData() {
		showProgress();
		DatabaseUtil.updateDisplayRank(DetectorInspector_ReorderActivity.this,mInspectionsList, mProfile.getDate());

		mProfile.setInspectionList(mInspectionsList);
		hideProgress();
		Intent intent = new Intent();
		intent.setAction(DetectorInspector_BookingActivity.ACTION);
		setResult(Activity.RESULT_OK, intent);
		finish();

	}

	private void updatePropertyLayout(List<Inspection> list) {

		if (mPropertyLayoutAdapter != null) {
			updatePropertyAdapter(list);
		} else {
			mPropertyLayoutAdapter = createAdapter(list);
			mPropertyLayoutAdapter.setViewBinder(mPropertyLayoutAdapter);
			mPropertyLayoutListView.setAdapter(mPropertyLayoutAdapter);

			((DragNDropListView) mPropertyLayoutListView)
					.setRemoveListener(new RemoveListener() {

						@Override
						public void onRemove(int which) {
							// TODO Auto-generated method stub

						}
					});

			((DragNDropListView) mPropertyLayoutListView)
					.setDropListener(new DropListener() {

						@Override
						public void onDrop(int from, int to) {
							List<Inspection> list = mInspectionsList;
							int size = list.size();
							// Log.e("TO and from  ",to+"    <<FROM   >>   "+from+" >>   "+size);
							if (size > to) {
								Inspection temp = list.get(from);
								list.remove(from);
								list.add(to, temp);

								updatePropertyLayout(mInspectionsList);
							} else {

							}

						}
					});

			((DragNDropListView) mPropertyLayoutListView)
					.setDragListener(new com.detector.inspector.lib.widget.DragListener() {

						public void onStopDrag(View itemView) {
							itemView.setVisibility(View.VISIBLE);
							itemView.setBackgroundColor(defaultBackgroundColor);
							Log.e("Drag onStopDrag  ", " Drag onStopDrag  ");
						}

						int backgroundColor = 0xe0103010;
						int defaultBackgroundColor;

						public void onDrag(int x, int y, ListView listView) {
							// TODO Auto-generated method stub
						}

						public void onStartDrag(View itemView) {
							itemView.setVisibility(View.INVISIBLE);
							defaultBackgroundColor = itemView
									.getDrawingCacheBackgroundColor();
							itemView.setBackgroundColor(backgroundColor);
							Log.e("Drag start  ", " Drag Start  ");
						}
					});

		}
	}

	private void updatePropertyAdapter(List<Inspection> list) {
		mPropertyLayoutAdapter.setData(createGroupData(list));
		mPropertyLayoutAdapter.notifyDataSetChanged();
	}

	private ReorderAdapter createAdapter(List<Inspection> list) {

		String[] group_from = new String[] { "section_name",
				"appointment_time", "arrow_mark", "large_ladder",
				"inspection_note" };
		int[] group_to = new int[] { R.id.section_name, R.id.appointment_time,
				R.id.arrow_mark, R.id.large_ladder, R.id.inspection_note };

		return new ReorderAdapter(this, createGroupData(list),
				R.layout.reorder_layout_item, group_from, group_to);

	}

	@SuppressWarnings({ "rawtypes" })
	private List<Map<String, ?>> createGroupData(List<Inspection> list) {
		// initialize group item data map

		List<Map<String, ?>> group_list = new ArrayList<Map<String, ?>>();
		Log.i("List Is >>>", list.toString());
		for (Inspection type : list) {
			// set group item value

			Map group_data_map = new HashMap();
			group_data_map.put("large_ladder", null);
			group_data_map.put("inspection_note", null);

			String keyTime = type.getKeytime();
			if (keyTime.trim().equalsIgnoreCase("00:00 AM")) {
				keyTime = type.getKeyNumber();

				if (keyTime != null && keyTime.length() > 0) {
					Log.e("Ket Number : ", keyTime);
				} else {
					keyTime = "";
				}
			} else {

			}

			group_data_map.put("appointment_time", "Key/Time : " + keyTime);

			String address = "Street No " + type.getStreetNumber()
					+ ", Street Name " + type.getStreetName() + ", "
					+ type.getSuburb() + ", " + type.getState() + " "
					+ type.getPostCode();

			// String address = type.getStreetNumber() + ", "
			// + type.getStreetName() + ", " + type.getSuburb() + ", "
			// + type.getState();

			group_data_map.put("section_name", address);
			group_data_map.put("arrow_mark", false);

			group_list.add(group_data_map);

		}
		return group_list;
	}

	@Override
	public void onRemove(int which) {
		if (which < 0 || which > SimpleListAdapter.mDataNew.size())
			return;
		mInspectionsList.remove(which);

	}

	@Override
	public void onDrop(int from, int to) {
		Inspection temp = mInspectionsList.get(from);
		mInspectionsList.remove(from);
		mInspectionsList.add(to, temp);
	}

}
