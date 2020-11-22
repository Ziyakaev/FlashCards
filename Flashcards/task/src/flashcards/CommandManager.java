package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static flashcards.Constants.fileStatistic;

public class CommandManager {
    private final Scanner scanner;
    private String [] params;
    Map<String, Integer> terminByCardMap = new HashMap<>();
    Map<String, Integer> definitionCardMap = new HashMap<>();
    CustomLog log = new CustomLog();
    Map<String, Integer> terminByErrors = new HashMap<>();
    List<Card> cards = new ArrayList<>();
    boolean isSaveFileAfterRun = false;

    public CommandManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void prepareData(String[] params){
        this.params = params;
        loadCardBeforeRun(parseArguments(params, "-import"));
        isSaveFileAfterRun = !parseArguments(params,"-export").isEmpty();
    }

    public void loadCardBeforeRun (String fileName) {
    	if(!fileName.isEmpty()) {
			loadCardsByFile(fileName);
		}
    }

    public void saveCardAfterRun(String fileName){
    	if(!fileName.isEmpty()){
    		saveCardsByFile(fileName);
		}

	}

    public String parseArguments(String[] params, String action) {
        String param= "";
        if ((params.length == 2) && (action.equalsIgnoreCase(params[0]))) {
            param = params[1];
        } else if (params.length == 4) {
            if (action.equalsIgnoreCase(params[0])) {
                param = params[1];
            }
            if (action.equalsIgnoreCase(params[2])) {
                param = params[3];
            }

        }
        return param;
    }

    public void processCommand() {
        boolean isRun = true;
        while (isRun) {
            log.handleLog("input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):", true);
            String textCommad = scanner.nextLine();
            log.handleLog(textCommad, false);
            switch (textCommad) {
                case "add":
                    loadCards();
                    break;
                case "remove":
                    removeCards();
                    break;
                case "import":
                    importCards();
                    break;
                case "ask":
                    askCards();
                    break;
                case "export":
                    exportCards();
                    break;
                case "log":
                    logCards();
                    break;
                case "hardest card":
                    hardestCards();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "exit":
                    if (isSaveFileAfterRun){
                        saveCardAfterRun(parseArguments(params, "-export"));
                    }
                    log.handleLog("Bye bye!", true);
                    isRun = false;
                    break;
                default:
                    System.out.println("not found command");
                    break;
            }
        }
    }

    private void resetStats() {
        terminByErrors.clear();
        log.handleLog("Card statistics have been reset.", true);
    }

    private void logCards() {
        log.handleLog("File name:", true);
        String logFileName = scanner.nextLine();
        log.handleLog(logFileName, false);
        File logFile = new File(logFileName);
        String dirName = "flashCards";
        createDirectory(dirName);
        log.handleLog("buy", false);
        try (PrintWriter printWriter = new PrintWriter(logFile)) {
            for (String log : log.getLogs()) {
                printWriter.println(log);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.handleLog("The log has been saved.", true);
    }

    private void hardestCards() {
        int max = Integer.MIN_VALUE;
        List<String> termWithMaxErrors = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : terminByErrors.entrySet()) {
            if (entry.getValue() >= max) {
                if (entry.getValue() == max) {
                    termWithMaxErrors.add(entry.getKey());
                } else {
                    max = entry.getValue();
                    termWithMaxErrors.add(entry.getKey());
                }
            }
        }
        if (termWithMaxErrors.isEmpty()) {
            log.handleLog("There are no cards with errors.", true);
        } else if (termWithMaxErrors.size() == 1) {
            log.handleLog("The hardest card is \"" + termWithMaxErrors.get(0) + "\"." + " You have "
                    + terminByErrors.get(termWithMaxErrors.get(0)) + " errors answering it", true);
        } else {
            StringBuilder logText = new StringBuilder("The hardest cards are");
            int sum = 0;
            for (int i = 0; i < termWithMaxErrors.size(); i++) {
                if (i == termWithMaxErrors.size() - 1) {
                    logText.append(" \"" + termWithMaxErrors.get(i) + "\".");
                    sum += terminByErrors.get(termWithMaxErrors.get(i));
                } else {
                    sum += terminByErrors.get(termWithMaxErrors.get(i));
                    logText.append(" \"" + termWithMaxErrors.get(i) + "\",");
                }
            }
            logText.append(" You have " + sum + "errors answering them.");

            log.handleLog(logText.toString(), true);
        }

    }

    public void loadCards() {
        log.handleLog("The card :", true);
        String term = scanner.nextLine();
        log.handleLog(term, false);
        if (hasTerm(term.toLowerCase())) {
            log.handleLog("The card \"" + term + "\" already exists.", true);
            return;
        }
        log.handleLog("The definition of the card :", true);
        String definition = scanner.nextLine();
        log.handleLog(definition, false);
        if (hasDefinition(definition.toLowerCase())) {
            log.handleLog("The definition \"" + definition + "\" already exists", true);
            return;
        }
        Card card = new Card(term, definition);
        cards.add(card);
        int num = cards.size();
        terminByCardMap.put(term.toLowerCase(), num - 1);
        definitionCardMap.put(definition.toLowerCase(), num - 1);
        log.handleLog("The pair (\"" + term + "\":\"" + definition + "\") has been added.", true);
    }

    private void removeCards() {
        log.handleLog("Which card?", true);
        String cardName = scanner.nextLine();
        log.handleLog(cardName, false);
        if (terminByCardMap.get(cardName.toLowerCase()) != null) {
            String definition = cards.get(terminByCardMap.get(cardName.toLowerCase())).getDefinition();
            terminByCardMap.remove(cardName);
            cards.remove((int) terminByCardMap.get(cardName.toLowerCase()));
            definitionCardMap.remove(definition);
            log.handleLog("The card has been removed.", true);
        } else {
            log.handleLog("Can't remove \"" + cardName + "\": there is no such card.", true);
        }
    }

    private void askCards() {
        log.handleLog("How many times to ask?", true);
        int countQuestion = Integer.parseInt(scanner.nextLine());
        log.handleLog(String.valueOf(countQuestion), false);
        try {
            makeGuesses(countQuestion);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void makeGuesses(int countQuestion) throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstanceStrong();
        for (int i = 0; i < countQuestion; i++) {
            int numberOfCard = cards.isEmpty() ? 0 : random.nextInt(cards.size());
            Card card = cards.get(numberOfCard);
            log.handleLog("Print the definition of \"" + card.getWord() + "\":", true);
            String guess = scanner.nextLine();
            log.handleLog(guess, false);
            boolean right = card.check(guess);
            if (right) {
                log.handleLog("Correct!", true);
            } else if (definitionCardMap.containsKey(guess)) {
                terminByErrors.merge(card.getWord(), 1, (oldValue, newValue) -> oldValue + newValue);
                log.handleLog("Wrong. The right answer is \"" + card.getDefinition() + "\", but your definition is correct for \""
                        + cards.get(definitionCardMap.get(guess)).getWord() + "\".", true);
            } else {
                terminByErrors.merge(card.getWord(), 1, (oldValue, newValue) -> oldValue + newValue);
                log.handleLog("Wrong. The right answer is \"" + card.getDefinition() + "\".", true);
            }
        }
    }

    private void importCards() {
        log.handleLog("File name:", true);
        String fileName = scanner.nextLine();
        log.handleLog(fileName, false);
        loadCardsByFile(fileName);
    }

    private void loadCardsByFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            initializeCardsByFile(file);
            initializeStatistic();
        } else {
            log.handleLog("File not found.", true);
        }
    }

    private void initializeStatistic() {
        File file = new File(fileStatistic);
        if (file.isFile()) {
            try (Scanner scannerFile = new Scanner(file);) {
                String[] data = new String[2];
                while (scannerFile.hasNext()) {
                    data = scannerFile.nextLine().split(" ");
                    terminByErrors.put(data[0], Integer.parseInt(data[1]));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            log.handleLog("initialized file statistic", false);
        }
    }

    private int initializeCardsByFile(File file) {
        int count = 0;
        if (!file.isFile()) {
            return count;
        }
        try (Scanner scannerFile = new Scanner(file);) {
            String[] data = new String[2];
            while (scannerFile.hasNext()) {
                data = scannerFile.nextLine().split(" ");
                Card card = new Card(data[0], data[1]);
                if (terminByCardMap.get(data[0].toLowerCase()) != null) {
                    cards.remove((int) terminByCardMap.get(data[0].toLowerCase()));
                }
                terminByCardMap.put(data[0].toLowerCase(), count);
                definitionCardMap.put(data[1].toLowerCase(), count);
                cards.add(card);
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log.handleLog(count + " cards have been loaded.", true);
        return count;
    }

    private void createDirectory(String dirName) {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private void exportCards() {
        log.handleLog("File name:", true);
        String fileName = scanner.nextLine();
        log.handleLog(fileName, false);
		saveCardsByFile(fileName);
    }

    private void saveCardsByFile(String fileName) {
        int count = 0;
        String dirName = "flashCards";
        createDirectory(dirName);
        File file = new File(fileName);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Card card : cards) {
                printWriter.println(card.getWord() + " " + card.getDefinition());
                count++;
            }

        } catch (FileNotFoundException e) {
            log.handleLog("Can't write to file " + fileName, true);
            e.printStackTrace();
        }
        exportStatistic();
        log.handleLog(count + " cards have been saved.", true);
    }

    private void exportStatistic() {
        String dirName = "flashCards";
        createDirectory(dirName);
        File file = new File(fileStatistic);
        int count = 0;
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, Integer> entry : terminByErrors.entrySet()) {
                printWriter.println(entry.getKey() + " " + entry.getValue());
                count++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean hasTerm(String definition) {
        return terminByCardMap.get(definition) != null;
    }


    private boolean hasDefinition(String definition) {
        return definitionCardMap.get(definition) != null;
    }

}
