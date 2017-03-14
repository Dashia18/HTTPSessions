package myHttpSessionServlet;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Daria Serebryakova on 14.03.2017.
 */
public class GetDataAsArray {

    public static String[][] getDataAsArray(String path){
        List<String> strList = fileScannerToSrtList(path);
        return listToArrayLoginPwd(strList);
    }
    private static List<String> fileScannerToSrtList(String path) {
        List<String> strList = new LinkedList<>();
        try {
            Scanner in = new Scanner(new File(path));
            while (in.hasNext()){
                strList.add(in.nextLine());
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return strList;
    }
    private static String[][] listToArrayLoginPwd(List<String> strList){
        String[][] arr = new String[strList.size()][2];
        Pattern pattern2 = Pattern.compile("[ ,!?\\[\\]]");
        int i = 0;
        for (String str:strList){
            String[] words = pattern2.split(str);
            arr[i][0] = words[0];//0 col
            arr[i][1] = words[1];//1 col
            i++;
        }
        return arr;
    }
}
