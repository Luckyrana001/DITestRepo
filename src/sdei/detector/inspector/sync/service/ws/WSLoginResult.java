package sdei.detector.inspector.sync.service.ws;

import java.io.Serializable;

public class WSLoginResult implements Serializable {

	static final long serialVersionUID = -7364461857784678551L;

	private int status;
	private String message;	
	private Technician technician;

	public WSLoginResult(){
		
	}
	
	public Technician getTechnicianId(){
		return technician;
	}
	
	

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	

	
}
