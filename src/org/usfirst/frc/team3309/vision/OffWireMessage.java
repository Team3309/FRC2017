package org.usfirst.frc.team3309.vision;

import org.json.JSONObject;

/**
 * Used to convert Strings into OffWireMessage objects, which can be interpreted
 * as generic VisionMessages.
 */
public class OffWireMessage extends VisionMessage {

	private boolean mValid = false;
	private String mType = "unknown";
	private String mMessage = "{}";

	public OffWireMessage(String message) {

		JSONObject j = new JSONObject(message);
		mType = (String) j.get("type");
		mMessage = (String) j.get("message");
		mValid = true;

	}

	public boolean isValid() {
		return mValid;
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
