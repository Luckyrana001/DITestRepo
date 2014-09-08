package sdei.detector.inspector.sync.report;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.detector.inspector.lib.util.Util;

public class Report implements Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 778931009872698964L;

	private transient String date_created;// report created date
	private transient String date_modified;// report modified date
	private transient String report_uuid;// unique id
	
	
	private String id;// report_id
	private String iid;// property_id
	private String n;// name

	private boolean leftCard;
	private boolean signature;
	private String serviceNote;
	private String problemNote;

	private List<ReportSection> reportSections = new LinkedList<ReportSection>();

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(getReportId()); // report id
		dest.writeString(n);// name
		dest.writeString(date_created);// created date
		dest.writeString(date_modified);// modified date
	
		
		dest.writeList(reportSections);// report section
		dest.writeString(iid);// property Id
		dest.writeString(report_uuid);// unique id

		dest.writeBooleanArray(new boolean[] { leftCard });
		dest.writeBooleanArray(new boolean[] { signature });
		dest.writeString(serviceNote);
		dest.writeString(problemNote);

	}

	public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
		public Report createFromParcel(Parcel in) {
			return new Report(in);
		}

		public Report[] newArray(int size) {
			return new Report[size];
		}
	};

	private Report(Parcel in) {

		id = in.readString();
		n = in.readString();
		date_created = in.readString();
		date_modified = in.readString();
		
	
		
		in.readList(reportSections, getClass().getClassLoader());
		iid = in.readString();
		report_uuid = in.readString();

		boolean[] b2 = new boolean[1];
		in.readBooleanArray(b2);
		leftCard = b2[0];
		boolean[] b3 = new boolean[1];
		in.readBooleanArray(b3);
		signature = b3[0];

		serviceNote = in.readString();
		problemNote = in.readString();

	}

	public Report() {
		// TODO Auto-generated constructor stub
	}

	public List<ReportSection> getReportSections() {
		return reportSections;
	}

	public boolean isLeftCard() {
		return leftCard;
	}

	public void setLeftCard(boolean leftCard) {
		this.leftCard = leftCard;
	}

	public boolean isSignature() {
		return signature;
	}

	public void setSignature(boolean signature) {
		this.signature = signature;
	}

	public String getServiceNote() {
		return serviceNote;
	}

	public void setServiceNote(String serviceNote) {
		this.serviceNote = serviceNote;
	}

	public String getInspectionId() {
		return iid;
	}

	public void setInspectionId(String iid) {
		this.iid = iid;
	}

	public void setReportSections(List<ReportSection> list) {
		reportSections = list;
	}

	public String getReportId() {
		return Util.parseNullString(id);
	}

	public void setReportId(String i) {
		id = i;
	}

	public String getReportName() {
		return Util.parseNullString(n);
	}

	public void setName(String s) {
		n = s;
	}

	public String getReportUUID() {
		return report_uuid;
	}

	public void setReportUUID(String s) {
		report_uuid = s;
	}

	public String getProblemNote() {
		return Util.parseNullString(problemNote);
	}

	public void setProblemNote(String problemNote) {
		this.problemNote = problemNote;
	}

	public Report(String ns, String rp_id, String dc, String dm, String insp_id) {
		n = ns;
		id = rp_id;
		date_created = dc;
		date_modified = dm;
		iid = insp_id;
	}

	public Report(Report report) {

		iid = report.iid;
		id = (report.id == null) ? null : new String(report.id);
		report_uuid = (report.report_uuid == null) ? null : new String(report.report_uuid);
		n = (report.n == null) ? null : new String(report.n);
		date_created = (report.date_created == null) ? null : new String(report.date_created);
		date_modified = (report.date_modified == null) ? null : new String(report.date_modified);
		

		// Copy report section
		List<ReportSection> new_report_section = new LinkedList<ReportSection>();
		
		for (ReportSection section : report.getReportSections()) 
		{
			new_report_section.add(new ReportSection(section));
		}
		reportSections = new_report_section;

		leftCard    = false;
		signature   = false;
		serviceNote = report.getServiceNote();
		this.problemNote = report.getProblemNote();

	}

	public void updateReportItem() {
		for (ReportSection section : getReportSections()) {
			section.setName(section.getName());
			for (ReportPhoto item : section.getReportPhotos()) {
				item.setPhotoId(item.getPhotoId());
				item.setItemUUID(item.getItemUUID());
				item.setHeight(item.getHeight());
				item.setItemId(item.getItemId());
				item.setAngle(item.getAngle());

			}
		}
	}

	public void addPhoto(ReportPhoto photo) {
		for (ReportSection section : getReportSections()) {
			if (section.getItemUUID().equals(photo.getItemUUID())) {
				section.getReportPhotos().add(photo);
				return;
			}
		}
		return;
	}

	public boolean getExistingSectioNameStatus(String text) {
		boolean flag = false;
		for (ReportSection section : getReportSections()) {
			if (section.getName().trim().equalsIgnoreCase(text.trim())) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	

}
