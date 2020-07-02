package ru.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class Utils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.y HH:mm:ss");

    /**
     * По указанному пути создает файл, если такого нет
     * @param filePath путь до создаваемого файла
     * @return объект созданного файла
     * @throws IOException исключени при создании файла
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

    /**
     * Задание структуры сообщения для логгирования
     * @param username имя пользователя, отправившего сообщение,
     *                 системные сообщения отправляются от пользователя System
     * @param msg сообщение
     * @return строка подготовленная для логгирования
     */
    public static String prepareLogMessage(String username, String msg) {
        return String.format("%s %s:\n%s\n", formatter.format(new Date()), username, msg);
    }

    /**
     * Метод логгирования информации в файл по указанному пути
     * @param path путь до файла, содержащего логи
     * @param msg сообщение, которое необходимо залогировать
     */
    public static void logging(String path, String msg) {
        try (OutputStream out = new BufferedOutputStream(
            new FileOutputStream(createFile(path), true))) {
                out.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLastLogs(String logPath, int msgCount) throws IOException {
        ArrayList<String> logs = new ArrayList<>();
        try (RandomAccessFile log = new RandomAccessFile(logPath, "r")) {
            long currentPosition = log.length() - 1;
            log.seek(currentPosition);
            int rowCount = 0;
            int x = log.read();
            while (rowCount < msgCount && currentPosition != 0) {
                do {
                    log.seek(--currentPosition);
                    if (currentPosition > 0)
                        x = log.read();
                    else
                        break;
                } while (x != 10 && x != 13);
                String logRow = new String(log.readLine().getBytes("ISO-8859-1"), "UTF-8");
                logs.add(0, logRow);
                rowCount++;
            }
        }
        return String.join("\n", logs) + "\n";
    }
}