package sdei.detector.inspector.sync.service.ws;

import android.os.Parcel;
import android.os.Parcelable;


 /*
  * 
  * a parcelable object to hold data
  * 
  * */
public class WSRequest implements Parcelable {
	private static final String tag = WSRequest.class.getSimpleName();
	/*
	 * URL string
	 */
	private String mUrl;

	/*
	 * HTTP Method
	 */
	private int mHttpMethod;

	/*
	 * Request Type
	 */
	private int mRequestType;
	
	public WSRequest(){
		
	}
	
	public WSRequest(String url, int method, int type){
		
		mHttpMethod = method;
		mUrl = url;
		mRequestType = type;
	}

	/*
	 * Get url
	 */
	public int getType() {
		return mRequestType;
	}

	/*
	 * Set url
	 */
	public void setType(int type) {
		mRequestType = type;
	}
	
	/*
	 * Get url
	 */
	public String getUrl() {
		return mUrl;
	}

	/*
	 * Set url
	 */
	public void setUrl(String url) {
		mUrl = url;
	}

	/*
	 * Get HTTP get method
	 */
	public int getHttpMethod() {
		return mHttpMethod;
	}

	/*
	 * Set HTTP get method
	 */
	public void setHttpMethod(int method) {
		mHttpMethod = method;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mUrl);
		dest.writeInt(mHttpMethod);
		dest.writeInt(mRequestType);
	}

	public static final Parcelable.Creator<WSRequest> CREATOR = new Parcelable.Creator<WSRequest>() {
		public WSRequest createFromParcel(Parcel in) {
			return new WSRequest(in);
		}

		public WSRequest[] newArray(int size) {
			return new WSRequest[size];
		}
	};

	private WSRequest(Parcel in) {

		mUrl = in.readString();
		mHttpMethod = in.readInt();
		mRequestType = in.readInt();
	}

}
