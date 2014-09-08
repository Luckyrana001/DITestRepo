package sdei.detector.inspector.home;

import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.report.ReportPhoto;
import sdei.detector.inspector.util.Utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.detector.inspector.lib.photo.ImageUtil;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class PhotoAdapter extends SimpleListAdapter implements	SimpleListAdapter.ViewBinder {

	private PhotoAdapterCallback mCallback;
	private UserProfile mProfile;
	public PhotoAdapter(Context context, List<? extends Map<String, ?>> data,int resource, String[] from, int[] to)  {
		super(context, data, resource, from, to);
		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
	}

	@Override
	public boolean setViewValue(View view, Object data, final int position) {
		final View v = view;
		if (data == null) {
			v.setVisibility(View.GONE);
			return true;
		}
		    v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) {

		} else if (v instanceof ImageView) {
			if (data instanceof Integer) {
				((ImageView) v).setImageResource((Integer) data);
			} else if (data instanceof ReportPhoto) {
				final ReportPhoto photo = (ReportPhoto) data;
				if (v.getId() == R.id.report_item_zoom_icon) {
					v.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.startPhotoViewer(photo);
						}
					});
					v.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {

							mCallback.setDeleteImage(photo, position);

							return false;
						}
					});

				} else if (v.getId() == R.id.history_imagew_cancel_btn) {

					v.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCallback.setDeleteImage(photo, position);
						}
					});
				} else if (v.getId() == R.id.report_item_photo) {

					final String filename = photo.getPhotoId();
					int width = Math
							.round(Const.REPORT_PHOTO_SIZE_VALUES.SMALL_BMP_WIDTH
									* Utils.getScreenScale());
					int height = Math
							.round(Const.REPORT_PHOTO_SIZE_VALUES.SMALL_BMP_HEIGHT
									* Utils.getScreenScale());

					// Log.e("Test Here", height + " : height && width : " +
					// width);

					final String key = filename + "_small";

					String yfileName = mProfile.getPhotoToPoolString().get(key);
					Bitmap bm = null;
					if (yfileName == null) {
						bm = ImageUtil.decodePhoto(filename, width, height);
						//----------------- Commented by Akhil---------------
//						mProfile.addPhotoToPool(key,
//								ImageUtil.BitMapToString(bm));
						//--------------------------------------------------------------------------------
						// mProfile.addWeakPhotoToPool(key, bm);
					} else {
						// bm = ImageUtil.decodePhoto(yfileName, width, height);
						// bm = ImageUtil.pickDecodePhoto(yfileName);
						bm = ImageUtil.StringToBitMap(yfileName);
					}
					if (bm != null) {
						if (bm.isRecycled()) {
							return true;
						} else {

							if (bm.getWidth() > bm.getHeight()) {
								v.setLayoutParams(new RelativeLayout.LayoutParams(
										width, height));
								v.setBackgroundResource(R.drawable.ic_land_frame);
							} else {
								v.setLayoutParams(new RelativeLayout.LayoutParams(
										height, width));
								v.setBackgroundResource(R.drawable.ic_port_frame);
							}
							((ImageView) v).setImageBitmap(bm);

						}
					}
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

	public void setPhotoAdpaterCallback(PhotoAdapterCallback callback) {
		mCallback = callback;
	}

	public static interface PhotoAdapterCallback {
		void startPhotoViewer(ReportPhoto photo);

		void setDeleteImage(ReportPhoto photo, int position);
	}

}
