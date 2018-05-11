/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.ideas.controller.latex;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author japarejo
 */
public class LatexCompilerTest {
    
    public LatexCompilerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of compile method, of class LatexCompiler.
     */
    @Test
    public void testCompile_3args() throws Exception {
        System.out.println("compile");        
        
        File f=new File("src/main/resources/testfiles");
        File outputFolder=new File(f.getAbsolutePath()+"/latexOutput");
        FileUtils.deleteDirectory(outputFolder);
        outputFolder.mkdir();
        LatexCompiler instance = new LatexCompiler();
        LatexCompilationResult expResult = null;
        LatexCompilationResult result = instance.compile("SimpleLatexDocument.tex","", f.getAbsolutePath(), outputFolder.getAbsolutePath());
        assertNotNull(result);        
        assertTrue(result.getOutputFiles().size()>=0);
        System.out.println("===================================================");
        System.out.println("SALIDA:"+result.getOutput());
        System.out.println("===================================================");
        System.out.println("ERRORES:"+result.getErrors());
        System.out.println("===================================================");
        System.out.println("FICHEROS CREADOS:");
        for(String createdFile:result.getOutputFiles()){
            System.out.println("   "+createdFile);
        }
        System.out.println("===================================================");
        
    }

    
    
}
