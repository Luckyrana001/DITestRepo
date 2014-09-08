package sdei.detector.inspector.sync.service.ws;

import java.io.Serializable;

public class Technician implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5046823065247268965L;
	public String technicianId;
	public String company;
	public String technicianName;
	public String validationOff;
	
	public boolean turnOffAlarmType;
	public boolean turnOffExpiryYear;
	public boolean turnOffNoOfAlarms;
	
}
