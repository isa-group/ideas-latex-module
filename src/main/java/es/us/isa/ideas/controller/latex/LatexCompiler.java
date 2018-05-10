/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.ideas.controller.latex;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author japarejo
 */
public class LatexCompiler {

    private String encoding = "UTF-8";
    private long maxTimeOut = 60000;
    private long waitTime = 1000;

    public static final String DEFAULT_OUTPUT_FORMAT = "PDF";

    /*public static Map<String, List<String>> compilationCommandsByOutput = new HashMap<>();

    static {
        compilationCommandsByOutput.put("PDF", Lists.newArrayList("pdflatex -output-directory"));
        compilationCommandsByOutput.put("HTML",Lists.newArrayList("htlatex -output-directory"));
        compilationCommandsByOutput.put("WORD",Lists.newArrayList("pdflatex -output-directory","pdf2word"));
        
    }*/
    
    public static Map<String, String> compilationCommandsByOutput = new HashMap<>();

    static {
        compilationCommandsByOutput.put("PDF", "pdflatex -output-directory");
        compilationCommandsByOutput.put("HTML", "htlatex -output-directory");        
        
    }

    public synchronized LatexCompilationResult compile(String file, String inputPath, String outputPath) throws IOException {
        return compile(file, inputPath, outputPath, DEFAULT_OUTPUT_FORMAT);
    }

    public synchronized LatexCompilationResult compile(String file, String inputPath, String outputPath, String outputFormat) throws IOException {
        LatexCompilationResult result = new LatexCompilationResult(file, inputPath, outputPath);
        List<String> originalFiles = generateNewFiles(outputPath, Collections.EMPTY_LIST);
        long start = System.currentTimeMillis();
        long current = start;
        File inputFile = new File(inputPath + "/" + file);
        File inputPathFile =new File(inputPath);
            if (inputFile.exists()) {
            
            String[] env = buildEnv();
            /*
            String[] commands = new String[compilationCommandsByOutput.get(outputFormat).size()];
            int i=0;
            for(String command:compilationCommandsByOutput.get(outputFormat)){
                commands[i]=command + " " + outputPath + " " + file;        
                i++;
            } **/  
            String command=compilationCommandsByOutput.get(outputFormat);
            String commands=command + " " + outputPath + " " + file;
            Process p = Runtime.getRuntime().exec(commands,env,inputPathFile);            
            while (p.isAlive() && current - start <= maxTimeOut ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LatexCompiler.class.getName()).log(Level.SEVERE, null, ex);
                }
                current = System.currentTimeMillis();
                if (current - start > maxTimeOut) {
                    p.destroy();
                }
            }
            result.setDuration(current - start);
            result.setExitCode(p.exitValue());
            result.setErrors(IOUtils.toString(p.getErrorStream(), encoding));
            result.setOutput(IOUtils.toString(p.getInputStream(), encoding));
            result.setOutputFiles(generateNewFiles(outputPath, originalFiles));
        } else {
            result.setDuration(0);
            result.setExitCode(-1);
            result.setErrors("The file " + inputFile.getAbsolutePath() + " does not exist!");
            result.setOutput("");
            result.setOutputFiles(Collections.EMPTY_LIST);
        }
        return result;
    }

    private List<String> generateNewFiles(String outputPath, List<String> originalFiles) {
        List<String> result = new ArrayList<>();
        File f = new File(outputPath);
        if (f.isDirectory()) {
            File[] listing = f.listFiles();
            for (File candidate : listing) {
                result.add(candidate.getAbsolutePath());
            }
        }
        result.removeAll(originalFiles);
        return result;
    }

    private String[] buildEnv() {
        String[] result={};
        ArrayList<String> env=new ArrayList<>();
        Map<String,String> envVars=System.getenv();
        for(String name:envVars.keySet()){
            env.add(name+"="+envVars.get(name));
        }
        return env.toArray(result);
    }
}
