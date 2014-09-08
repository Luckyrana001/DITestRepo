package sdei.detector.inspector.home;

import sdei.detector.inspector.R;
import sdei.detector.inspector.home.PhotoAdapter.PhotoAdapterCallback;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportPhoto;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddMultipleView implements PhotoAdapterCallback {
	private LayoutInflater inflater;

	public AddMultipleView() {
		// TODO Auto-generated constructor stub
	}
	 
	public AddMultipleView(Report mReport, int selectedReport,
			Activity activity, LinearLayout multipleViewAdd) {
		// TODO Auto-generated constructor stub

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		multipleViewAdd.removeAllViews();
		for (int i = 0; i < mReport.getReportSections().size(); i++) {
			View mView = inflater.inflate(R.layout.start_inspection_child_item_new,	null);
			TextView mSection = (TextView) mView
					.findViewById(R.id.section_name);
			mSection.setText(mReport.getReportSections().get(i).getName());
			mSection.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			multipleViewAdd.addView(mView);
		}
	}

	@Override
	public void startPhotoViewer(ReportPhoto photo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeleteImage(ReportPhoto photo, int position) {
		// TODO Auto-generated method stub

	}
}
