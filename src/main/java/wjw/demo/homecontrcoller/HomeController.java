package wjw.demo.homecontrcoller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.LastModified;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import wjw.demo.common.MultipartFileSender.MultipartFileSender;
import wjw.demo.common.encodethread.EncodeThread;
import wjw.demo.common.msg.encodeMsg;
import wjw.demo.entity.UploadAndEncode;
import wjw.demo.entity.user;

@Controller
class HomeController {
    
	private static ExecutorService singleService = Executors.newSingleThreadExecutor(); 
	
	private static Map<String,Thread> watchinglist = new HashMap<>();
	
	private static int watchNumber = 0;
	
	@RequestMapping("video1") 
	String video(){
		return "video";
	}
	
	@RequestMapping("/") 
	String index(Map<String, Object> model ,HttpServletRequest request) throws SQLException{ 
		 
		 HttpSession sss = request.getSession();
		
		 if(sss.getAttribute("user_state") != null)
		 {
			 	System.out.println("stage/:"+sss.getId());
			    String name = (String)sss.getAttribute("name");
                String id =  (String)sss.getAttribute("id");			    
			    /***************/
                         
			    //驱动
		    	String driver = "com.mysql.jdbc.Driver";   	
		    	// URL指向要访问的数据库名scutcs
		    	String url = "jdbc:mysql://10.113.196.33:3306/intern2015";
		    	// MySQL配置时的用户名
		    	String user = "intern_db";
		   	   // Java连接MySQL配置时的密码
		    	String password = "db_intern";
		    	
		    	// 加载驱动程序
		    	try {
					Class.forName(driver);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	// 连续数据库
		    	java.sql.Connection conn = null;
				try {
					conn = DriverManager.getConnection(url, user, password);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	if(!conn.isClosed())
		    	System.out.println("Succeeded connecting to the Database!");
		    	// statement用来执行SQL语句
		    	java.sql.Statement statement = conn.createStatement();
		    	// 要执行的SQL语句
		    	String sql = "select * from wjw_user ;";
		    
		    	
		    	
		    	ResultSet rs = statement.executeQuery(sql);
		    	
		    	List<user> list = new ArrayList<user>();	
		    	
		    	while(rs.next())
		    	{   
		    		user u = new user();
		    	    u.setUser_id(rs.getString(2));
		    	    u.setUser_name(rs.getString(1));
		    	    list.add(u);
		    	    
		    	}
		    	user u = new  user();
		    	
		    	rs.close();  
		    	conn.close();
		    	
		    	u.setUser_id(id);
		    	u.setUser_name(name);
		    	
		    	model.put("ukey",u);
		    	model.put("alluser", list);
		    	
		    	
		    	
			 return  "index2";
		 }
		 System.out.println("outstage/:"+sss.getId());
		 
		 
		 
		 int statues = 1;  
		 
		 model.put("state", statues);
		 
		 return "index";
	 }

	@RequestMapping("register")
	String register()
	{
		return "index3";
	}
	@RequestMapping("inster")
	String inster(HttpServletRequest request,Model model) throws SQLException
	{
		
		String name = request.getParameter("name");
    	String id = request.getParameter("id");
    	int states = 1;
    	
    	
    	//驱动
    	String driver = "com.mysql.jdbc.Driver";   	
    	// URL指向要访问的数据库名scutcs
    	String url = "jdbc:mysql://10.113.196.33:3306/intern2015";
    	// MySQL配置时的用户名
    	String user = "intern_db";
   	   // Java连接MySQL配置时的密码
    	String password = "db_intern";
    	
    	// 加载驱动程序
    	try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// 连续数据库
    	java.sql.Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!conn.isClosed())
    	System.out.println("Succeeded connecting to the Database!");
    	// statement用来执行SQL语句
    	java.sql.Statement statement = conn.createStatement();
    	// 要执行的SQL语句
    	String sql = "insert into wjw_user ( name, id) values ( '"+name+"', '"+id+"');";
    	
    	
    	try{
    	  statement.execute(sql);
    	}catch(SQLException e){
    		System.out.println("error");
    		states = 2;
    		
    	}
    	
    	
    	
    	
        model.addAttribute("state",states);
    	
        conn.close(); 
		
		return "index1";
	}
	
	
	
	@RequestMapping("upload1")
	String upload(){
		return "demo";
	}
   
    
    @RequestMapping("wjw") 
    String wjw(HttpServletRequest request,HttpServletResponse response ,Model model) throws IOException, SQLException {

    	String name = request.getParameter("name");
    	String id = request.getParameter("id");
    	HttpSession sss = request.getSession();
    	int states = 1;
    	//驱动
    	String driver = "com.mysql.jdbc.Driver";   	
    	// URL指向要访问的数据库名scutcs
    	String url = "jdbc:mysql://10.113.196.33:3306/intern2015";
    	// MySQL配置时的用户名
    	String user = "intern_db";
   	   // Java连接MySQL配置时的密码
    	String password = "db_intern";
    	
    	// 加载驱动程序
    	try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// 连续数据库
    	java.sql.Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!conn.isClosed())
    	System.out.println("Succeeded connecting to the Database!");
    	// statement用来执行SQL语句
    	java.sql.Statement statement = conn.createStatement();
    	// 要执行的SQL语句
    	String sql = "select * from wjw_user where name = '"+name+"' and id = '"+id+"';";
    	
    	
    	ResultSet rs = null;
    	
    	try{
    	rs = statement.executeQuery(sql);
    	}catch(SQLException e)
    	{
    		System.out.println(sql);
    		e.printStackTrace();
    	}
    	
    	if(!rs.next())
    	{ 
    	  rs.close();  
          conn.close();
          states = 0;
          model.addAttribute("state",states);
          
    	  return "index1";
    	}
    	
    	/***
    	 * 登录成功  添加session
    	 */
    	
    	sss.setAttribute("user_state", true);
    	sss.setAttribute("name", name);
    	sss.setAttribute("id", id);

    	
    	/*****
    	 *  读取出所有用户
    	 */
	
    	sql = "select * from wjw_user ;";
    	
    	rs = statement.executeQuery(sql);
    	
    	
    	
    	
    	List<user> list = new ArrayList<user>();
    	
    	
    	while(rs.next())
    	{   
    		user u = new user();
    	    u.setUser_id(rs.getString(2));
    	    u.setUser_name(rs.getString(1));
    	    list.add(u);
    	    
    	}
    	
    	
    	rs.close();  
    	conn.close();  
    	
    	user u = new user();
    	
    	u.setUser_name(name);
    	u.setUser_id(id);
    	
    	
    	System.out.println(list);
    	
    	model.addAttribute("ukey",u);
    	model.addAttribute("alluser", list);
    	model.addAttribute("state",states);
    	
		return "index2";
    }
    
    @RequestMapping("logout")
    String logout(HttpServletRequest request)
    {
    	HttpSession sss = request.getSession();
    	
    	sss.removeAttribute("user_state");
    	return "index1"; 
    }
   
   
    
   /******
    *    针对文件上传 新添加的东西
    ******/
    
    @RequestMapping("plupload")
    public String plupload(@RequestParam("share_id") String share_id ,
    		               @RequestParam("u_id") String u_id,
    		               @RequestParam("part_id") String part_id,
    		               Model model)
    {   
       	System.out.println(share_id+":"+u_id+":"+part_id);
    	model.addAttribute("share_id",share_id);
    	model.addAttribute("u_id", u_id);
    	model.addAttribute("part_id", part_id);
    	
    	return "plupload_view";
    }
    
    
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {  
        return "You can upload a file by posting to this same URL.";  
    }  
   
    @RequestMapping(value="/upload", method=RequestMethod.POST)  
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name,  
            @RequestParam("file") MultipartFile file) throws FileNotFoundException{  
        if (!file.isEmpty()) {  
             
        	 
        	BufferedOutputStream stream =  
                    new BufferedOutputStream(new FileOutputStream(new File(UploadAndEncode.dirTemp+name)));  
            try {  
                
            	InputStream input = file.getInputStream();
            	BufferedInputStream bufferInput = new BufferedInputStream(input);
            	byte [] bytes = new byte[64*1024*1024];
            	int len = bufferInput.read(bytes);
            	while(len > 0){
            		stream.write(bytes, 0, len);
            		System.out.println("have receive :"+len);
            		len = bufferInput.read(bytes);
            	}
            	     
                stream.close();  
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";  
            } catch (Exception e) {  
                return "You failed to upload " + name + " => " + e.getMessage();  
            }  
        } else {  
            return "You failed to upload " + name + " because the file was empty.";  
        }  
    }  
    
    
    //------------------------------------------------------         ---------------------------------//
    //------------------------------------------------------新增 断点 续传 ---------------------------------//
    //------------------------------------------------------        ---------------------------------//
    //------------------------------------------------------        ---------------------------------// 
    
   /****
    *   分片版  ------  取一个分片，返回一次
    */
    @RequestMapping(value="/uploadchunks", method=RequestMethod.GET)
    public @ResponseBody String provideUploadChunkInfo() {  
        return "You can upload a file by posting to this same URL.";  
    } 
    
    
    @RequestMapping(value="/uploadchunks", method=RequestMethod.POST)  
    public @ResponseBody String handleFileUploadChunk(
    		@RequestParam("name") String name,
    		@RequestParam("chunk") int chunk,
    		@RequestParam("chunks") int chunks,
    		@RequestParam("share_id") String share_id,
    		@RequestParam("u_id") String u_id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part_id") String part_id
           ) throws IOException{ 
    	   
    	    //System.out.println("123123");
    	    // 支持跨域访问
    	    
    	    // 将文件名改成标准 缓存文件的 格式 ： {share_id}_{u_id}_{lastModified}_{name}_temp
    	    String tempname = share_id+"_"+u_id+"_"+name+"_temp";
    	    System.out.println("tempname : "+ tempname);
    	    
        if (!file.isEmpty()) {  
        	
	        System.out.print("original name: "+name);
	       	        
	        System.out.println(" changed to: "+tempname);
        	
        	System.out.println("name: "+name+" chunks = "+chunks+" chunk = "+chunk);
        	
            /** 打开temp 文件夹 **/
        	File dictory = new File(UploadAndEncode.dirTemp);
        	
        	if(!dictory.exists()){
        		dictory.mkdirs();
        	}
        	
        	RandomAccessFile writer = new RandomAccessFile(UploadAndEncode.dirTemp+tempname, "rw"); 
        	 
        	long fileLength = writer.length();  
        	writer.seek(fileLength);
        	
        	
            try {  
                
            	InputStream input = file.getInputStream();
            	BufferedInputStream bufferInput = new BufferedInputStream(input);
            	byte [] bytes = new byte[100*1024*1024];
            	int len = bufferInput.read(bytes);
            	while(len > 0){
            		
            		writer.write(bytes,0, len);
            		
            		System.out.println("have receive :"+len);
            		len = bufferInput.read(bytes);
            		
            	}
                
            	writer.close();
            	
                
            	if(chunk == chunks-1){//最后一片接受成功
            		
                File oldFile = new File(UploadAndEncode.dirTemp+tempname); 
                /*****
                 * 
                 *  上传完成后的 视频 的命名 格式 为  {share_id}.xxx
                 * 
                 * 
                 ******/
                
                // 注意 格式是必须被 考虑的问题 如果文件没有小数点的话 下一句话只能阻塞进程
                
                File finishFile = new File(UploadAndEncode.dirTemp+share_id+name.substring(
                		                                                                   Math.max(0,name.lastIndexOf("."))
                		                                                                   )
                                           );
                
                if(finishFile.exists()){//已有文件上传成功 本次上传的文件要被删除
                	 String ifencoding = UploadAndEncode.checkVideoUrl(share_id+".xxx");
                	 
                	 if(ifencoding.startsWith("encoding")||ifencoding.startsWith("encoded_successed")){
                		 /**
                		  *  之前 别人 上传的关于 share_id 的 视频 正在转码 ，我们不能删除它，所以，我们只有放弃当前的视频
                		  */
                		 oldFile.delete();
                	 }
                	 else {
                		 /** 
                		  *  覆盖 原视频 
                		  */
                		 finishFile.delete();
                		 oldFile.renameTo(finishFile);
                		 
                	 }
                }
                else {
                	oldFile.renameTo(finishFile);
                }
            	  
            	/*** 更新数据库 ****/
            	UploadAndEncode.setVideoUrl("uploaded:"+u_id+":"+part_id, share_id+".xxx");
            	
            	/*** 删除缓存文件 ***/
            	UploadAndEncode.deleteTempCacheFile(share_id);
            	
            	
                /*** 开启一个线程处理 视频转码 ***/
                EncodeThread thread = new EncodeThread();
                singleService.submit(thread);
              }  
              
               //取消 uploading 状态
                        
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";  
            } catch (Exception e) {  
                return "You failed to upload " + name + " => " + e.getMessage();  
            }  
        } else {  
            return "You failed to upload " + name + " because the file was empty.";  
        }  
    }  
    
    //--------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------//
    @RequestMapping(value="/checkplupload") 
    public @ResponseBody String checkUploadFile(
    	   @RequestParam("fileName") String name,
    	   @RequestParam("chunk_size") Long chunk_size,
    	   @RequestParam("file_size") Long file_size,
    	   @RequestParam("share_id") String share_id,
    	   @RequestParam("u_id") String u_id,
    	   @RequestParam("chunks") int chunks){
    	   
    	
    	   System.out.println("filename: "+name+" chunks = "+chunks+" chunk_size = "+chunk_size);
    	   System.out.println("file_size: "+file_size);
    	
    	   String tempname = share_id+"_"+u_id+"_"+name+"_temp";
    	   
    	   
    	   Long numberOfrevicechunks ;
    	   
    	   //open 缓冲文件 temp
    	   File file = new File(UploadAndEncode.dirTemp+tempname);
    	   
    	   
    	   // file doesn't exist
    	   if(!file.exists()){
    		   numberOfrevicechunks = (long) 0;
    	   }
    	   else {
    		   
    		   if(file.length() >= file_size)
    			  numberOfrevicechunks = chunks*chunk_size ;
    		   else
    		   numberOfrevicechunks = file.length();
    	   }
    	   	   
    	   return numberOfrevicechunks+"";
    }
    
    //---------------------------------------         ---------------------------------//
    //---------------------------------------新增 断点 续传 ---------------------------------//
    //---------------------------------------        ---------------------------------//
    //---------------------------------------        ---------------------------------//
    
    
    /****
     *   新增观看视频模块
     */
    @RequestMapping("watch")
    public void play_video( HttpServletRequest req , HttpServletResponse res ,
    		                 @RequestParam String share_id) throws Exception {   
    	
    	System.out.println("request video:" + share_id);	
    	
    	String videoUrl = UploadAndEncode.checkVideoUrl(share_id);
    	if(!videoUrl.startsWith("encoded_successed")){
    		res.sendError(HttpServletResponse.SC_NOT_FOUND);
    		return ;
    	}
    	
    	File f = new File(UploadAndEncode.dirEncode+share_id+".mp4");
    	
    	MultipartFileSender
    	.fromFile(f)
    	.with(req)
    	.with(res)
    	.serveResource();
    	
    	System.out.println("over");
    	
    }
    
    /******
     *  新增  上传文件删除 功能
     */
    @RequestMapping("delete")
    public void delete( @RequestParam String share_id,
    		            @RequestParam String u_id){
    	
    	System.out.println("delete video : "+ share_id);
    	System.out.println("u_id : "+u_id);
    	
    	String filename = share_id+".mp4";
    	
    	String videoUrl = UploadAndEncode.checkVideoUrl(filename);
    	
    	
    	
    	/*** 完成状态的 state 格式  应为 
    	 *   encoded_successed:上传者的u_id:上传者所属part长的u_id
    	 */
    	System.out.println("video state : "+videoUrl);
    	
    	
    	/* caution
    	 *  
    	 * 因为需求变更  后台不在做身份检测  所以下面的代码注释
    	 */
    	/*
    	// check if is the right person 
    	if(!videoUrl.startsWith("encoded_successed")||!videoUrl.contains(u_id)){
    		System.out.println("person info error: break off");
    		return ;
    	}
    	
    	// 开始 删除 video 信息 与 相关 文件  
    	
    	if(!UploadAndEncode.setVideoUrl("file_deleted",filename)){
    		 
    		 System.out.println("delete file failed!");
    		 
    	}
    	
    	*/
    	
    	
    	/** 删除 文件 **/
    	
    	while(!UploadAndEncode.deleteFile(UploadAndEncode.dirEncode+filename)){
    		System.out.println("delete file failed,try again!");
    		try {
				  Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
            	
    	return ;
    }
    
    //--------------------------------------------------------
    
    /** 测试 日历 表格 **/
    
    @RequestMapping("scheduler")
    String toScheduler(){
    	
    	return "scheduler";
    	
    }
    
    
}
