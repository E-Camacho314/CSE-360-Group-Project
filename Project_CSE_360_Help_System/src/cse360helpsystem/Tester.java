package cse360helpsystem;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class Tester {
	public static DatabaseHelper databaseHelper;
	
	// Search by groups, add an article to a special access group, and search by special access groups tester
	@Test
	public void WN1() {
		try {
		databaseHelper.connectToDatabase();
		databaseHelper.loginUser("i", "i");
		databaseHelper.searchArticlesByGroups("i", "1");
		databaseHelper.createGroup("new", "i");
		databaseHelper.addArticleToGroup(1, "new");
		databaseHelper.searchArticlesByGroups("i", "new");
		databaseHelper.closeConnection();
		}
		catch(Exception e) {
			
		}
	}
	
	// Search by Difficulty Tester
	@Test
	public void WN2() {
		try {
			databaseHelper.connectToDatabase();
			databaseHelper.loginUser("i", "i");
			databaseHelper.getArticlesByDifficulty("i", true, false, false, false);
			databaseHelper.getArticlesByDifficulty("i", false, true, false, false);
			databaseHelper.getArticlesByDifficulty("i", false, false, true, false);
			databaseHelper.getArticlesByDifficulty("i", false, false, false, true);
			databaseHelper.getArticlesByDifficulty("i", true, true, true, true);
			databaseHelper.closeConnection();

		}
		catch(Exception e) {
			
		}
	}
	
	// Create special access group and add user to special access group tester
	@Test
	public void WN3() {
		try {
			databaseHelper.connectToDatabase();
			databaseHelper.loginUser("i", "i");
			databaseHelper.createGroup("new", "i");
			databaseHelper.addInstructor("new", "a", "admin");
			databaseHelper.addInstructor("new", "m", "view");
			databaseHelper.addStudentToViewAccess("new", "s");
			databaseHelper.closeConnection();
		}
		catch(Exception e) {
			
		}
	}
}
