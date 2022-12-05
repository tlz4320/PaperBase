package util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Sql.Sql.Ids;

public class Utils {
    public static Random random = new Random();
    public static boolean checkFile(String query, File file, boolean nul) {
        if(nul)
            query = query.toLowerCase();
        if(!file.exists())
            return false;
        if(file.isFile() && Utils.checkPostfix(file.getName())){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while((line = reader.readLine()) != null){
                    if(nul)
                        line = line.toLowerCase();
                    if (line.contains(query))
                        return true;
                }
            }catch (Exception e){
                return false;
            }
        }
        File[] files = file.listFiles();
        if(files == null || files.length == 0)
            return false;
        for(File f : files){
            if(checkFile(query, f, nul))
                return true;
        }
        return false;
    }
    public static boolean checkFile(String query, String path, boolean nul){
        File file = new File(path);
        return checkFile(query, file, nul);
    }
    public static boolean checkPostfix(String filename){
        String res = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return "java txt r cpp c h json xml py json".contains(res);
    }
    public static String isDateFormat(String query) {
        if (query.length() < 3)
            return null;
        StringBuilder builder = new StringBuilder();
        for (char c : query.toCharArray()) {
            if (c >= '0' && c <= '9')
                builder.append(c);
            else
                builder.append('-');
        }
        String[] timeset = builder.toString().split("[-]");
        if (timeset.length < 2 || timeset.length > 3)
            return null;
        try {
            if (timeset.length == 2) {

                int first_num = Integer.parseInt(timeset[0]);
                int second_num = Integer.parseInt(timeset[1]);
                if (first_num > 12 && second_num <= 12)
                    return String.format("%02d",first_num) + "-" + String.format("%02d",second_num);
                if (first_num <= 12 && second_num <= 31)
                    return String.format("%02d",first_num) + "-" + String.format("%02d",second_num);
                return null;

            }
            int first_num = Integer.parseInt(timeset[0]);
            int second_num = Integer.parseInt(timeset[1]);
            int tired_num = Integer.parseInt(timeset[2]);
            if(first_num > 1900 && second_num <= 12 && tired_num <= 31)
                return first_num + "-" + String.format("%02d",second_num) + "-" + String.format("%02d",tired_num);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static long generateID(String filename, String filepath, String des, String time){
        try {
            if(Ids == null)
                Ids = Sql.Sql.getIDs();
            Date date = Date.valueOf(time);
            int low8byte = (int)(date.getTime() / 511456000) + random.nextInt() * 0x0f00;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((filename + filepath + des).getBytes());
            long up8byte = new BigInteger(md5.digest()).longValue();
            up8byte = (up8byte * 0xf0f00000) >>> 32 + up8byte * 0x00000f00;
            up8byte = random.nextInt() * 0x000f + up8byte;
            long res = up8byte;
            res = res << 31 + low8byte;
            while(Ids.contains(res))
                res++;
            return res;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
