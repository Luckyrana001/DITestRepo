package sdei.detector.inspector.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sdei.detector.inspector.R;
import sdei.detector.inspector.sync.report.ReportPhoto;
import sdei.detector.inspector.sync.report.ReportSection;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import com.detector.inspector.lib.widget.ComplexExpandableAdapter;
import com.detector.inspector.synch.report.ReportItem;

public class ComplexPropertyLayoutAdapter extends ComplexExpandableAdapter	implements ComplexExpandableAdapter.ViewBinder {

	private Context mContext;
	private AdapterCallback mCallback;
	private InputMethodManager imm;
	String editTextValue = "";
	EditText field_input;
	
	public ComplexPropertyLayoutAdapter(Context context,
			List<? extends Map<String, ?>> group_data,
			List<? extends List<? extends Map<String, ?>>> child_data,
			int group_resource, int child_resource, String[] group_from,
			String[] child_from, int[] group_to, int[] child_to) {
		super(context, group_data, child_data, group_resource, child_resource,group_from, child_from, group_to, child_to);
		mContext = context;
		imm = (InputMethodManager) mContext	.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void onGroupExpanded(int groupPosition)  
	{
		// mCallback.collapseOtherGroup(groupPosition);
	}

	@Override
	public void onGroupCollapsed(int groupPosition) 
	{
		// mCallback.saveLayout(groupPosition);
	}

	public void setAdpaterCallback(AdapterCallback callback) 
	{
		mCallback = callback;
	}

	@Override
	public boolean setGroupView(int groupPosition, View view, Object data,	boolean isExpanded) {

		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {
			if (data instanceof String) {
				((TextView) v).setText((String) data);
			} else if (data instanceof Boolean) {

				if (isExpanded) 
				{
					((TextView) v).setBackgroundResource(R.drawable.im_inspectioncollasped);
				} else 
				{
					((TextView) v).setBackgroundResource(R.drawable.im_inspectioncollasped_allow);
				}

				final int group_index = groupPosition;
				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mCallback.collapseOtherGroup(group_index);
					}
				});

			} else if (data instanceof Integer) {
				if (v.getId() == R.id.section_number) {
					// ((TextView) v).setText(String.valueOf(((Integer) data)
					// .intValue()));
				}
			}
		} else if (v instanceof ImageView) {
			if (data instanceof Integer) {
				((ImageView) v).setImageResource(((Integer) data).intValue());
			}
		}
		return true;
	}

	@Override
	public boolean setChildView(final int groupPosition,
			final int childPosition, View view, Object data, boolean isLastChild) {
		// TODO Auto-generated method stub
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof Button) {
			if (data instanceof ReportSection) {
				final ReportSection reportSection = (ReportSection) data;
				if (v.getId() == R.id.report_item_item_camera_btn) {
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.capturePhoto(reportSection);
						}
					});
				}

			}
		} else if (v instanceof EditText) {
			if (data instanceof String) {
				((EditText) v).setText((String) data);
			} else if (data instanceof ReportItem) {
				final ReportItem mReportItem = (ReportItem) data;
				switch (v.getId()) {
				case R.id.electrical_notes_edit_txt:

					// field_input = (EditText) v
					// .findViewById(R.id.electrical_notes_edit_txt);
					//
					// if (mReportItem.isVoltProblem()) {
					// v.setVisibility(View.VISIBLE);
					// } else {
					// v.setVisibility(View.GONE);
					// }
					//
					// field_input.setInputType(InputType.TYPE_CLASS_TEXT);
					//
					// field_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
					// field_input.setText(mReportItem.getElectricalNote());
					//
					// final ReportItem input_field = mReportItem;
					// final EditText field_input_text = field_input;
					//
					// field_input
					// .setOnEditorActionListener(new
					// TextView.OnEditorActionListener() {
					// @Override
					// public boolean onEditorAction(TextView v,
					// int actionId, KeyEvent event) {
					// if (actionId == EditorInfo.IME_ACTION_DONE) {
					//
					// imm.hideSoftInputFromWindow(
					// field_input.getWindowToken(), 0);
					// input_field.setElectricalNote(v
					// .getText().toString());
					//
					// }
					// return false;
					// }
					// });

					// field_input.addTextChangedListener(new TextWatcher() {
					//
					// @Override
					// public void beforeTextChanged(CharSequence s,
					// int start, int count, int after) {
					// // TODO Auto-generated method stub
					//
					// }
					//
					// @Override
					// public void onTextChanged(CharSequence s, int start,
					// int before, int count) {
					// // TODO Auto-generated method stub
					//
					// }
					//
					// @Override
					// public void afterTextChanged(Editable s) {
					// // TODO Auto-generated method stub
					// input_field.setElectricalNote(s.toString());
					// }
					//
					// });

					((EditText) v).setText(mReportItem.getElectricalNote());

					if (mReportItem.isVoltProblem()) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}

					((EditText) v).setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							mCallback.editNoteValue(v.getId(), mReportItem,
									groupPosition, childPosition);
							return false;
						}
					});

					break;
				case R.id.manufacture_edit_txt:

					// field_input = (EditText) v
					// .findViewById(R.id.manufacture_edit_txt);
					//
					// field_input.setInputType(InputType.TYPE_CLASS_TEXT);
					//
					// field_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
					// field_input.setText(mReportItem.getManufacturer());
					//
					// final ReportItem input_field1 = mReportItem;
					// final EditText field_input_text1 = field_input;
					//
					// field_input
					// .setOnEditorActionListener(new
					// TextView.OnEditorActionListener() {
					// @Override
					// public boolean onEditorAction(TextView v,
					// int actionId, KeyEvent event) {
					// if (actionId == EditorInfo.IME_ACTION_DONE) {
					//
					// imm.hideSoftInputFromWindow(
					// field_input.getWindowToken(), 0);
					// input_field1.setManufacturer(v
					// .getText().toString());
					//
					// }
					// return false;
					// }
					// });

					// field_input.addTextChangedListener(new TextWatcher() {
					//
					// @Override
					// public void beforeTextChanged(CharSequence s,
					// int start, int count, int after) {
					// // TODO Auto-generated method stub
					//
					// }
					//
					// @Override
					// public void onTextChanged(CharSequence s, int start,
					// int before, int count) {
					// // TODO Auto-generated method stub
					//
					// }
					//
					// @Override
					// public void afterTextChanged(Editable s) {
					// // TODO Auto-generated method stub
					// input_field1.setManufacturer(s.toString());
					// }
					//
					// });

					((EditText) v).setText(mReportItem.getManufacturer());
					((EditText) v).setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							mCallback.editNoteValue(v.getId(), mReportItem,
									groupPosition, childPosition);
							return false;
						}
					});

					break;

				case R.id.expiry_edit_txt:
					((EditText) v).setText(mReportItem.getExpiryYear());
					((EditText) v)
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									mCallback.setDate(mReportItem,
											groupPosition, childPosition,
											v.getId());
								}
							});
					break;

				case R.id.new_expiry_year_edit_txt:
					((EditText) v).setText(mReportItem.getNewExpiryYear());
					((EditText) v)
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									mCallback.setDate(mReportItem,
											groupPosition, childPosition,
											v.getId());
								}
							});
					break;

				default:
					break;

				}
			} else if (data instanceof HashMap) {

				// final HashMap map=((HashMap)data);
				// String name=(String) map.get("name");
				// boolean b=(Boolean)map.get("visible");
				// ((EditText) v).setText(name);
				//
				// if (b) {
				// v.setVisibility(View.VISIBLE);
				// } else {
				// v.setVisibility(View.GONE);
				// }
				//
				// ((EditText) v).setOnTouchListener(new OnTouchListener() {
				//
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// mCallback.editNoteValue(v.getId(), mReportItem);
				// return false;
				// }
				// });

			}
		} else if (v instanceof TextView) {
			if (data instanceof String) {

				switch (v.getId()) {
				case R.id.detector_type_spinner:
					((TextView) v).setText((String) data);
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.chooseDetectorType(groupPosition,
									childPosition);
						}
					});
					break;

				case R.id.report_item_photo_number:
					((TextView) v).setText((String) data);
					break;
				}

			} else if (data instanceof ReportItem) {
				final ReportItem mReportItem = (ReportItem) data;
				switch (v.getId()) {
				case R.id.detector_type_spinner:

					break;
				case R.id.battery_replaced:
					setRadioButton(mReportItem.isBatteryReplaced(), v,
							mReportItem);
					break;
				case R.id.cleaned:
					setRadioButton(mReportItem.isCleaned(), v, mReportItem);
					break;
				case R.id.stricked_applied:
					setRadioButton(mReportItem.isStickedApplied(), v,
							mReportItem);

					break;

				case R.id.decibel_test:
					setRadioButton(mReportItem.isDecibelTest(), v, mReportItem);
					break;

				case R.id.not_required:
					setRadioButton(mReportItem.isNotRequired(), v, mReportItem);
					break;

				case R.id.volt_problem:
					setRadioButton(mReportItem.isVoltProblem(), v, mReportItem);
					break;

				// editText
				case R.id.electrical_note_txt:
					if (mReportItem.isVoltProblem()) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}
					break;

				default:
					break;
				}

			}
		} else if (v instanceof Gallery) {
			if (data instanceof ReportSection) {
				final List<Map<String, ?>> data_list = new ArrayList<Map<String, ?>>();
				final ReportSection item = (ReportSection) data;

				Log.d("TEST", "item photos: " + item.getReportPhotos().size());
				if (item.getReportPhotos().size() == 0) {
					v.setVisibility(View.GONE);
					return true;
				}
				Log.d("TEST", "create gallery");
				v.setVisibility(View.VISIBLE);
				// add photos
				for (ReportPhoto photo : item.getReportPhotos()) {
					Map gallery_data_map = new HashMap();
					gallery_data_map.put("item_photo", photo);
					gallery_data_map.put("report_item_zoom_icon", photo);
					data_list.add(gallery_data_map);
				}

				if (data_list.size() > 0) {
					final String[] from = new String[] { "item_photo",
							"report_item_zoom_icon" };

					final int[] to = new int[] { R.id.report_item_photo,
							R.id.report_item_zoom_icon };

					final PhotoAdapter adapter = new PhotoAdapter(mContext,
							data_list,
							R.layout.start_inspection_item_gallery_item_new,
							from, to);
					mCallback.setPhotoAdapterCallback(adapter);
					adapter.setViewBinder(adapter);
					((Gallery) v).setAdapter(adapter);
				}
			}

		} else {
			throw new IllegalStateException(
					v.getClass().getName()
							+ " is not a "
							+ " view that can be bounds by this SimpleExpandableAdapter");
		}

		return true;
	}

	private void setRadioButton(final boolean b, View v,
			final ReportItem mReportItem) {
		if (b) {
			v.setBackgroundResource(R.drawable.im_select_check_active);
		} else {
			v.setBackgroundResource(R.drawable.im_select_check_inactive);
		}
		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.setRadioButton(v, mReportItem, b);
			}
		});

	}

	public static interface AdapterCallback {
		public void collapseOtherGroup(int groupPosition);

		public void chooseDetectorType(int groupPosition, int childPosition);

		public void editNoteValue(int id, ReportItem mReportItem,int groupPosition, int childPosition);

		public void setDate(ReportItem mReportItem, int groupPosition,	int childPosition, int id);

		public void setRadioButton(View v, ReportItem mReportItem, boolean b);

		public void setPhotoAdapterCallback(PhotoAdapter adapter);

		public void capturePhoto(ReportSection reportSection);

	}

}
