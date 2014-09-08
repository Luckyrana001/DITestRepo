package sdei.detector.inspector.sync.service.ws;

import sdei.detector.inspector.util.Const;
import android.os.Parcel;
import android.os.Parcelable;



   public class WSResponse implements Parcelable {

	/*
	 * HTTP status code
	 */
	private int m_httpStatus;
	
	/*
	 * Server returned xml string
	 */
	private String m_httpResult;

	public WSResponse() 
	{
		m_httpStatus = Const.HTTP_STATUS_CODES.NONE;
		m_httpResult = "";
	}

	/*
	 * Get HTTP status
	 */
	public int getHttpStatus() 
	{
		return m_httpStatus;
	}

	/*
	 * Set HTTP status
	 */
	public void setHttpStatus(int status) 
	{
		m_httpStatus = status;
	}

	/*
	 * Get HTTP status
	 */
	public String getHttpResult() 
	{
		return m_httpResult;
	}

	/*
	 * Set HTTP status
	 */
	public void setHttpResult(String result) {
		m_httpResult = result;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt   (m_httpStatus);
		dest.writeString(m_httpResult);
	}

	public static final Parcelable.Creator<WSResponse> CREATOR = new Parcelable.Creator<WSResponse>() {
		public WSResponse createFromParcel(Parcel in) {
			return new WSResponse(in);
		}

		public WSResponse[] newArray(int size) {
			return new WSResponse[size];
		}
	};

	private WSResponse(Parcel in) {

		m_httpStatus = in.readInt();
		m_httpResult = in.readString();
	}

}
