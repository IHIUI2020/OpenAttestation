/*
Copyright (c) 2012, Intel Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.intel.openAttestation.manifest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class AttestUtil {
	private static String manifestWSURL;
	private static String trustStorePath;
	private static String clientKeyStorePath;
	private static String clientTrustStorePassword;
	private static String clientKeyStorePassword;
	private static String configFile;
	
	public static void loadProperties(){
		FileInputStream manifestPropertyFile = null;
		try {
			Properties properties = new Properties();
			String path = AttestUtil.class.getClassLoader().getResource("/").getPath();
			
			manifestPropertyFile = new FileInputStream(path + configFile);
	    	properties.load(manifestPropertyFile);
			manifestWSURL = properties.getProperty("manifest_webservice_url");
			trustStorePath = properties.getProperty("truststore_path");
			clientKeyStorePath = properties.getProperty("keystore_path");
			clientTrustStorePassword = properties.getProperty("trust_store_password");
			clientKeyStorePassword = properties.getProperty("key_store_password");
			manifestPropertyFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  finally{
	    	   try {
                   if (manifestPropertyFile != null)	    	 	   
                	   manifestPropertyFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	       }
	}
		
	public static String getManifestWebServiceURL(){
		//get from .properties file
		return manifestWSURL;
	}
	
	public static String getTrustStorePath(){
		return trustStorePath;
	}
	
	public static void setConfigFile(String configFile) {
		AttestUtil.configFile = configFile;
	}

	public static WebResource getClient(String url){
		if (url.toLowerCase().startsWith("https")){
			//return new JerseyClient(url).getWebResource();

            System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            //Two-Way SSL authentication support
            if (clientKeyStorePath != null && !clientKeyStorePath.isEmpty())
            {
				System.setProperty("javax.net.ssl.trustStorePassword", clientTrustStorePassword); 
	            System.setProperty("javax.net.ssl.keyStore", clientKeyStorePath);
				System.setProperty("javax.net.ssl.keyStorePassword", clientKeyStorePassword);
				System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            }


		}//else{
			ClientConfig config = new DefaultClientConfig();
	        WebResource resource = Client.create(config).resource(url);
			return resource;
		//}
		
	}
	
	public static String getDateTimePattern(){
		return "yyyy-MM-dd HH:mm:ss";
	}
	 
}
