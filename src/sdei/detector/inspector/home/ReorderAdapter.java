package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class ReorderAdapter extends SimpleListAdapter implements
		SimpleListAdapter.ViewBinder {
	private AdapterCallback mCallback;

	public void setAdapterCallback(AdapterCallback callback) {
		mCallback = callback;
	}

	public static interface AdapterCallback {

		void ClickOnNotCompleted(int position, Inspection data);
	}

	public ReorderAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean setViewValue(View view, final Object data, final int position) {
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {
			if (data instanceof String) {
				((TextView) v).setText((String) data);
			} else if (data instanceof Inspection) {
				v.setVisibility(View.GONE);

				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mCallback.ClickOnNotCompleted(position,
								(Inspection) data);
					}
				});

			}

		}
		return true;
	}
}
