package net.weedfs.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

/*
 *  WeedFSClient Clss
 */

public class WeedFSClient {
	// master url
	private String masterAddress;
	private String masterPort;

	public WeedFSClient(String address, String port) {
		this.masterAddress = address;
		this.masterPort = port;
		
	}

	public RequestResult write(String path) {
		File inputFile = new File(path);
		RequestResult result = new RequestResult();
		WeedAssignedInfo assignedInfo = null;

		if (!inputFile.exists()) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg("File not exist");
		}
		try {
			BufferedReader in = sendHttpGetRequest("http://"+this.masterAddress+":"+this.masterPort + "/", "dir/assign");
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Gson gson = new Gson();
			assignedInfo = gson.fromJson(response.toString(),
					WeedAssignedInfo.class);

		} catch (Exception e) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg(e.toString());
			return result;
		}

		try {
			// add more file options;
			FileBody fileBody = new FileBody(inputFile, "text/plain");//

			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost post = new HttpPost("http://"
					+ assignedInfo.getPublicUrl() + "/" + assignedInfo.getFid());
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			entity.addPart("fileBody", fileBody);
			post.setEntity(entity);

			String response = EntityUtils.toString(client.execute(post)
					.getEntity(), "UTF-8");
			client.getConnectionManager().shutdown();

			int size = Integer.parseInt(response.substring(8,
					response.length() - 1));
			result.setErrorMsg(null);
			result.setFid(assignedInfo.getFid());
			result.setSize(size);
			result.setSuccess(true);
			return result;

		} catch (Exception e) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg(e.toString());
			return result;
		}
	}

	/*
	 * Used to send request to WeedFS server
	 */

	private BufferedReader sendHttpGetRequest(String host,
			String requestUrlDetail) throws Exception {

		HttpURLConnection con = null;
		try {
			URL requestUrl = new URL(host.toString() + requestUrlDetail);
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
		System.out.println("\nSending 'GET' request to URL : " + this.masterAddress + ":" + this.masterPort);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		return in;
	}

	/*
	 * example: fid = 3,01637037d6
	 */
	
	public RequestResult read(String fid, String path) {
		String volumnId = fid.split(",")[0];
		ServerLocations locations = null;

		File output = new File(path);
		RequestResult result = new RequestResult();

		if (output.exists()) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg("File already exist");
		}

		try {
			BufferedReader in = sendHttpGetRequest("http://"+this.masterAddress+":"+this.masterPort + "/",
					"dir/lookup?volumeId=" + volumnId);
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			Gson gson = new Gson();
			locations = gson.fromJson(response.toString(),
					ServerLocations.class);

		} catch (Exception e) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg(e.toString());
			return result;
		}
		BufferedReader in = null;
		BufferedWriter wr = null;
		try {
			in = sendHttpGetRequest("http://" + locations.getOnePublicUrl()
					+ "/", fid);
			String inputLine;

			output.createNewFile();
			wr = new BufferedWriter(new FileWriter(output));

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				wr.write(inputLine);
			}
			in.close();

		} catch (Exception e) {
			result.setFid(null);
			result.setSuccess(false);
			result.setErrorMsg(e.toString());
			return result;

		} finally {
			try {
				if (in != null && wr != null) {
					in.close();
					wr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		RequestResult result = null;

		WeedFSClient client = new WeedFSClient("localhost", "9333");
		try {
			result = client
					.write("/Volumes/MacintoshHD/hackthon/weed-fs/testFile.txt");

			client.read(result.getFid(), "/Volumes/MacintoshHD/hackthon/weed-fs/testFile1.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(result.toString());
	}
}
