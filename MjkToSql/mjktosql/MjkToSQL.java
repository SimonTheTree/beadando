/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mjktosql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ganter
 */
public class MjkToSQL {

    static String currentCategory;
    static int currentCategoryID;
    static List<String> categoryList = new ArrayList<String>();
    static List<String> questionList =  new ArrayList<String>();
    static final String SRC_FOLDER_STR = "/home/ganter/Documents/Suli/Egyetem/2016-17_2/DB2/kerdesek";
    static final String SQL_QUESTIONS_TABLE_NAME = "NORMAL_QUESTIONS";
    static final String SQL_QUESTION_TOPICS_TABLE_NAME = "QUESTION_TOPICS";

    static final File SRC_FOLDER = new File(SRC_FOLDER_STR);
    static final File OUT_FILE = new File("/home/ganter/Desktop/Quiz.sql");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FileIterator fit = new FileIterator(SRC_FOLDER, "txt");
        fit.performFunction(new Function() {
            @Override
            public void function(File f) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    String question = "";
                    String[] answer = new String[4];
                    
                    int hardness = Integer.parseInt(f.getName().split("_")[0]);
                    boolean isQuestion = false;
                    while (null != (line = br.readLine())) {
                        line = line.trim();
                        if (line.matches("\\</.*\\>")) {
                            line.substring(line.indexOf("</") + 1, line.indexOf(">"));
                            currentCategory = null;
                            isQuestion = false;

                        } else if (line.matches("\\<.*\\>")) {
                            currentCategory = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
                            if (!categoryList.contains(currentCategory)) {
                                categoryList.add(currentCategory);
                            }
                            currentCategoryID = categoryList.indexOf(currentCategory);
                            isQuestion = false;

                        } else if (line.matches("=(.*)")) {

                            isQuestion = true;

                            //SQL-escape-eljuk a sort (' ->\')
                            line = line.replace("'", "''");
                            
                            //kiszedjuk a kerdest:
                            question = line.substring(line.indexOf("=") + 1, line.indexOf("^"));

                            //kiszedjuk a helyes valaszt:
                            //^& van elotte, es utana ^ vagy sorvege van
                            Pattern pattern = Pattern.compile("(?<=\\^&).*?(?=(\\^|$))");
                            Matcher matcher = pattern.matcher(line);
                            matcher.find();
                            answer[0] = matcher.group();

                            //kiszedjuk a helytelen valaszokat
                            //^ van elotte, nem &-el kezdodik, es utana ^ vagy sorvege van
                            pattern = Pattern.compile("(?<=\\^)[^&](.*?)(?=(\\^|$))");
                            matcher = pattern.matcher(line);
                            matcher.find();
                            answer[1] = matcher.group();
                            matcher.find();
                            answer[2] = matcher.group();
                            matcher.find();
                            answer[3] = matcher.group();
                            matcher.find();

                        }
                        if (isQuestion) {
                            questionList.add(String.format("INSERT INTO %s "
                                    + "(question, right_answer, answer1, answer2, answer3, topic_ID, difficulty) "
                                    + "VALUES ('%s', '%s', '%s', '%s', '%s', %d, %d);",
                                    SQL_QUESTIONS_TABLE_NAME,
                                    question,
                                    answer[0],
                                    answer[1],
                                    answer[2],
                                    answer[3],
                                    currentCategoryID,
                                    hardness
                            ));
                        }
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        try(PrintWriter bw = new PrintWriter(OUT_FILE, "UTF-8")) {
            for (String currentCategory : categoryList) {
                bw.printf(String.format("INSERT INTO %s "
                        + "(topic_ID, name) "
                        + "VALUES (%d, '%s');%n",
                        SQL_QUESTION_TOPICS_TABLE_NAME,
                        categoryList.indexOf(currentCategory),
                        currentCategory
                ));
            }
            for (String question : questionList){
                bw.println(question);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
