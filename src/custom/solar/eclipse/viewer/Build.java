package custom.solar.eclipse.viewer;


import static custom.solar.eclipse.viewer.config.Constants.OUTPUT_PDF_FILE;
import static custom.solar.eclipse.viewer.util.LogUtil.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.itextpdf.text.DocumentException;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.config.ConfigFromFile;
import custom.solar.eclipse.viewer.draw.GenerateViewer;

/** Build the PDF file for the viewer as a standalone program from the command line. */
public final class Build {
  
  /** 
   As a standalone program, generate the viewer as a PDF file (one page, two-sided).
   The file is saved to the file system, in an existing directory (see config.ini). 
  */
  public static void main(String... args) throws DocumentException, IOException {
    log("Building a custom solar eclipse viewer, using data from a config file.");
    
    Config config = new ConfigFromFile().init();
    log("  " + config.toString());
    
    log("Generating PDF file.");
    GenerateViewer viewer = new GenerateViewer(config);
    viewer.outputTo(streamFor(OUTPUT_PDF_FILE, config));
    
    log("File saved to " + fullFileName(OUTPUT_PDF_FILE, config));
    log("Done.");
  }

  //PRIVATE 
  
  private static OutputStream streamFor(String fileName, Config config) throws FileNotFoundException {
    return new FileOutputStream(fullFileName(fileName, config));
  }
  
  private static String fullFileName(String name, Config config) {
    String result = config.outputDir() + File.separator + name;
    return result;
  }
}
