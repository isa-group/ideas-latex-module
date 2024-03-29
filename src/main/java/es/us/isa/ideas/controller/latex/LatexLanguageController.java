package es.us.isa.ideas.controller.latex;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.us.isa.ideas.module.common.AppResponse;
import es.us.isa.ideas.module.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/language")
public class LatexLanguageController extends BaseLanguageController {

    public static final String OUTPUT_FOLDER="/Latex-OutputFolder";
    
    @RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse checkLanguage(String id, String content, String fileUri, HttpServletRequest request ) {

        AppResponse appResponse = new AppResponse();

        boolean problems = false;

        //System.out.println("CheckSyntax: " + res );
        appResponse.setFileUri(fileUri);

        if (problems) {
            appResponse.setStatus(Status.OK_PROBLEMS);
        } else {
            appResponse.setStatus(Status.OK);
        }

        return appResponse;
    }

    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content,HttpServletRequest request) {
        AppResponse appResponse = new AppResponse();

        return appResponse;
    }       
    
    @RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse executeOperation(String id, String content, String fileUri, String auxArg0, HttpServletRequest request) {
        AppResponse appResponse = constructBaseResponse(fileUri);
        if ("compileToPDF".equals(id)) {
            WorkspaceSync ws = new WorkspaceSync();
            String tempDirectory = ws.setTempDirectory();
            appResponse.setContext(tempDirectory);
            request.getSession().setAttribute("TempDirectory", tempDirectory);
        } else if ("doActualCompilation".equals(id)) {
            try {
                String tempDirectory = (String) request.getSession().getAttribute("TempDirectory");
                LatexCompiler compiler = new LatexCompiler();                
                String filePath=fileUri.substring(0,fileUri.lastIndexOf("\\")+fileUri.lastIndexOf("/")+1);           
                String file=fileUri;
                if(file.contains("/"))
                    file=generateRelativePath(file);
                LatexCompilationResult compilation = compiler.compile(file,filePath, tempDirectory, tempDirectory+OUTPUT_FOLDER, "PDF");
                appResponse.setHtmlMessage(compilation.generateHTMLMessage());
                if (!"".equals(compilation.getErrors())) {
                    appResponse.setStatus(Status.OK_PROBLEMS);
                }
                WorkspaceSync ws = new WorkspaceSync();
                ws.deleteTemp(tempDirectory, "pdf", fileUri);
            } catch (IOException ex) {
                appResponse.setStatus(Status.ERROR);
                Logger.getLogger(LatexLanguageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return appResponse;
    }

    // Analysis operations:
    private AppResponse constructBaseResponse(String fileUri) {
        AppResponse appResponse = new AppResponse();
        appResponse.setFileUri(fileUri);
        appResponse.setStatus(Status.OK);
        return appResponse;
    }

    private String generateRelativePath(String file) {
        StringBuilder result=new StringBuilder();
        String[] files=file.split("/");
        int index=2;
        if(files.length<2){
            index=1;
        }
        result.append(files[index]);
        for(int i=index+1;i<files.length;i++){
            result.append("/").append(files[i]);
        }
        return result.toString();
    }
   

}
