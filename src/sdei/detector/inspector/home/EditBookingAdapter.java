package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import sdei.detector.inspector.R;
import sdei.detector.inspector.sync.report.ReportSection;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.detector.inspector.lib.widget.ComplexExpandableAdapter;

public class EditBookingAdapter extends ComplexExpandableAdapter implements
		ComplexExpandableAdapter.ViewBinder {

	public EditBookingAdapter(Context context,
			List<? extends Map<String, ?>> group_data,
			List<? extends List<? extends Map<String, ?>>> child_data,
			int group_resource, int child_resource, String[] group_from,
			String[] child_from, int[] group_to, int[] child_to) {
		super(context, group_data, child_data, group_resource, child_resource,
				group_from, child_from, group_to, child_to);

	}

	private EditAdapterCallback mCallback;

	public void setPropertyCallback(EditAdapterCallback callback) {
		mCallback = callback;
	}

	public static interface EditAdapterCallback {

		void DeleteListItem(int position, ReportSection mEditLocation);

		void EditListItem(int position);

		void setSaveEditItem(int position);

	}

	@Override
	public boolean setGroupView(final int groupPositioin, View view,
			Object data, boolean isExpanded) {
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof EditText) {

			if (data instanceof ReportSection) {
				final ReportSection mEditLocation = (ReportSection) data;
				if (v.getId() == R.id.edit_location_name_edit_txt) {

					if (mEditLocation.getEditLocation()) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}
					((EditText) v).setText(mEditLocation.getName()
							.trim());

				}
			}
		} else if (v instanceof TextView) {
			if (data instanceof ReportSection) {
				final ReportSection mEditLocation = (ReportSection) data;
				if (v.getId() == R.id.edit_location_name) {
					if (mEditLocation.getEditLocation()) {
						v.setVisibility(View.GONE);
					} else {
						v.setVisibility(View.VISIBLE);
					}
					((TextView) v).setText(mEditLocation.getName());
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

							mCallback.DeleteListItem(groupPositioin,mEditLocation);
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

							mCallback.setSaveEditItem(groupPositioin);
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
							mCallback.EditListItem(groupPositioin);
						}
					});
				}
			}

		}

		return true;
	}

	@Override
	public boolean setChildView(int groupPositioin, int childPosition,
			View view, Object data, boolean isLastChild) {
		// TODO Auto-generated method stub
		return false;
	}
}
