package sdei.detector.inspector;

import android.content.Intent;
import android.os.Bundle;

public interface IViewActivity {

	void onBeforeCreate(Bundle savedInstanceState);
	void getView();
	void showProgress();
	void hideProgress();
    void startActivity(Intent intent);
    public void getActivtyResult(int requestCode, int resultCode, Intent data);

}
