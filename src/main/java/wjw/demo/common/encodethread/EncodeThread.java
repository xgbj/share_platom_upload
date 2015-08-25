package wjw.demo.common.encodethread;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import wjw.demo.common.msg.encodeMsg;
import wjw.demo.entity.UploadAndEncode;

public class EncodeThread implements Runnable{
	
	/****
	 * 
	 * 命令行执行函数
	 * 
	 */
	public static void exeCmd(String commandStr) {  
		
		System.out.println("cmd start");
        BufferedReader br = null;  
        try {  
            Process p = Runtime.getRuntime().exec(commandStr);  
            
            final InputStream is1 = p.getInputStream();
            
            new Thread(new Runnable() {
                public void run() {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                    try{
                    while(br.readLine() != null) ;
                    }
                    catch(Exception e) {
                      e.printStackTrace();
                    }
                }
            }).start(); // 启动单独的线程来清空p.getInputStream()的缓冲区
            
            InputStream is2 = p.getErrorStream();
            
            br = new BufferedReader(new InputStreamReader(is2));  
            String line = null;  
            StringBuilder sb = new StringBuilder();  
            while ((line = br.readLine()) != null) {  
               // sb.append(line + "\n");
                encodeMsg.encodeMsg = line;              
                System.out.println(line);
            }  
            
            /** 这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行   **/
            p.waitFor();
            //System.out.println(sb.toString());
           
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        finally  
        {  
            if (br != null)  
            {  
                try {  
                    br.close();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                } 
               
            }  
            System.out.println("cmd end");
        }  
    } 
	
	public String encode(String result){
		
		 
		String encodedresult = result.substring(0,result.lastIndexOf("."))+".mp4";
		
		/*****
		 *  开始进行转码,调用命令行
		 */
		  String cmd = UploadAndEncode.commandGenerateLinux(result);
		  System.out.println(cmd);
		 
		  //执行命令行
		  exeCmd(cmd);
		  /***
		   *  目前没有比较理想的方法去判定一个命令行运行的结果是什么
		   *  java 可以获取 命令行 打印的输出流  但单凭输出流 不太好判断这次
		   *  转码的结果到底是怎样的，
		   *  暂时的解决方案是 转码后检查 encoded 文件是否存在于 文件夹中 做一个 简略的判断
		   */
		   //如果encoded 文件中没有想要的文件· 则转码失败 
		   if(!UploadAndEncode.checkFileExistInEncoded(encodedresult)){
			   
			   //删除文件	   
			   UploadAndEncode.deleteFile(UploadAndEncode.dirTemp+result);
			   return "error:encoded failed";
		   }
		   else if(!UploadAndEncode.checkIfEncodedSuccessed(UploadAndEncode.dirEncode+encodedresult)){//如果转码后的文件大小 == 0 的话 转码失败
			   
			   //删除文件
			   UploadAndEncode.deleteFile(UploadAndEncode.dirEncode+encodedresult);
			   UploadAndEncode.deleteFile(UploadAndEncode.dirTemp+result);
			   return "error:encoded failed";
			   
		   }
		   
		   // 否则 转码成功 ，删除temp文件夹中 的文件
		   UploadAndEncode.deleteFile(UploadAndEncode.dirTemp+result);
		   
		   return UploadAndEncode.dirEncode+encodedresult;
		
	}
   
	
	public void run(){
		  
		 System.out.println("starting encode"); 
		  /* 
		   *  先检查路径 若不存在则新建
		   */
		  File dictory = new File(UploadAndEncode.dirTemp);  
		  if(!dictory.exists()){
			  dictory.mkdir();
		  }
		  dictory = new File(UploadAndEncode.dirEncode);
		  if(!dictory.exists()){
			  dictory.mkdir();
		  }
		  
		  
		  while(true){
		  
			  
			/**
			 *  开始转码
			 */
			  
        	/****
		     * 检查 TempFloder 中是否有可支持的转码 对象
	     	 * 
		   	 */			
	    	String filename = UploadAndEncode.TempHasFile();
						 
		    if(filename == null)
			{   
		    	System.out.println("finish encode");
				return ;
			}
			
			System.out.println(filename);
	        
			/***
			 *  转码成功后的视频格式 还是应该为 share_id.mp4
			 */
			
			String encodedfilename = filename.substring(0,filename.lastIndexOf("."))+".mp4";
			
			String videoUrl = UploadAndEncode.checkVideoUrl(filename);
			/****
			 * 检查 EncodedFloder 中是否有同名的转码后视频
		     */	 
		    if(UploadAndEncode.checkFileExistInEncoded(encodedfilename))
			{
			  /***
			   *  Encoded 文件夹下 存在 同名的已转码文件 , 检查 videoUrl
			   *  从 Temp 文件夹下删除缓存文件，更新数据库 , 跳过本次转码进程 
		       */
		      System.out.println("encodedFloder exists file: "+encodedfilename+"\n check videoUrl....");	
		     	     
		      if(videoUrl.startsWith("encoding")||videoUrl.startsWith("encoded_failed")){ // 说明 是 转码 中 出了 问题 这种情况下应删除 转码后的视频  然后跳过这个 视频
		    	 
		    	 UploadAndEncode.deleteFile(UploadAndEncode.dirEncode+encodedfilename);
		    	 
		    	 UploadAndEncode.setVideoUrl(videoUrl.replace("encoding", "uploaded"), filename);
		    	 
		      }
		      else if(videoUrl.startsWith("encoded_successed")){// 说明是转码后出了问题  这种情况 应该删除 转码前的视频 然后 跳过这个流程
		    	 
		    	 UploadAndEncode.deleteFile(UploadAndEncode.dirTemp+filename);
		    	 
		    	 
		      }
		      else if(videoUrl.startsWith("file_deleted")){// 说明删除的时候并没有成功的删除视频
		    	  
		    	  UploadAndEncode.deleteFile(UploadAndEncode.dirEncode+encodedfilename);
		      }
		      
		      continue;
					 
			}
		    
            String videoUrl_encoding = videoUrl.replace("uploaded", "encoding");
            			 
		    System.out.println(videoUrl+" >>>>>>change to>>>>> "+ videoUrl_encoding);
		    // 切换视频状态  至  转码 中
		    UploadAndEncode.setVideoUrl(videoUrl_encoding, filename);
			  
		    // 开始 转码 。。。。
		    String result = encode(filename);
		  
		    System.out.println("encode result: "+result);
		  
		      /***
		       *  error : 转码失败
		       */
		     if(result.startsWith("error")){
			  
			  
				System.out.println("seting database video_url:file_deleted_encoded_failed");
				
				// 转码失败 同时删除 转码前 与 转码后的视频 
				UploadAndEncode.deleteFile(UploadAndEncode.dirEncode+encodedfilename);
				UploadAndEncode.deleteFile(UploadAndEncode.dirTemp+filename);
				
				UploadAndEncode.setVideoUrl(videoUrl_encoding.replace("encoding", "encoded_failed"),filename);
			
			  
		     }
		
		      /***
		       *  success : 成功完成一个文件的转码 
		       */
		      else {
			       UploadAndEncode.setVideoUrl(videoUrl_encoding.replace("encoding", "encoded_successed"),filename);
			           
		      }
		  
		  }  
		
	}

}
