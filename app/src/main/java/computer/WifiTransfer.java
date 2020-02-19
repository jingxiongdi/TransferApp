package computer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiTransfer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("WifiTransfer start");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					createServerSocket();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void createServerSocket() throws Exception {
		System.out.println("createServerSocket----------");
		ServerSocket ss = new ServerSocket(8888);
		while (true){
			System.out.println("getInputStream----------");
			Socket socket = ss.accept();
			InputStream in = socket.getInputStream();
			int content;
			//装载文件名的数组
			byte[] c = new byte[1024];
			//解析流中的文件名
			for(int i=0;(content = in.read())!=-1;i++) {
				//表示文件名已经读取完毕
				if(content=='#'){
					break;
				}
				c[i] = (byte) content;
			}

			//得到文件名
			String fileName = new String(c,"utf-8").trim();
			System.out.println("fileName----------"+fileName);
			File savedFile = new File(fileName);
			if(savedFile.exists()){
				savedFile = new File("1"+fileName);
			}
			savedFile.createNewFile();
			OutputStream outputStream = new FileOutputStream(savedFile);
			byte[] buf = new byte[1024];
			int len;
			//判断是否读到文件末尾
			while ((len = in.read(buf))!=-1){
				outputStream.write(buf,0,len);
			}
			outputStream.flush();
			outputStream.close();
			System.out.println("file trans success----------");
			OutputStream outputStream2 = socket.getOutputStream();
			outputStream2.write("save_success".getBytes());
			outputStream2.flush();
			outputStream2.close();
			socket.close();
			System.out.println("send callback success----------");
			break;
		}
		ss.close();

	}

}
