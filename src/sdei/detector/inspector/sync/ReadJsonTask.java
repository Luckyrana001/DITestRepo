package sdei.detector.inspector.sync;

import sdei.detector.inspector.sync.report.Report;
import android.content.Context;
import android.os.AsyncTask;

import com.detector.inspector.lib.util.Util;
import com.google.myjson.Gson;

public class ReadJsonTask extends AsyncTask<String, Void, ParseJsonResult> {

	private Context mContext;
	
	public ReadJsonTask(Context context) {
		mContext = context;
	}

	@Override
	protected ParseJsonResult doInBackground(String... params) {
		// TODO Auto-generated method stub
		String report_uuid = params[0];
		String text = Util.readJSONFile(mContext,report_uuid);		
		ParseJsonResult result = new ParseJsonResult();
		Gson gson = new Gson();
		Report report;
		try {
			report = gson.fromJson(text.toString(), Report.class);
			result.isSuccess = true;
			result.report = report;
		} catch (Exception e) {
			result.isSuccess = false;
		}
		
		return result;

	}


}

