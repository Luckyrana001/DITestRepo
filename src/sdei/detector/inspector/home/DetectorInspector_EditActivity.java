package sdei.detector.inspector.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportSection;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.detector.inspector.synch.report.ReportItem;

public class DetectorInspector_EditActivity extends
		sdei.detector.inspector.BaseActivity implements
		EditBookingAdapter.EditAdapterCallback {

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_EditActivity";
	private ExpandableListView mListView;
	private EditBookingAdapter mAdapter;
	private UserProfile mProfile;
	private View mHomeBtn;
	private EditText mAddEdittxt;
	private Report newReport = null;
	private Report oldReport = null;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState)   {
		
		super.onBeforeCreate(savedInstanceState);
		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		oldReport = mProfile.getReport();
		newReport = new Report(oldReport);

		setContentView(R.layout.activity_edit);
	}

	@Override
	public void getView() {
		TextView   mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.edit_location));

		mHomeBtn = (Button) findViewById(R.id.home_btn);
		mHomeBtn.setBackgroundResource(R.drawable.selector_add_button);

		mHomeBtn.setVisibility(View.VISIBLE);
		mHomeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)          {
				mHomeBtn.setVisibility(View.GONE);
				mAddEdittxt.setVisibility(View.VISIBLE);
				mAddEdittxt.setFocusableInTouchMode(true);
				mAddEdittxt.setFocusable(true);
				mAddEdittxt.requestFocus();
				mAddEdittxt.setEnabled(true);

				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.showSoftInput(mAddEdittxt,
								InputMethodManager.SHOW_FORCED);
			}
		});

		mAddEdittxt = (EditText) findViewById(R.id.add_area_edit_txt);
		mAddEdittxt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					Utils.hideKeyboard(DetectorInspector_EditActivity.this,mAddEdittxt);

					String text = mAddEdittxt.getText().toString().trim();
					
					
					if (text.length() > 0) {

						mHomeBtn.setVisibility(View.VISIBLE);
						mAddEdittxt.setVisibility(View.GONE);
						
						if (newReport.getExistingSectioNameStatus(text)) 
						{
							Utils.showAlert(DetectorInspector_EditActivity.this,"Message", "Location name already exist!");
						} else {
							updateSectionName(newReport, text);

							// newReport.reorderReportSection();
							mAddEdittxt.setText("");
							updatePropertyLayout(newReport);
						}

					} 
					else 
					{
						Utils.showAlert(DetectorInspector_EditActivity.this,"Message", "Please enter the Location Name");
					}

					return true;
				}

				return false;
			}

		});
		
		mListView = (ExpandableListView) findViewById(R.id.start_inspection_list);
		mListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		updatePropertyLayout(newReport);
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_edit_btn:
			mProfile.setReport(oldReport);
			finish();
			break;
		case R.id.done_edit_btn:

			mProfile.setReport(newReport);
			moveToStartInspection();

			break;

		default:
			break;
		}
	}

	private void moveToStartInspection() {
		Intent intent = new Intent();
		intent.setAction(Detector_Inspector_StartInspectionActivity.ACTION);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed()  {
		mProfile.setReport(oldReport);
		finish();
	}

	private void updatePropertyLayout(Report mReport) {
		if (mAdapter != null) {
			updateListAdapterValue(mReport);
		} else {
			mListView.setGroupIndicator(null);
			mAdapter = createPropertyAdapter(newReport);
			mAdapter.setViewBinder(mAdapter);
			mAdapter.setPropertyCallback(DetectorInspector_EditActivity.this);
			mListView.setAdapter(mAdapter);
		}
	}

	private void updateListAdapterValue(Report mReport) {
		mAdapter.setData(createGroupData(mReport), createChildData(mReport));
		mAdapter.notifyDataSetChanged();
	}

	private EditBookingAdapter createPropertyAdapter(Report mReport) {

		String[] group_from = new String[] { "edit_delete_txt",
				"edit_save_edit_txt", "edit_edit_txt",
				"edit_location_name_edit_txt", "edit_location_name" };
		int[] group_to = new int[] { R.id.edit_delete_txt,
				R.id.edit_save_edit_txt, R.id.edit_edit_txt,
				R.id.edit_location_name_edit_txt, R.id.edit_location_name };
		String[] child_from = new String[] { "section_name", "section_number",
				"arrow_mark" };
		int[] child_to = new int[] { R.id.section_name, R.id.section_number,
				R.id.arrow_mark };

		return new EditBookingAdapter(this, createGroupData(mReport),
				createChildData(mReport),
				R.layout.edit_booking_delete_edit_btn,
				R.layout.property_layout_item, group_from, child_from,
				group_to, child_to);

	}

	private List<? extends List<? extends Map<String, ?>>> createChildData(
			Report mReport) {

		// initialize group item data map
		List<List<Map<String, ?>>> child_list = new ArrayList<List<Map<String, ?>>>();

		boolean editable = true;
		for (ReportSection type : mReport.getReportSections()) {
			editable = true;

			// set child item value
			List<Map<String, ?>> child_detail_list = new ArrayList<Map<String, ?>>();
			if (editable) {

				// set group item value
				Map group_data_map = new HashMap();
				group_data_map.put("section_number", null);
				group_data_map.put("section_name", null);
				group_data_map.put("arrow_mark", null);

				child_detail_list.add(group_data_map);

			}
			child_list.add(child_detail_list);
		}
		return child_list;
	}

	private List<? extends Map<String, ?>> createGroupData(Report mReport) {
		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		boolean showTitleFlag = true;

		for (ReportSection p : mReport.getReportSections()) {
			Map data_map = new HashMap();

			data_map.put("edit_delete_txt", p);

			data_map.put("edit_save_edit_txt", p.getEditLocation());
			data_map.put("edit_edit_txt", p.getEditLocation());
			data_map.put("edit_location_name_edit_txt", p);
			data_map.put("edit_location_name", p);

			data.add(data_map);

		}
		return data;
	}

	public void showAlert(final Context context, String title, String message,
			final int position) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
								DatabaseUtil.deletePhoto(newReport
										.getReportSections().get(position)
										.getReportPhotos(), context);
								newReport.getReportSections().remove(position);
								updatePropertyLayout(newReport);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				})

				.create().show();
	}

	@Override
	public void EditListItem(int position) {
		newReport.getReportSections().get(position).setEditLocation(true);
		updatePropertyLayout(newReport);
	}

	@Override
	public void setSaveEditItem(int position) {
		Log.d(getLocalClassName(), position + " : position");
		EditText mEditText = (EditText) mListView.getChildAt(position)
				.findViewById(R.id.edit_location_name_edit_txt);
		Utils.hideKeyboard(DetectorInspector_EditActivity.this, mEditText);
		String text = null;
		if (mEditText != null) {
			Log.d(getCallingPackage(), mEditText.getText().toString());
			text = mEditText.getText().toString().trim();
		}

		if (text.length() > 0) {
			if (newReport.getExistingSectioNameStatus(text)) {
				Utils.showAlert(DetectorInspector_EditActivity.this, "Message",
						"Location name already exist!");
			} else {
				newReport.getReportSections().get(position).setName(text);
				newReport.getReportSections().get(position)
						.setEditLocation(false);
				updatePropertyLayout(newReport);
			}

		} else {
			Utils.showAlert(DetectorInspector_EditActivity.this, "Message",
					"Please enter the Location Name");
		}
	}

	private void updateSectionName(Report newReport, String mSectionName) {
		ReportSection add_section = new ReportSection();
		
		add_section.setSectionId(0);
		add_section.setReportId(newReport.getReportId());
		add_section.setName(mSectionName);
		add_section.setEditLocation(false);
		add_section.setHistoryDetail(false);
		add_section.setItems(new ReportItem(newReport.getReportId(), 0,	mSectionName, 0));
		newReport.getReportSections().add(add_section);
	}

	@Override
	public void DeleteListItem(int position, ReportSection reportSection) {
		showAlert(DetectorInspector_EditActivity.this, "Confirmation Message",
				"Do you want to delete this location?", position);
	}

}
