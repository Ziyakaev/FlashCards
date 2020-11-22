package flashcards;

import java.util.ArrayList;
import java.util.List;

public class CustomLog {
    List<String> logs = new ArrayList<>();
    CustomLog(){

    }
    public void handleLog(String text, boolean isPrint){
        addLog(text);
        printLog(text, isPrint);
    }

    private void addLog(String text){
        logs.add(text);
    }

    public List<String> getLogs() {
        return logs;
    }

    private void printLog(String text, boolean isPrint) {
        if (isPrint) {
            System.out.println(text);
        }
    }
}
