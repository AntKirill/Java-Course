package ru.ifmo.ctddev.antonov.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class RecursiveWalk {
    int processFile(Path pathFile) throws IOException {
        InputStream in = null;
        File file = pathFile.toFile();
        try {
            in = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len;
            Hasher hasher = new Hasher();
            int ans = hasher.getInitialValue();
            while ((len = in.read(buf)) != -1) {
                ans = hasher.FNV(buf, len);
            }
            return ans;
        } catch (IOException e) {
            return 0;
        } finally {
            close(in);
        }
    }

    void processLine(BufferedReader in, PrintWriter out, String curFileName) throws IOException {
        Path path = Paths.get(curFileName);
        if (Files.notExists(path)) {
            out.println(String.format("%08x", 0).concat(" ").concat(curFileName));
            System.out.println(Constants.FILE_NOT_EXISTS.concat(curFileName));
        } else {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            out.println(String.format("%08x", processFile(file)).concat(" ").concat(file.toString()));
                        } catch (IOException e) {
                            visitFileFailed(path, e);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        System.out.println(Constants.UNREADEBLE.concat(exc.getMessage()));
                        out.println(String.format("%08x", 0).concat(" ").concat(path.toString()));
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else if (Files.isRegularFile(path)) {
                try {
                    out.println(String.format("%08x", processFile(path)).concat(" ").concat(path.toString()));
                } catch (IOException e) {
                    out.println(String.format("%08x", 0).concat(" ").concat(path.toString()));
                }
            } else {
                System.out.println(Constants.UNKNOWN);
            }
        }
    }

    void run(String[] args) {
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            if (args.length != 2) {
                throw new IOException(Constants.WRONG_ARG_NUM);
            }
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
                writer = new PrintWriter(args[1], "UTF-8");
            } catch (IOException e) {
                throw new IOException(Constants.NO_PATH.concat(args[0]));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(reader, writer, line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            close(reader);
            close(writer);
        }
    }


    <T extends Closeable>
    void close(T r) {
        try {
            if (r != null) {
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        new RecursiveWalk().run(args);
    }

}