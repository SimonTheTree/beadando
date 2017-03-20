/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mjktosql;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author ganter
 */
public class FileIterator {
    private List<File> files;
    
    /**
     * Create fileiterator that performs a function on any file inside given 
     * directory(and recursively subdirectories). 
     * @param folder The folder to iterate over
     */
    public FileIterator(final File folder) {
        files = new ArrayList<File>();
        try(Stream<Path> paths = Files.walk(Paths.get(folder.getAbsolutePath()))) {
            paths.forEach(filePath -> {                
                if (Files.isRegularFile(filePath)) {
                    files.add(filePath.toFile());
                }
            });
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Create fileiterator that performs a function on each file inside given 
     * directory(and recursively subdirectories) with specified extension. 
     * @param folder The folder to iterate over
     * @param validExtensions list of valid extensions. Okay, if ANY matches.
     */
    public FileIterator(final File folder, String... validExtensions) {
        files = new ArrayList<File>();
        try(Stream<Path> paths = Files.walk(Paths.get(folder.getAbsolutePath()))) {
            paths.forEach(filePath -> {                
                if (Files.isRegularFile(filePath)) {
                    String fileName = filePath.getFileName().toString();
                    String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                
                    boolean extensionOK = false;
                    
                    //check for extension match
                    for(String ex : validExtensions){
                        if(ex.equals(extension))
                            extensionOK = true;
                    }
                    
                    files.add(filePath.toFile());
                }
            });
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    void performFunction(Function f){
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
            File file = iterator.next();
            f.function(file);
        }
    }
    
}
