/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.ideas.controller.latex;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private long maxTimeOutPerCommand = 10000;
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
        compilationCommandsByOutput.put("PDF", "pdflatex ");
        compilationCommandsByOutput.put("HTML", "htlatex ");

    }

    public synchronized LatexCompilationResult compile(String file, String filePath, String inputPath, String outputPath) throws IOException {
        return compile(file, filePath, inputPath, outputPath, DEFAULT_OUTPUT_FORMAT);
    }

    public synchronized LatexCompilationResult compile(String file, String filePath, String inputPath, String outputPath, String outputFormat) throws IOException {
        LatexCompilationResult result = new LatexCompilationResult(file, filePath, inputPath, outputPath);
        Map<String, Long> originalFiles = generateNewFiles(outputPath, Collections.EMPTY_MAP);
        long start = System.currentTimeMillis();
        long current = start;
        File inputFile = new File(inputPath + "/" + file);
        File outputFile = new File(outputPath + "/" + file);
        File inputPathFile = new File(inputPath);
        File outputPathFile = new File(outputPath);
        if (inputFile.exists()) {
            Files.copy(inputFile, outputFile);
            String[] env = buildEnv();
            String command = compilationCommandsByOutput.get(outputFormat);
            String commands = command + " " + file;
            // We execute the compilation three times to ensure the correct binding of the references in the file:
            executeCommand(commands, env, result, outputPathFile);
            executeCommand(commands, env, result, outputPathFile);
            commands = "makeindex " + file;
            executeCommand(commands, env, result, outputPathFile);
            commands = "bibtex " + file;
            executeCommand(commands, env, result, outputPathFile);
            commands = command + " " + file;
            executeCommand(commands, env, result, outputPathFile);
            result.setOutputFiles(new ArrayList<String>(generateNewFiles(outputPath, originalFiles).keySet()));
            outputFile.delete();                    
            /*
            String[] commands = new String[compilationCommandsByOutput.get(outputFormat).size()];
            int i=0;
            for(String command:compilationCommandsByOutput.get(outputFormat)){
                commands[i]=command + " " + outputPath + " " + file;        
                i++;
            } **/  
            
        } else {
            result.setDuration(0);
            result.setExitCode(-1);
            result.setErrors("The file " + inputFile.getAbsolutePath() + " does not exist!");
            result.setOutput("");
            result.setOutputFiles(Collections.EMPTY_LIST);
        }
        return result;
    }

    private Map<String, Long> generateNewFiles(String outputPath, Map<String, Long> originalFiles) {
        Map<String, Long> result = new HashMap<>();
        File f = new File(outputPath);
        if (f.isDirectory()) {
            File[] listing = f.listFiles();
            for (File candidate : listing) {
                result.put(candidate.getAbsolutePath(), candidate.lastModified());
            }
        }
        for (String filename : originalFiles.keySet()) {
            result.remove(filename, originalFiles.get(filename));
        }
        return result;
    }

    private String[] buildEnv() {
        String[] result = {};
        ArrayList<String> env = new ArrayList<>();
        Map<String, String> envVars = System.getenv();
        for (String name : envVars.keySet()) {
            env.add(name + "=" + envVars.get(name));
        }
        return env.toArray(result);
    }

    private void executeCommand(String command, String[] env, LatexCompilationResult result, File inputPathFile) throws IOException {
        long start = System.currentTimeMillis();
        long current = start;
        Process p = Runtime.getRuntime().exec(command, env, inputPathFile);
        Writer w = new OutputStreamWriter(p.getOutputStream());
        while (p.isAlive() && current - start <= maxTimeOutPerCommand) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LatexCompiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            current = System.currentTimeMillis();
            if (current - start > maxTimeOutPerCommand) {
                p.destroy();
            }
            w.write("\n");
        }
        result.setDuration(current - start);
        result.setExitCode(p.exitValue());
        if (p.getErrorStream().available() != 0) {
            result.setErrors(result.getErrors() + IOUtils.toString(p.getErrorStream(), encoding));
        }
        if (p.getInputStream().available() != 0) {
            result.setOutput(result.getOutput() + "\n" + IOUtils.toString(p.getInputStream(), encoding));
        }
    }
}
