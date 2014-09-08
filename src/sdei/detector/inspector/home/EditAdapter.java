package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import sdei.detector.inspector.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.detector.inspector.lib.model.EditLocation;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class EditAdapter extends SimpleListAdapter implements
		SimpleListAdapter.ViewBinder {

	public EditAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	private EditAdapterCallback mCallback;

	public void setPropertyCallback(EditAdapterCallback callback) {
		mCallback = callback;
	}

	public static interface EditAdapterCallback {

		void DeleteListItem(int position);

		void EditListItem(int position);

		void setSaveEditItem(int position);

	}

	@Override
	public boolean setViewValue(View view, Object data, final int position) {
		// TODO Auto-generated method stub
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {
			if (data instanceof EditLocation) {
				final EditLocation mEditLocation = (EditLocation) data;
				if (v.getId() == R.id.edit_location_name) {
					if (mEditLocation.getEditLocation()) {
						v.setVisibility(View.GONE);
					} else {
						v.setVisibility(View.VISIBLE);
					}
					((TextView) v).setText(mEditLocation.getLocationName());
				} else if (v.getId() == R.id.edit_delete_txt) {

					if (mEditLocation.getEditLocation()) {
						v.setVisibility(View.GONE);
					} else {
						v.setVisibility(View.VISIBLE);
					}
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Log.e(getClass().getCanonicalName(),
									mEditLocation.getLocationName() + "&&&&"
											+ mEditLocation.getEditLocation());
							mCallback.DeleteListItem(position);
						}
					});
				}

			} else if (data instanceof Boolean) {
				if (v.getId() == R.id.edit_save_edit_txt) {
					if ((Boolean) data) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.setSaveEditItem(position);
						}
					});
				} else if (v.getId() == R.id.edit_edit_txt) {
					if ((Boolean) data) {
						v.setVisibility(View.GONE);
					} else {
						v.setVisibility(View.VISIBLE);
					}
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.EditListItem(position);
						}
					});
				}
			}

		}else if (v instanceof EditText) {
			
			Log.e(getClass().getCanonicalName(),
					 "&&&&"
							);
			if (data instanceof EditLocation) {
				final EditLocation mEditLocation = (EditLocation) data;
				Log.e(getClass().getCanonicalName(),
						mEditLocation.getLocationName() + "&&&&"
								+ mEditLocation.getEditLocation());
				
				
				if (v.getId() == R.id.edit_location_name_edit_txt) {
					if (mEditLocation.getEditLocation()) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}

				}
			}
		}

		return true;
	}
}
