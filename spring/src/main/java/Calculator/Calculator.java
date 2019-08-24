package Calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum(String filePath) throws IOException {
        return fileReadTemplate(filePath, new LineCallback<Integer>() {
            public Integer doSomethingWithLine(String line, Integer value) {
                return Integer.valueOf(line)+value;
            }
        }, 0);
    }

    public Integer calcMultiply(String filePath) throws IOException {
        return fileReadTemplate(filePath, new LineCallback<Integer>() {
            public Integer doSomethingWithLine(String line, Integer value) {
                return Integer.valueOf(line)*value;
            }
        }, 1);
    }

    public String concatenate(String filePath) throws IOException {
        return fileReadTemplate(filePath, new LineCallback<String>() {
            public String doSomethingWithLine(String line, String value) {
                return value+line;
            }
        }, "");
    }

    private <T> T fileReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = initVal;
            String line = null;
            while ((line=br.readLine())!=null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if(br!=null) {
                try { br.close(); }
                catch (IOException e) {}
            }
        }
    }

}
