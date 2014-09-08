package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.detector.inspector.lib.widget.SimpleListAdapter;

public class DetectorInspector_HistoryAdapter extends SimpleListAdapter
		implements SimpleListAdapter.ViewBinder {

	public DetectorInspector_HistoryAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	private DetectorInspector_HistoryAdapterCallback mCallback;

	public void setPropertyCallback(
			DetectorInspector_HistoryAdapterCallback callback) {
		mCallback = callback;
	}

	public static interface DetectorInspector_HistoryAdapterCallback {
		void RefreshAdapter();
	}

	@Override
	public boolean setViewValue(View view, Object data, int position) {
		// TODO Auto-generated method stub
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {
			if (data instanceof String)
				((TextView) v).setText((String) data);
			else if (data instanceof Boolean) {
				if ((Boolean) data)
					((TextView) v).setVisibility(View.VISIBLE);
				else
					((TextView) v).setVisibility(View.GONE);
				
				
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mCallback.RefreshAdapter();						
					}
				});
				
				
			}

		} else if (v instanceof LinearLayout) {
			if (data instanceof Boolean)
				if ((Boolean) data)
					((LinearLayout) v).setVisibility(View.VISIBLE);
				else
					((LinearLayout) v).setVisibility(View.GONE);
		}

		return true;
	}
}
