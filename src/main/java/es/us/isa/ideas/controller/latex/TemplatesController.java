
package es.us.isa.ideas.controller.latex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/template")
public class TemplatesController {
    
    private String extension=".tex";
    
    @RequestMapping("/document")
    @ResponseBody
    public List<TemplateDocument> getTemplateDocuments(HttpServletRequest request,HttpServletResponse respons){
        List<TemplateDocument> result=new ArrayList<TemplateDocument>();
        ServletContext sc = request.getSession().getServletContext();
        String realPath = sc.getRealPath("/WEB-INF/classes/repo/templates/documents");
        System.out.println("Searching for template documents on: "+realPath);
        File repo=new File(realPath);
        TemplateDocument td=null;
        List<String> dependences=null;
        if(repo.exists() && repo.isDirectory()){
            File [] templates=repo.listFiles();
            for(File f:templates){
                if(f.getName().endsWith(extension)){                    
                  dependences=generateDependencesFromFiles(f.getName().replace(extension,""),templates);
                  td=new TemplateDocument(f.getName(),dependences);                  
                  result.add(td);
                }
            }
        }
        return result;
    }   
    
    @RequestMapping("/dependences/document/{name:.+}")
    @ResponseBody
    public List<String> getDependences(@PathVariable("name") String name,HttpServletRequest request,HttpServletResponse response){
        List<String> result=new ArrayList<>();
        ServletContext sc = request.getSession().getServletContext();
        String realPath = sc.getRealPath("/WEB-INF/classes/repo/templates/documents");
        System.out.println("Searching for template documents on: "+realPath);
        File repo=new File(realPath);
        if(repo.exists() && repo.isDirectory()){
            File [] templates=repo.listFiles();
            result=generateDependencesFromFiles(name.replace(extension,""),templates);
        }
        return result;
    }
    
    private List<String> generateDependencesFromFiles(String name, File[] templates){
        List<String> result=new ArrayList<>();
        for(File f:templates){
            if(f.getName().contains(name) && !f.getName().endsWith(extension))
                result.add(f.getName());
        }
        return result;
    }
    
    @RequestMapping("/document/{name:.+}")
    @ResponseBody
    public String getTemplateDocuments(@PathVariable("name") String name,HttpServletRequest request,HttpServletResponse response){
        String result="";
        ServletContext sc = request.getSession().getServletContext();
        String realPath = sc.getRealPath("/WEB-INF/classes/repo/templates/documents/"+name);
        try {        
            result=FileUtils.readFileToString(new File(realPath));
        } catch (IOException ex) {
            Logger.getLogger(TemplatesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
        
    class TemplateDocument {
        String name;
        List<String> dependences;
        public TemplateDocument(String name,List<String> dependences) {
            this.name=name;
            this.dependences=dependences;
        }
        public TemplateDocument(String name) {
            this(name,Collections.EMPTY_LIST);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }                        

        public List<String> getDependences() {
            return dependences;
        }
        
        
    }
    
    @RequestMapping("/project")
    @ResponseBody
    public List<TemplateProject> getTemplateProjects(HttpServletRequest request,HttpServletResponse respons){
        List<TemplateProject> result=new ArrayList<>();
        ServletContext sc = request.getSession().getServletContext();
        String realPath = sc.getRealPath("/WEB-INF/classes/repo/templates/projects");
        System.out.println("Searching for template documents on: "+realPath);
        File repo=new File(realPath);
        TemplateProject td=null;
        List<String> dependences=null;
        if(repo.exists() && repo.isDirectory()){
            File [] templates=repo.listFiles();
            for(File f:templates){
                if(f.isDirectory()){                    
                  dependences=generateDependencesFromFiles(f.getName().replace(extension,""),templates);
                  td=new TemplateProject(f.getName(),"");                  
                  result.add(td);
                }
            }
        }
        return result;
    }  
    
    class TemplateProject {
        String name;
        String description;

        public TemplateProject(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
            
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
                        
    }
}
