package database;

import java.io.File;

// Finds a file of the project (like icon.png) even when the program was started from another folder.
public class FileFinder {

    public static String find(String name) {
        // 1. look in the folder of the compiled classes and in the folders above it
        try {
            File classes = new File(FileFinder.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String found = searchUp(classes, name);
            if (found != null) {
                return found;
            }
        } catch (Exception e) {
            // not possible here, try the next way
        }

        // 2. look in the folders INSIDE the folder the program was started from (up to 4 levels down),
        //    the closest folders first
        File start = new File(System.getProperty("user.dir"));
        for (int depth = 1; depth <= 4; depth++) {
            String found = searchDown(start, name, depth);
            if (found != null) {
                return found;
            }
        }

        // 3. look in the start folder itself and in the folders above it
        String found = searchUp(start, name);
        if (found != null) {
            return found;
        }

        return name;        // not found anywhere: use the plain name like before
    }

    // looks for the file in the folders that are exactly "depth" levels below this folder
    private static String searchDown(File folder, String name, int depth) {
        File[] inside = folder.listFiles();
        if (inside == null) {
            return null;
        }
        for (int i = 0; i < inside.length; i++) {
            if (!inside[i].isDirectory() || inside[i].getName().startsWith(".")) {
                continue;                                  // skip files and hidden folders like .git
            }
            if (depth == 1) {
                File file = new File(inside[i], name);
                if (file.isFile()) {
                    return file.getAbsolutePath();
                }
            } else {
                String found = searchDown(inside[i], name, depth - 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // goes up folder by folder (at most 6 times) until the file is found
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