package sdei.detector.inspector.sync.report;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportPhotoComment implements Serializable, Parcelable {

	private static final long serialVersionUID = -8770672354596591888L;
	
	private transient String photo_uuid;
	private transient String comment_uuid; // report coment uuid

	private int report_photo_comment_id;
	private int x;
	private int y;
	private int name; // Valid value are 1 - 12. Used as index
	private Integer report_photo_id;
	private String value;

	public ReportPhotoComment() {

	}
	
	public ReportPhotoComment(ReportPhotoComment rc) {
		
		x = rc.x;
		y = rc.y;
		comment_uuid = rc.comment_uuid;
		report_photo_comment_id = rc.report_photo_comment_id;
		name = rc.name;

		photo_uuid = (rc.photo_uuid == null) ? null : new String(rc.photo_uuid);
		value = (rc.value == null) ? null : new String(rc.value);
		report_photo_id = (rc.report_photo_id == null) ? null : new Integer(rc.report_photo_id);
	}
	
	/*
	 * @param
	 * 1. report_section_id
	 * 2. report_item_id
	 * 3. x
	 * 4. y
	 * 5. comment uuid
	 * 6. value
	 * 7. name: used as index
	 * 8. report_photo_id: set to null for new instance
	 * 9. report_photo_comment_id: set to 0 for new instance
	 */
	public ReportPhotoComment(String uuid, int x_cord, int y_cord, 
			String cm_id, String s, int c_index,Integer rp_id,
			int rpc_id) {

		photo_uuid = uuid;
		x = x_cord;
		y = y_cord;
		comment_uuid = cm_id;
		value = s;
		name = c_index;
		report_photo_id = rp_id;
		report_photo_comment_id = rpc_id;
	}
	
	public void setComment(String s) {
		value = s;
	}
	
	public String getComment() {
		
		return value;
	}

	public String getPhotoUUID() {
		return photo_uuid;
	}

	public void setPhotoUUID(String s) {
		photo_uuid = s;
	}

	public String getCommentId() {
		return comment_uuid;
	}

	public void setCommentId(String uuid) {
		comment_uuid = uuid;
	}
	
	public void setIndex(int i){
		name = i;
	}
	
	public int getIndex(){
		return name;
	}

	public int getX() {
		return x;
	}

	public void setX(int i) {
		x = i;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int i) {
		y = i;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(photo_uuid);
		dest.writeInt(x);
		dest.writeInt(y);
		dest.writeString(comment_uuid);
		dest.writeString(value);
		dest.writeInt(name);
	}

	public static final Parcelable.Creator<ReportPhotoComment> CREATOR = new Parcelable.Creator<ReportPhotoComment>() {
		public ReportPhotoComment createFromParcel(Parcel in) {
			return new ReportPhotoComment(in);
		}

		public ReportPhotoComment[] newArray(int size) {
			return new ReportPhotoComment[size];
		}
	};

	private ReportPhotoComment(Parcel in) {
		photo_uuid = in.readString();
		x = in.readInt();
		y = in.readInt();
		comment_uuid = in.readString();
		value = in.readString();
		name = in.readInt();
	}

}
