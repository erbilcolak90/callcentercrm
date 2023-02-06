package com.callcentercrm.www.csv;

import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.enums.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVHelper {

    public static String TYPE =  "text/csv";
    static String[] HEADERs = {"id","name","surname","phoneNumber"};

    public static boolean hasCSVFormat(MultipartFile file){

        if(!TYPE.equals(file.getContentType())){
            return false;
        }
        return true;
    }

    public static List<User> csvUsers(InputStream inputStream){
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        ){

            List<User> users = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for(CSVRecord csvRecord: csvRecords){
                String email = csvRecord.get("email");
                int index = email.indexOf('@');
                String username = email.substring(0,index);
                User user = new User(null,csvRecord.get("name"),csvRecord.get("surname"),
                        username,csvRecord.get("email"),csvRecord.get("phoneNumber"),Status.PENDING,new Date(),new Date());

                users.add(user);
            }
            int size = users.size();

            return users;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
