package com.dianping.phoenix.console.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class DeployGitService {
	
	private URL m_fromURL;
	private String m_toGitSshPath;
	private String m_localPath;
	
	private File m_localRootFile;
	
	private int BUFFER = 2048;
	
	public DeployGitService(URL fromURL,String toGitSshPath){
		this(fromURL,toGitSshPath,"/data/appdatas/phoenix");
	}
	
	public DeployGitService(URL fromURL,String toGitSshPath,String localPath){
		m_fromURL = fromURL;
		m_toGitSshPath = toGitSshPath;
		m_localPath = localPath;
	}
	
	private void init() throws Exception{
		m_localRootFile = new File(m_localPath);
		if(!m_localRootFile.exists()){
			m_localRootFile.mkdirs();
		}
		File gitPath = new File(m_localRootFile,"git/.git");
		if(!gitPath.exists()){
			Git.init().setDirectory(m_localRootFile).call();
		}
		
	}
	
	private void clear(){
		
	}
	
	private void uncompresseResource(File zFile,File parentDir){
		if(!parentDir.exists()){
			parentDir.mkdirs();
		}
		try {
            
            ZipFile zipFile = new ZipFile(zFile);
            Enumeration emu = zipFile.entries();
            int i=0;
            while(emu.hasMoreElements()){
                ZipEntry entry = (ZipEntry)emu.nextElement();
                //会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
                if (entry.isDirectory())
                {
                    new File(parentDir, entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(parentDir,  entry.getName());
                //加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
                //而这个文件所在的目录还没有出现过，所以要建出目录来。
                File parent = file.getParentFile();
                if(parent != null && (!parent.exists())){
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER);           
                
                int count;
                byte data[] = new byte[BUFFER];
                while ((count = bis.read(data, 0, BUFFER)) != -1)
                {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void loadResource(File destFile,URL resourceURL) throws IOException{
		if(!destFile.exists()){
			destFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(destFile);
		InputStream in = resourceURL.openStream();
		try{
			int count;
            byte data[] = new byte[BUFFER];
			while((count = in.read(data, 0, BUFFER)) != -1){
				fos.write(data, 0, count);
			}
		}finally{
			in.close();
			fos.close();
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		DeployGitService dgs = new DeployGitService(null,null);
		File file = new File("/data/phoenix/dpsf.war");
		URL url = new URL("http://192.168.8.45:8080/artifactory/repo2-cache/org/apache/activemq/activemq-web-console/5.4.2/activemq-web-console-5.4.2.war");
//		dgs.loadResource(file,url);
		dgs.uncompresseResource(file,new File("/data/phoenix/dpsf"));
	}
	
//	public static void main(String[] args) throws GitAPIException, IOException{
//		String path = "/data/phoenix";
//		String url = "git@github.com:firefox007/policedog.git";
//		File root = new File(path);
//        if(!root.exists())
//            root.mkdirs();
////        File gitF = new File(root,".git");
////        if(!gitF.exists()) {//如果已经初始化过,那肯定有.git文件夹
////                        //初始化git库,相当于命令行的 git init
////            Git.init().setDirectory(root).call();
////        }
////        Git git = Git.open(root); //打开git库
//        
////        FileRepositoryBuilder builder = new FileRepositoryBuilder();
////        Repository repository = builder.setGitDir(root).readEnvironment().findGitDir().build();
////
////        Git git = new Git(repository);              
////        CloneCommand clone = git.cloneRepository();
////        clone.setBare(false);
////        clone.setCloneAllBranches(true);
////        clone.setDirectory(root).setURI(url);
////        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider("firefox007", "19810816mxb");                
////        clone.setCredentialsProvider(user);
////        clone.call();
//        
//        Git.cloneRepository()
//        .setBare(false)
//        .setDirectory(root)
//        .setProgressMonitor(new ProgressMonitor(){
//
//			@Override
//			public void start(int totalTasks) {
//				// TODO Auto-generated method stub
//				System.out.println("start");
//			}
//
//			@Override
//			public void beginTask(String title, int totalWork) {
//				// TODO Auto-generated method stub
//				System.out.println("beginTast");
//			}
//
//			@Override
//			public void update(int completed) {
//				// TODO Auto-generated method stub
//				System.out.println("update");
//			}
//
//			@Override
//			public void endTask() {
//				// TODO Auto-generated method stub
//				System.out.println("endTask");
//			}
//
//			@Override
//			public boolean isCancelled() {
//				// TODO Auto-generated method stub
//				System.out.println("isCancelled");
//				return false;
//			}
//        	
//        })
//        .setCloneAllBranches(false)
//        .setRemote("origin")
//        .setURI(url)
//        .call();
//	}

}
