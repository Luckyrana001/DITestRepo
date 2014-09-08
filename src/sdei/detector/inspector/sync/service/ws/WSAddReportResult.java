package sdei.detector.inspector.sync.service.ws;

import java.io.Serializable;

public class WSAddReportResult implements Serializable {

	static final long serialVersionUID = -602049008639482257L;

	private int status;
	private String propertyId;
	private String message;

	public WSAddReportResult() {

	}

	public String getAddedPropertyId() {
		return propertyId;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

}
