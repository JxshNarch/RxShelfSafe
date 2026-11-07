package database;

import java.io.File;


public class FileFinder {

    public static String find(String name) {
      
        try {
            File classes = new File(FileFinder.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String found = searchUp(classes, name);
            if (found != null) {
                return found;
            }
        } catch (Exception e) {
           
        }

      
      
        
        File start = new File(System.getProperty("user.dir"));
        File[] inside = start.listFiles();
        if (inside != null) {
            for (int i = 0; i < inside.length; i++) {
                if (inside[i].isDirectory()) {
                    File file = new File(inside[i], name);
                    if (file.isFile()) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        String found = searchUp(start, name);
        if (found != null) {
            return found;
        }

        return name;       
    }

   
    private static String searchUp(File folder, String name) {
        int steps = 0;
        while (folder != null && steps < 6) {
            File file = new File(folder, name);
            if (file.isFile()) {
                return file.getAbsolutePath();
            }
            folder = folder.getParentFile();
            steps++;
        }
        return null;
    }
}