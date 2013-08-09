package net.weedfs.client;

public class RequestResult {
	boolean success;
	int size;
	String fid;
	String errorMsg;

	@Override
	public String toString() {
		return "WriteRequestResult [success=" + success + ", size=" + size
				+ ", fid=" + fid + ", errorMsg=" + errorMsg + "]";
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public RequestResult() {
	}

	public RequestResult(boolean success, String fid) {
		this.success = success;
		this.fid = fid;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
