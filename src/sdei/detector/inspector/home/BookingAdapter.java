package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import sdei.detector.inspector.R;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class BookingAdapter extends SimpleListAdapter implements SimpleListAdapter.ViewBinder {
	private AdapterCallback mCallback;

	public void setAdapterCallback(AdapterCallback callback) 
	{
		mCallback = callback;
	}

	public static interface AdapterCallback {

		void ClickOnNotCompleted(int position, Inspection data);
		void OpenStartInspectionScreen (int position, Inspection data);

	}

	Context context;
	public BookingAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public boolean setViewValue(View view, final Object data, final int position) {
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		v.setVisibility(View.VISIBLE);
		if (v instanceof RelativeLayout)                {
			if (data instanceof Inspection)             {
				v.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v)  {
						mCallback.ClickOnNotCompleted(position,	(Inspection) data);
						return false;
					}
				});
			}

			v.setOnClickListener(new OnClickListener()  {

				@Override
				public void onClick(View v) 
				{
					mCallback.OpenStartInspectionScreen(position,(Inspection) data);
				}
			});

		} else if (v instanceof TextView) {
			if (data instanceof String) {
				((TextView) v).setText((String) data);
			} else if (data instanceof Inspection) {
				final Inspection type = (Inspection) data;
				if (v.getId() == R.id.large_ladder) {
					String largeLadder = type.isHasLargeLadder();

					if (largeLadder.trim().equalsIgnoreCase("True")) {
						v.setVisibility(View.VISIBLE);
					} else {
						v.setVisibility(View.GONE);
					}

					((TextView) v).setText("Large Ladder : " + largeLadder);

				} else if (v.getId() == R.id.inspection_note) {
					String note = type.getNotes();
					if (note.trim().length() > 0) {
						v.setVisibility(View.VISIBLE);
					} else 
					{
						v.setVisibility(View.GONE);
					}
					((TextView) v).setText("Note : " + note);

				} else if (v.getId() == R.id.appointment_time) {
					boolean flag = true;
					String keyTime = type.getKeytime();
					if (keyTime.trim().equalsIgnoreCase("00:00 AM")) {
						keyTime = type.getKeyNumber();
						flag = false;

						if (keyTime != null && keyTime.length() > 0) {
//							Log.e("Key Number : ", keyTime);
						} else {
							keyTime = "";
						}
					} else {

					}
					((TextView) v).setText(keyTime);

					if (flag) {
						((TextView) v).setTextColor(context.getResources()	.getColor(R.color.browncolor));
					} else {
						((TextView) v).setTextColor(Color.GREEN);
					}

				} else {
					v.setVisibility(View.GONE);
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.ClickOnNotCompleted(position, type);
						}
					});

				}

			}

		}
		return true;
	}
}
