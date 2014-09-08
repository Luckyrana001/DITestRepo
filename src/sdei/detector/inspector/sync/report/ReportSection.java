package sdei.detector.inspector.sync.report;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.detector.inspector.lib.util.Util;
import com.detector.inspector.synch.report.ReportItem;

public class ReportSection implements Serializable, Parcelable {

	private static final long serialVersionUID   = -7442266575463113950L;
	private transient int     section_count;
	private transient String  section_type;
	private transient Boolean editLocation;
	private transient String  internal_uuid      = Util.getUUID(); 
	// internal uuid
	private  boolean historyDetail;
	
	
	private int       id;     // report section id
	private int       dr;     // display rank
	private String    rid;    // report id

	private String n;         // name

	private boolean is_deleted;
	private String  date_created;
	private String  date_modified;


	private List<ReportPhoto> reportPhotos  =  new LinkedList<ReportPhoto>();
	private ReportItem        reportItems;

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setType(String s) {
		section_type = s;
	}

	public String getType() {

		return Util.parseNullString(section_type);
	}

	public void setName(String s) {
		n = s;
	}

	public String getName() {

		return Util.parseNullString(n);
	}

	public String getReportId() {
		return Util.parseNullString(rid);
	}

	public void setReportId(String id) {
		rid = id;
	}

	public int getSectionId() {
		return id;
	}

	public void setSectionId(int i) {
		id = i;
	}

	public int getSectionCount() {
		return section_count;
	}

	public void setSectionCount(int i) {
		section_count = i;
	}

	public String getDateCreated() {
		return Util.parseNullString(date_created);
	}

	public String getDateModified() {
		return Util.parseNullString(date_modified);
	}

	public boolean isDeleted() {
		return is_deleted;
	}

	public String getIsDeleted() {
		return String.valueOf(is_deleted);
	}

	public List<ReportPhoto> getReportPhotos() {
		return reportPhotos;
	}

	public void setReportPhotos(List<ReportPhoto> reportPhotos) {
		this.reportPhotos = reportPhotos;
	}

	public String getItemUUID() {
		return internal_uuid;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(n);
		dest.writeString(getReportId());
		dest.writeInt(id);
		dest.writeInt(section_count);
		dest.writeString(section_type);
		dest.writeInt(dr);
		dest.writeBooleanArray(new boolean[] { is_deleted });
		dest.writeString(date_created);
		dest.writeString(date_modified);
		dest.writeList(reportPhotos);
		dest.writeParcelable(reportItems, flags);

		dest.writeString(internal_uuid);
		dest.writeBooleanArray(new boolean[] { editLocation });
		dest.writeBooleanArray(new boolean[] { historyDetail });


	}

	public static final Parcelable.Creator<ReportSection> CREATOR = new Parcelable.Creator<ReportSection>() {
		public ReportSection createFromParcel(Parcel in) {
			return new ReportSection(in);
		}

		public ReportSection[] newArray(int size) {
			return new ReportSection[size];
		}
	};

	private ReportSection(Parcel in) {
		n = in.readString();
		section_count = in.readInt();
		rid = in.readString();
		id = in.readInt();
		section_type = in.readString();
		dr = in.readInt();
		boolean[] b = new boolean[1];
		in.readBooleanArray(b);
		is_deleted = b[0];
		date_created = in.readString();
		date_modified = in.readString();
		in.readList(reportPhotos, getClass().getClassLoader());
		reportItems = in.readParcelable(getClass().getClassLoader());
		internal_uuid = in.readString();
		boolean[] b1 = new boolean[1];
		in.readBooleanArray(b1);
		editLocation = b1[0];
		
		boolean[] b2 = new boolean[1];
		in.readBooleanArray(b2);
		historyDetail = b2[0];
		

		
	}

	public ReportSection() {

	}

	public ReportSection(String ns, String rp_id, int rs_id, String st,
			int display_rank, boolean isdel, String dc, String dm, String ph_id) {

		n = ns;
		rid = rp_id;
		id = rs_id;
		section_type = st;
		dr = display_rank;
		is_deleted = isdel;
		date_created = dc;
		date_modified = dm;
		section_count = 0;
		internal_uuid = ph_id;
		editLocation = false;

	}

	public ReportSection(ReportSection section) {

		section_count = section.section_count;
		id = section.id;
		dr = section.dr;

		rid = (section.rid == null) ? null : new String(section.rid);
		section_type   = (section.section_type == null) ? null : new String(section.section_type);
		n = (section.n == null) ? null : new String(section.n);

		is_deleted     = section.is_deleted;
		date_created   = (section.date_created == null) ? null : new String(section.date_created);
		date_modified  = (section.date_modified == null) ? null : new String(section.date_modified);

		List<ReportPhoto> new_photos = new LinkedList<ReportPhoto>();
		for (ReportPhoto photo : section.getReportPhotos()) {
			new_photos.add(new ReportPhoto(photo));
		}
		reportPhotos    = new_photos;
		reportItems     = section.getItems();
		internal_uuid   = (section.internal_uuid == null) ? null : new String(section.internal_uuid);
		editLocation    = false;

		

	}

	public Boolean getEditLocation() {
		return editLocation;
	}

	public void setEditLocation(Boolean editLocation) {
		this.editLocation = editLocation;
	}

	public ReportItem getItems() {
		return reportItems;
	}

	public void setItems(ReportItem items) {
		reportItems = items;
	}
	
	public boolean isHistoryDetail() {
		return historyDetail;
	}

	public void setHistoryDetail(boolean historyDetail) {
		this.historyDetail = historyDetail;
	}
	

	
}
