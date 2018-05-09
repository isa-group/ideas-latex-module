package es.us.isa.ideas.controller.latex;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public class WorkspaceSync {

    public static final String OUTPUT_FOLDER = "Latex-OutputFolder";       

    public String setTempDirectory() {
        String res = null;
        Path temp = null;
        Path p = null;
        try {
            temp = Files.createTempDirectory("LatexTemp");
            p = temp.resolve(OUTPUT_FOLDER);
            Files.createDirectories(p);//meterle al directorio temporal la carpeta de resultado
            res = temp.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }        
        return res;
    }
    

    public static String getProjectPath(String fileUri) {
        String path = "";
        String copy = new String(fileUri).replace('\\', '/');
        String[] parts = copy.split("/");
        if (parts.length >= 2) {
            path = parts[0] + "/" + parts[1];
        } else {
            path = null;
        }

        return path;
    }

    /*public static void moveFiles(String tempD,String fileUri){
     File tem= new File(tempD);
     File project= new File(getProjectPath(fileUri));
		
     try {
     FileUtils.copyDirectory(tem, project); //this method does not overwrite
     } catch (IOException e) {
				
     e.printStackTrace();
     }
		
     }*/
    /*private static Boolean tempEqualsProject(String tempD,String fileUri){
     File tem= new File(tempD);
     String pr= getProjectPath(fileUri);
     pr=pr.replace('/', '\\'); //TODO: is this really necessary?
     File old= new File(pr);
     String[] tempContent=tem.list();
     String[] oldContent= old.list();
		
     return oldContent.equals(tempContent)?true:false;
     }*/

    /*public static void endExecution(String tempD, String fileUri) {
     if(!tempEqualsProject(tempD, fileUri)){//TODO: probably this check is not needed
     moveFiles(tempD, fileUri); 
     }
     try {
     FileUtils.deleteDirectory(new File(tempD));
     } catch (IOException e) {
			
     e.printStackTrace();
     }
		
     }*/
    public static boolean deleteTemp(String temp,final String extension,String inputPath) throws IOException {
        boolean result=false;
        /*File f=new File(temp);
        File output=new File(inputPath);
        if(f.isDirectory() && f.exists() && output.isDirectory() && output.exists()){
            File[] candidates=f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith("."+extension.toLowerCase());
                }
            });
            for(File candidate:candidates){
                FileUtils.copyFileToDirectory(candidate, output);
            }        
            result=FileUtils.deleteQuietly(new File(temp));
        }*/
        return result;

    }
}
