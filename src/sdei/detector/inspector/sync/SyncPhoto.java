package sdei.detector.inspector.sync;

public class SyncPhoto {


	private String mPhotoUUID;
	private boolean isSynced;
	
	public String getPhotoUUID() {
		return mPhotoUUID;
	}
	
	public void setPhotoUUID(String mPhotoUUID) {
		this.mPhotoUUID = mPhotoUUID;
	}
	
	public boolean isSynced() {
		return isSynced;
	}
	
	public void setSynced(boolean isSynced) {
		this.isSynced = isSynced;
	}
	
}
