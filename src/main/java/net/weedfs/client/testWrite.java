package net.weedfs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class testWrite {

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {

		String inputLine;
		HttpURLConnection con = null;
		try {
			URL requestUrl = new URL(
					"http://localhost:9333/dir/lookup?volumeId=3");
			con = (HttpURLConnection) requestUrl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// optional default is GET
		try {
			con.setRequestMethod("GET");
		} catch (ProtocolException e) {
		}

		// add request header
		con.setRequestProperty("User-Agent", "");
		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		System.out.println(response.toString());

		in.close();

	}

}
