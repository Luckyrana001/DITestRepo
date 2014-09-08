package sdei.detector.inspector.sync.report;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportPhoto implements Serializable, Parcelable {

	private static final long serialVersionUID = -31763107583941190L;

	private transient String item_uuid;// section id

	private int id;// photo_id
	private int x;
	private int y;
	private int angle;
	private int q;// quality
	private Integer riid;// report
	private String dt;// created date
	private String photo_guid;// photo uuid
	private String name; // name of the image, populated on the server
	private String url;

	public ReportPhoto() {

	}

	/*
	 * @param 1. photo uuid 2. report item id: set to null to create new
	 * instance 3. report section id 4. quality 5. data created 6. name 7.
	 * report_photo_id: set to 0 to create a new one on server 8. url 9. width
	 * 10. height 11. angle
	 */
	public ReportPhoto(String ph_id, Integer ri_id, String uuid, int photo_q,
			String dc, String ns, int rpp_id, String u, int px, int py, int a) {

		photo_guid = ph_id;
		riid = ri_id;
		item_uuid = uuid;
		q = photo_q;
		dt = dc;
		name = ns;
		id = rpp_id;
		url = u;
		x = px;
		y = py;
		angle = a;
	}

	public ReportPhoto(ReportPhoto photo) {

		item_uuid = photo.item_uuid;
		q = photo.q;
		id = photo.id;
		x = photo.x;
		y = photo.y;
		angle = photo.angle;

		riid = (photo.riid == null) ? null : new Integer(photo.riid);
		photo_guid = (photo.photo_guid == null) ? null : new String(
				photo.photo_guid);
		dt = (photo.dt == null) ? null : new String(photo.dt);
		name = (photo.name == null) ? null : new String(photo.name);
		url = (photo.url == null) ? null : new String(photo.url);

	}

	public void setPhotoId(String s) {
		photo_guid = s;
	}

	public String getPhotoId() {

		return photo_guid;
	}

	public int getItemId() {
		return (riid == null) ? 0 : riid;
	}

	public void setItemId(int i) {
		riid = Integer.valueOf(i);
	}

	public String getItemUUID() {
		return item_uuid;
	}

	public void setItemUUID(String s) {
		item_uuid = s;
	}

	public int getWidth() {
		return x;
	}

	public void setWidth(int i) {
		x = i;
	}

	public int getHeight() {
		return y;
	}

	public void setHeight(int i) {
		y = i;
	}

	public void setAngle(int i) {
		angle = i;
	}
	public int getAngle(){
		return angle;
	}

	public String getDate() {
		return dt;
	}

	public void setDate(String dt) {
		this.dt = dt;
	}

	public long getDateAsMillis() {
		// The dt format is 2011-02-28T14:28:00.0000000+10:00
		if (dt.contains("/Date(")) {
			String date = dt.substring(5, dt.length() - 1);
			return Long.parseLong(date);
		} else {
			return Long.parseLong(dt);
		}
	}

	public int getQuality() {
		return q;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(getItemId());
		dest.writeString(item_uuid);
		dest.writeString(photo_guid);
		dest.writeInt(q);
		dest.writeString(dt);
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeString(url);
		dest.writeInt(x);
		dest.writeInt(y);
		dest.writeInt(angle);
	}

	public static final Parcelable.Creator<ReportPhoto> CREATOR = new Parcelable.Creator<ReportPhoto>() {
		public ReportPhoto createFromParcel(Parcel in) 
		{
			return new ReportPhoto(in);
		}

		public ReportPhoto[] newArray(int size) {
			return new ReportPhoto[size];
		}
	};

	private ReportPhoto(Parcel in) {
		riid = Integer.valueOf(in.readInt());
		item_uuid = in.readString();
		photo_guid = in.readString();
		q = in.readInt();
		dt = in.readString();

		name = in.readString();
		id = in.readInt();
		url = in.readString();
		x = in.readInt();
		y = in.readInt();
		angle = in.readInt();
	}

}
