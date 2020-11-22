package flashcards;

import java.util.*;

public class UI {
	Scanner scanner = new Scanner(System.in);

	public void start(String[] param){
		CommandManager commandManager = new CommandManager(scanner);
		commandManager.prepareData(param);
		commandManager.processCommand();
	}



}