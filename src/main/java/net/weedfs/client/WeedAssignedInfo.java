package net.weedfs.client;
/*
 * Class to take FS info.
 */

public class WeedAssignedInfo {
	int count;
	String fid;
	String publicUrl;
	String url;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "WeedAssignedInfo [count=" + count + ", fid=" + fid
				+ ", publicUrl=" + publicUrl + ", url=" + url + "]";
	}

}
