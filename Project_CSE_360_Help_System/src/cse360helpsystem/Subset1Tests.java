package cse360helpsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Subset1Tests {

	 private AccountService accountService;
	 
	 public class AccountService {
		    private String username;
		    private String password;
		    private boolean accountCreated = false;

		    public boolean establishAccount(String oneTimeCode, String username, String password, String confirmPassword) {
		        if (!isValidOneTimeCode(oneTimeCode)) {
		            throw new IllegalArgumentException("Invalid one-time code");
		        }
		        if (!password.equals(confirmPassword)) {
		            throw new IllegalArgumentException("Passwords do not match");
		        }
		        this.username = username;
		        this.password = password;
		        this.accountCreated = true;
		        return true;
		    }

		    public boolean isAccountCreated() {
		        return accountCreated;
		    }

		    private boolean isValidOneTimeCode(String code) {
		        // Example validation logic
		        return code != null && code.matches("\\d{6}");
		    }
	    void setUp() {
	        accountService = new AccountService();
	    }

	    // Test for initial account creation with valid inputs
	    @Test
	    void testEstablishAccountWithValidInputs() {
	        String oneTimeCode = "123456";
	        String username = "testUser";
	        String password = "securePassword";
	        String confirmPassword = "securePassword";

	        assertDoesNotThrow(() -> 
	            assertTrue(accountService.establishAccount(oneTimeCode, username, password, confirmPassword))
	        );
	        assertTrue(accountService.isAccountCreated());
	    }

	    // Test for account creation with invalid one time code
	    @Test
	    void testEstablishAccountWithInvalidOneTimeCode() {
	        String oneTimeCode = "abc123"; // Invalid code
	        String username = "testUser";
	        String password = "securePassword";
	        String confirmPassword = "securePassword";

	        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
	            accountService.establishAccount(oneTimeCode, username, password, confirmPassword)
	        );
	        assertEquals("Invalid one-time code", exception.getMessage());
	        assertFalse(accountService.isAccountCreated());
	    }

	    // Test for account creation with password fields not matching
	    @Test
	    void testEstablishAccountWithNonMatchingPasswords() {
	        String oneTimeCode = "123456";
	        String username = "testUser";
	        String password = "securePassword";
	        String confirmPassword = "wrongPassword";

	        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
	            accountService.establishAccount(oneTimeCode, username, password, confirmPassword)
	        );
	        assertEquals("Passwords do not match", exception.getMessage());
	        assertFalse(accountService.isAccountCreated());
	    }

	    // Test for account creation with one time code field empty
	    @Test
	    void testEstablishAccountWithEmptyOneTimeCode() {
	        String oneTimeCode = ""; // Empty code
	        String username = "testUser";
	        String password = "securePassword";
	        String confirmPassword = "securePassword";

	        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
	            accountService.establishAccount(oneTimeCode, username, password, confirmPassword)
	        );
	        assertEquals("Invalid one-time code", exception.getMessage());
	        assertFalse(accountService.isAccountCreated());
	    }
	}
}