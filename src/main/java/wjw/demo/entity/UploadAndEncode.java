package wjw.demo.entity;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;




import org.springframework.stereotype.Component;

import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;


@Component
public class UploadAndEncode {
       
	
	
	
	
	public static String SupportFormat = "mpeg,avi,rm,rmvb,mkv,dat,dvd,wmv,asf,mp4,flv,mpg";
	
	public static String defulatDir = "/home/";
	
	
	/***
	 * uploadFlag  used to  
	 */
	private boolean uploadFlag = false;
	
	/****
	   * dirEncode is the path of Encode floder
	   * which is used to save encoded videos
	   * dirTemp is the path of Temp floder
	   * which is used to save uploaded videos
	   */
	  
	  public static String dirEncode = "/home/encoded/";
	  
	  public static String dirTemp = "/home/temp/";
	  
	  /****
	   * dirFfmpeg and dirMencoder are the path of encode software
	   * 
	   */
	  
	  private String dirFfmpeg;
	  
	  private String dirMencoder;
	  	  
	  /***
	   * check whether the file exist in encodefloder
	   * 
	   * 
	   * @param filename
	   * @param dirEncode
	   * @return
	   */
	  public static boolean checkFileExistInEncoded(String filename)
	  {
		   if((new File(dirEncode+filename).exists()))
		   {
			  return true;   
		   }
		   else 
		   return false;		  
	  }
	  
	  
	  /****
	   * check whether the tempfloder has avaiable video
	   * 
	   *  返回文件名，
	   * 
	   * @return
	   */
	   public static String TempHasFile()
	   {   
		   File file = new File(dirTemp);
		   
		   if(file.isDirectory())
		   {   
			   if(!file.exists())
			   {
				   file.mkdir();   
			   }
			  
			   File[] filelist =  file.listFiles();
			   String path;
			   
			   for(File f : filelist)
			   {
				   path = f.getName();
				   if(SupportFormat.contains(
						                     (path.substring(path.lastIndexOf(".")+1,path.length())).toLowerCase()
						                    ))
				   {
					 return path;  
				   }
			   }
			   
			   return null;
		   }
		   else
		   {
			   System.out.println("the dirTemp "+dirTemp+" is not a dictory,change it to "+defulatDir);
			   dirTemp = defulatDir;
			   return null;
		   }
		  
	   }
	   
	   /***
	    * check the value of video_url in the database
	    * 函数会取字符串name的最后一个 . 前面的子串作为share_id
	    * @return
	    *  
	    */
	    public static String checkVideoUrl(String name) 
	    {   
	    	String checkshareId;
	    	if(name.contains(".")){
	    	 checkshareId = name.substring(0,name.lastIndexOf("."));
	    	}
	    	else {
	    	  checkshareId = name;	
	    	}
	    	
	    	/*
	    	 * 根据 ckeckshareId 返回 Video_url 键值  
	    	 */
	       
	    	//驱动
	    	String driver = "com.mysql.jdbc.Driver";   	
	    	// URL指向要访问的数据库名scutcs
	    	String url = "jdbc:mysql://10.113.196.33:3306/intern2015";
	    	// MySQL配置时的用户名
	    	String user = "intern_db";
	   	   // Java连接MySQL配置时的密码
	    	String password = "db_intern";
	    	
	    	// 加载驱动程序
	        try{
	    	
	    	
	    	try{
			Class.forName(driver);
	    	}catch(ClassNotFoundException e){
	    	     e.printStackTrace();
	    	}
	    	// 连续数据库
	    	java.sql.Connection conn = null;
			
				conn = DriverManager.getConnection(url, user, password);
			
	    	if(!conn.isClosed())
	    	System.out.println("Succeeded connecting to the Database!");
	    	// statement用来执行SQL语句
	    	java.sql.Statement statement = conn.createStatement();
	    	
	    	
	    	// 要执行的SQL语句
	    	String sql = "select video_url from share where s_id = "+checkshareId+";";
	    	
	    	
	    	ResultSet rs = null;
	    	
	    	
	    	rs = statement.executeQuery(sql);
	    	
	    	if(rs.next()){
	    	    
	    		
	    		String result = rs.getString(1);
	    		rs.close();
	    		conn.close();
	    		
	    		return result;
	    	}
	    	rs.close();
	    	conn.close();
	    	}catch(SQLException e ){
	    	    e.printStackTrace();	
	    	}
	    	
	    	return "error";
	    	
	    }
	    
	    /*****
	     * check if the encoded file is right
	     * 检查一下转码后的文件是否有效：
	     * 判断逻辑 很简单 检查文件的对应大小 如果小于  1024b 1kb 那么就返回 false
	     * 其他情况 返回 true
	     */
	    public static boolean checkIfEncodedSuccessed(String absoultepath){

	    	return (new File(absoultepath)).length()>1024?true:false;
	    			
	    }
	    
	    
	    
	   /****
	    * set the value of video_url in the database
	    * @return
	    * @throws ClassNotFoundException 
	    */
	    public static boolean setVideoUrl(String s,String filename)
	    {
	    	String checkshareId = filename.substring(0,filename.lastIndexOf("."));
	    	 	
	    	//驱动
	    	String driver = "com.mysql.jdbc.Driver";   	
	    	// URL指向要访问的数据库名scutcs
	    	String url = "jdbc:mysql://10.113.196.33:3306/intern2015";
	    	// MySQL配置时的用户名
	    	String user = "intern_db";
	   	   // Java连接MySQL配置时的密码
	    	String password = "db_intern";
	    	
	    	boolean rs = false;
	    	
	    	try{
	    	// 加载驱动程序
	    	try{
			Class.forName(driver);
	    	}catch(ClassNotFoundException e){
	    		e.printStackTrace();
	    	}
	    	// 连续数据库
	    	java.sql.Connection conn = null;
		
		    conn = DriverManager.getConnection(url, user, password);
			
	    	if(!conn.isClosed())
	    	System.out.println("Succeeded connecting to the Database!");
	    	// statement用来执行SQL语句
	    	java.sql.Statement statement = conn.createStatement();
			
			
		    statement = conn.createStatement();
			
	    	// 要执行的SQL语句
	    	String sql = "update share set video_url = '"+s+"' where s_id = "+checkshareId+";";
	    	
	    	System.out.println(sql);
	    	
	    	rs = statement.execute(sql);
	    	 
		    conn.close();
		    
	    	}catch(MySQLSyntaxErrorException e){
	    		e.printStackTrace();
	    		return false;
	    	}catch(SQLException e){
	    		e.printStackTrace();
	    		return false;
	    	}
	    	
	    	return rs;
	       
	    }
	   
	   /**
	    * delete a file from a floder
	    * @return
	    */
        public static boolean deleteFile(String absluatefilepath)
        { 
           return (new File(absluatefilepath)).delete();
        }
        
        /**
         * delete temp cache file of a share_id in temp 
         */
        public static void deleteTempCacheFile(String share_id){
        	
        	File[] filelist = (new File(UploadAndEncode.dirTemp)).listFiles();
        	
        	System.out.println("the number of files in temp floder is : "+filelist.length);
        	
        	for(File file : filelist){
        		String filename =  file.getName();
        		if(!filename.contains("_"))continue;
        		
        		String file_share_id = filename.substring(0, filename.indexOf("_"));
        		if((file_share_id.equals(share_id))&&filename.endsWith("temp")){
        		  	System.out.println("delete file: "+filename);
        			file.delete();
        		}
        		
        	}
        	
        	System.out.println("finish deleted temp cache");
        	
        }
        
	  
	   /****
	    * cmd 语句生成函数
	    * @return
	    */
        public static String commandGenerateWindows(String filename)
        {
        	return "cmd /c C:\\ffmpeg\\bin\\ffmpeg.exe  -i "+dirTemp+filename+" -acodec aac -vcodec libx264 -strict -2 -crf 31 -s 720x576 "+
                   dirEncode+filename.substring(0,filename.lastIndexOf("."))+".mp4";
        	/*return "ping www.baidu.com";*/
        }
        
        public static String commandGenerateLinux(String filename)
        {
        	return "sudo ffmpeg -i "+dirTemp+filename+" -f psp -acodec libfaac -ab 94k -vcodec libx264 -cqp 28 -coder 1 -refs 3 -deblockalpha 1 -deblockbeta -1 -me_method umh -subq 9 -me_range 32 -trellis 2 -chromaoffset -2 -nr 0 -bf 2 -b_strategy 1 -bframebias 0 -directpred 3 -g 250 -i_qfactor 1.3 -b_qfactor 1.4 -flags2 +bpyramid+wpred+mixed_refs+8x8dct -er 2 -s 720x576 "+
                    dirEncode+filename.substring(0,filename.lastIndexOf("."))+".mp4";
        }
        
        
      
        
        
        

	public boolean isUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(boolean uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getDirEncode() {
		return dirEncode;
	}

	public void setDirEncode(String dirEncode) {
		this.dirEncode = dirEncode;
	}

	public String getDirTemp() {
		return dirTemp;
	}

	public void setDirTemp(String dirTemp) {
		this.dirTemp = dirTemp;
	}

	public String getDirFfmpeg() {
		return dirFfmpeg;
	}

	public void setDirFfmpeg(String dirFfmpeg) {
		this.dirFfmpeg = dirFfmpeg;
	}

	public String getDirMencoder() {
		return dirMencoder;
	}

	public void setDirMencoder(String dirMencoder) {
		this.dirMencoder = dirMencoder;
	}  
	
}
