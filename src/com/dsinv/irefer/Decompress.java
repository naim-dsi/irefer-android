package com.dsinv.irefer;

import android.util.Log; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream; 
 
/** 
 * 
 * @author jon collected from http://www.jondev.net/articles/Unzipping_Files_with_Android_%28Programmatically%29
 */ 
public class Decompress { 
  private String _zipFile; 
  private String _location; 
 
  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 
 
    _dirChecker(""); 
  } 
 
//  public void unzip() { 
//    try  { 
//      FileInputStream fin = new FileInputStream(_zipFile); 
//      ZipInputStream zin = new ZipInputStream(fin); 
//      ZipEntry ze = null; 
//      while ((ze = zin.getNextEntry()) != null) { 
//        Log.v("Decompress", "Unzipping " + ze.getName()); 
// 
//        if(ze.isDirectory()) { 
//          _dirChecker(ze.getName()); 
//        } else { 
//          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
//          for (int c = zin.read(); c != -1; c = zin.read()) { 
//            fout.write(c); 
//          } 
// 
//          zin.closeEntry(); 
//          fout.close(); 
//          
//        } 
//        Log.v("Decompress", "Unzip done " + ze.getName());  
//      } 
//      zin.close(); 
//    } catch(Exception e) { 
//      Log.e("Decompress", "unzip", e); 
//    } 
// 
//  } 

  public void unzip() {
    try {
      //String filename = download(fileUrl);
      ZipFile zip = new ZipFile(_zipFile);
      Enumeration<? extends ZipEntry> zippedFiles = zip.entries();
      while (zippedFiles.hasMoreElements()) {
        ZipEntry entry = zippedFiles.nextElement();
        InputStream is = zip.getInputStream(entry);
        String name = entry.getName();
        File outputFile = new File(_location + name);
        String outputPath = outputFile.getCanonicalPath();
        name = outputPath.substring(outputPath.lastIndexOf("/") + 1);
        outputPath = outputPath.substring(0, outputPath.lastIndexOf("/"));
        File outputDir = new File(outputPath);
        outputDir.mkdirs();
        outputFile = new File(outputPath, name);
        outputFile.createNewFile();
        FileOutputStream out = new FileOutputStream(outputFile);

        byte buf[] = new byte[16384];
        do {
          int numread = is.read(buf);
          if (numread <= 0) {
            break;
          } else {
            out.write(buf, 0, numread);
          }
        } while (true);

        is.close();
        out.close();
      }
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 
 
    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
} 