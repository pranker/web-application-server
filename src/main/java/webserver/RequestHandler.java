package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			DataOutputStream dos = new DataOutputStream(out);
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader a = new BufferedReader(inputStreamReader);

			String line;
			String url="";

			while(!(line = a.readLine()).equals("")) {
				
				 if(line==null) {
					 break;
				 }
				 log.debug(line);
				 String[] goURL = line.split(" ");	 
				 if(goURL.length>=1){
					 if(goURL[1].equals("/index.html")){
						 url = goURL[1];
						 log.debug(goURL[1]);
						 break;
					 }
				 }
				 
				 if(goURL.length>=1){
					 if(goURL[1].equals("/user/form.html")){
						 url = goURL[1];
						 log.debug(goURL[1]);
						 break;
					 }
				 }

				 if(goURL.length>=1){
					 if(goURL[1].substring(0, 12).equals("/user/create")){
						 url = goURL[1].substring(5);
						 HttpRequestUtils util = new HttpRequestUtils();
						 Map map = util.parseQueryString(url);
						 Object[] ar = map.values().toArray();
						 
						 log.debug(ar[0].toString());
						 log.debug(ar[1].toString());
						 log.debug(ar[2].toString());
						 log.debug(ar[3].toString());
						 
						 User user = new User(ar[0].toString(),
								 ar[1].toString(),
								 ar[2].toString(),
								 ar[3].toString());
						 
							while(!(line = a.readLine()).equals("")) {
	
								 log.debug(line);	 
							}

						 
						 
						 break;
					 }
				 }
				 
				

			}
			
			byte[] body = Files.readAllBytes(new File("./webapp"+url).toPath());
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
