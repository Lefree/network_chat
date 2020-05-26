package practice.java.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils {

    /**
     * По указанному пути создает файл, если такого нет
     * @param filePath путь до создаваемого файла
     * @return 
     * @throws IOException
     */
    public static File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        createRecursiveDir(file.getParent());
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * Метод рекурсивно идет с конца пути к корню, в случае если
     * в указанном пути есть несозданные дириктории, осуществляет
     * создание
     * @param directoryPath путь к конечной дириктории
     */
    private static void createRecursiveDir(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists())
            createRecursiveDir(directory.getParent());
        directory.mkdir();
    }

    public static void logging(String path, String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.y HH:mm:ss");
        String logMessage = formatter.format(new Date()) + "\n" + msg;
        try (FileOutputStream fos =
            new FileOutputStream(createFile(path), true)) {
                fos.write(logMessage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}