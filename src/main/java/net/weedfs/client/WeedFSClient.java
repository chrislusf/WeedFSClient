package net.weedfs.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
 *  WeedFSClient Class
 */
public class WeedFSClient {
    
    // master address & port number
    private String masterAddress;
    private String masterPort;
    
    public WeedFSClient (String address, String port) {
        this.masterAddress = address;
        this.masterPort = port;
    }
    
    public RequestResult write(String path) {
        
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        
        File inputFile = new File(path);
        RequestResult result = new RequestResult();
        WeedAssignedInfo assignedInfo = null;
        
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("File doesn't exist");
        }
        
        BufferedReader in = null;
        
        // 1. send assign request and get fid
        try {
            in = new BufferedReader(new InputStreamReader(sendHttpGetRequest("http://"
                    + this.masterAddress + ":" + this.masterPort + "/", "dir/assign",
                    "GET")));
            
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            
            Gson gson = new Gson();
            assignedInfo = gson.fromJson(response.toString(), WeedAssignedInfo.class);
            
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 2. send write request and volume server
        // TODO: add more file options;
        
        FileBody fileBody = new FileBody(inputFile, "text/plain");
        HttpClient client = new DefaultHttpClient();
        
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        
        HttpPost post = new HttpPost("http://" + assignedInfo.getPublicUrl() + "/"
                + assignedInfo.getFid());
        
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        entity.addPart("fileBody", fileBody);
        post.setEntity(entity);
        
        try {
            // TODO: add more file options;
            String response = EntityUtils.toString(client.execute(post).getEntity(),
                    "UTF-8");
            client.getConnectionManager().shutdown();
            int size = Integer.parseInt(response.substring(8, response.length() - 1));
            
            result.setFid(assignedInfo.getFid());
            result.setSize(size);
            result.setSuccess(true);
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /*
     * example: fid = 3,01637037d6 write file to local file
     */
    
    public RequestResult read(String fid, String path) {
        
        if (fid == null || fid.length() == 0) {
            throw new IllegalArgumentException("Fid cannot be empty");
        }
        
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        
        File output = new File(path);
        RequestResult result = new RequestResult();
        
        if (output.exists()) {
            throw new IllegalArgumentException("output file ");
        }
        
        String volumnId = fid.split(",")[0];
        ServerLocations locations = null;
        
        BufferedReader in = null;
        
        // 1. send quest to get volume address
        try {
            in = new BufferedReader(new InputStreamReader(sendHttpGetRequest("http://"
                    + this.masterAddress + ":" + this.masterPort + "/",
                    "dir/lookup?volumeId=" + volumnId, "GET")));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Gson gson = new Gson();
            locations = gson.fromJson(response.toString(), ServerLocations.class);
            
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 2. download the file
        BufferedOutputStream wr = null;
        try {
            InputStream input = sendHttpGetRequest("http://" + locations.getOnePublicUrl()
                    + "/", fid, "GET");
            
            output.createNewFile();
            wr =  new BufferedOutputStream(new FileOutputStream(output));

            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                wr.write(buffer, 0 , len);
            }
            result.setSuccess(true);
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        finally {
            try {
                if (in != null && wr != null) {
                    in.close();
                    wr.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /*
     * example: fid = 3,01637037d6 write file to local file
     */
    
    public InputStream read(String fid) {
        
        if (fid == null || fid.length() == 0) {
            throw new IllegalArgumentException("Fid cannot be empty");
        }
        
        String volumnId = fid.split(",")[0];
        ServerLocations locations = null;
        
        BufferedReader in = null;
        
        // 1. send quest to get volume address
        try {
            in = new BufferedReader(new InputStreamReader(sendHttpGetRequest("http://"
                    + this.masterAddress + ":" + this.masterPort + "/",
                    "dir/lookup?volumeId=" + volumnId, "GET")));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Gson gson = new Gson();
            locations = gson.fromJson(response.toString(), ServerLocations.class);
            
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 2. get input stream
        try {
            return sendHttpGetRequest("http://" + locations.getOnePublicUrl() + "/", fid,
                    "GET");
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    /*
     * delete the file
     */
    
    public RequestResult delete(String fid) {
        
        if (fid == null || fid.length() == 0) {
            throw new IllegalArgumentException("Fid cannot be empty");
        }
        
        RequestResult result = new RequestResult();
        
        String volumnId = fid.split(",")[0];
        ServerLocations locations = null;
        
        BufferedReader in = null;
        
        // 1. send quest to get volume address
        try {
            in = new BufferedReader(new InputStreamReader(sendHttpGetRequest("http://"
                    + this.masterAddress + ":" + this.masterPort + "/",
                    "dir/lookup?volumeId=" + volumnId, "GET")));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Gson gson = new Gson();
            locations = gson.fromJson(response.toString(), ServerLocations.class);
            
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 2. delete the file
        try {
            
            HttpURLConnection con = null;
            URL requestUrl = new URL("http://" + locations.getOnePublicUrl() + "/" + fid);
            con = (HttpURLConnection) requestUrl.openConnection();
            
            con.setRequestMethod("DELETE");
            
            // add request header
            con.setRequestProperty("User-Agent", "");
            int responseCode = con.getResponseCode();
            
            if (responseCode == 200) {
                result.setSuccess(true);
            }
            else {
                result.setSuccess(false);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        return result;
    }
    
    /*
     * Used to send request to WeedFS server
     */
    private InputStream sendHttpGetRequest(String host, String requestUrlDetail,
            String method) throws Exception {
        
        HttpURLConnection con = null;
        URL requestUrl = new URL(host.toString() + requestUrlDetail);
        con = (HttpURLConnection) requestUrl.openConnection();
        
        // optional default is GET
        con.setRequestMethod(method);
        
        // add request header
        con.setRequestProperty("User-Agent", "");
        int responseCode = con.getResponseCode();
        
        
        return con.getInputStream();
    }
    
    public static void main(String[] args) {
        RequestResult result = null;
        WeedFSClient client = new WeedFSClient("localhost", "9333");
        try {
            result = client.write("/WeedFS/test.data");
            client.read(result.getFid(), "/WeedFS/test.data1");
            client.delete(result.getFid());
            File file = new File("/WeedFS/test.data1");
            file.delete();
            client.read(result.getFid(), "/WeedFS/test.data1");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(result.toString());
    }
}
