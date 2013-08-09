package net.weedfs.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/*
 *  WeedFSClient test
 */

public class WeedFSClientTest {
    
    @Before
    public void setUp() {
        // should setup weedfs service.
    }
    
    @After
    public void tearDown() {
        // should shutdown weedfs service.
    }
    
    @Test
    public void testRead() {
        // create File
        int fileNum = 10;
        Random ran = new Random();
        
        File[] fileList = new File[fileNum];
        BufferedWriter wr = null;
        
        for (int i = 0; i < fileNum; i++) {
            fileList[i] = new File("./testFile" + i);
            try {
                fileList[i].createNewFile();
                wr = new BufferedWriter(new FileWriter(fileList[i]));
                
                int sizeRange = ran.nextInt(7);
                byte[] tempData = new byte[(int) Math.pow(10.0, sizeRange)];
                ran.nextBytes(tempData);
                String writeData = new String(tempData);
                wr.write(writeData.toString());
            }
            catch (IOException e) {
                Assert.fail("create file failed");
            }
            finally {
                try {
                    if (wr != null)
                        wr.close();
                }
                catch (IOException e) {
                    Assert.fail(e.toString());
                }
            }
        }
        
        WeedFSClient client = new WeedFSClient("localhost", "9333");
        RequestResult[] result = new RequestResult[fileNum];
        
        for (int i = 0; i < fileNum; i++) {
            result[i] = client.write(fileList[i].getAbsolutePath());
            Assert.assertTrue(result[i].isSuccess());
        }
        
        // download files;
        
        File[] verfiedFileList = new File[fileNum];
        try {
            for (int i = 0; i < fileNum; i++) {
                verfiedFileList[i] = new File("./downloadFile" + i);
                client.read(result[i].getFid(), verfiedFileList[i].getAbsolutePath());
                
                BufferedReader rd1 = new BufferedReader(new FileReader(fileList[i]));
                BufferedReader rd2 = new BufferedReader(
                        new FileReader(verfiedFileList[i]));
                
                String temp1 = null;
                String temp2 = null;
                
                while ((temp1 = rd1.readLine()) != null
                        && (temp2 = rd2.readLine()) != null) {
                    Assert.assertEquals(temp1, temp2);
                }
            }
        }
        catch (Exception e) {
            Assert.fail("create file failed:" + e.toString());
        }
        
        try {
            for (int i = 0; i < fileNum; i++) {
                verfiedFileList[i].delete();
                fileList[i].delete();
                client.delete(result[i].getFid());
            }
        }
        catch (Exception e) {
            Assert.fail("create file failed:" + e.toString());
        }
    }
}
