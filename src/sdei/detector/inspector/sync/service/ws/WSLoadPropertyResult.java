package sdei.detector.inspector.sync.service.ws;

import java.io.Serializable;
import java.util.List;

import com.detector.inspector.lib.model.Inspection;

public class WSLoadPropertyResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2740488876188397550L;
	private int status;
	private String message;
	private List<Inspection> propertyInfo;

	public WSLoadPropertyResult() {
		// TODO Auto-generated constructor stub
	}

	public List<Inspection> getInspectionList(){
		return propertyInfo;
	}
	
	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}