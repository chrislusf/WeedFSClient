package net.weedfs.client;

import java.util.List;

public class ServerLocations {

	List<ServerLocation> locations;

	public List<ServerLocation> getLocations() {
		return locations;
	}

	public void setLocations(List<ServerLocation> locations) {
		this.locations = locations;
	}

	public class ServerLocation {

		String publicUrl;
		String url;

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
	}

	public String getOnePublicUrl() {

		return locations.get(0).getPublicUrl();

	}

}