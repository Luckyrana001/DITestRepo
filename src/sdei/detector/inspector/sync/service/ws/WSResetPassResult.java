package sdei.detector.inspector.sync.service.ws;

import java.io.Serializable;

public class WSResetPassResult implements Serializable {

	static final long serialVersionUID = -6867328656165535550L;

	private String message;
	private int status;

	public WSResetPassResult() {

	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

}
