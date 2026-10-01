import java.util.Scanner;

public class ATM_Simulation_System {
	public static void main(String[] args) {
		ATM atm = new ATM(new Account("123456789", "1234", 1_000.00));
		atm.start();
	}

	private enum SessionState { IDLE, AUTHENTICATED, EXITED }

	private static final class Account {
		private final String number;
		private final String pin;
		private double balance;

		private Account(String number, String pin, double openingBalance) {
			this.number = number;
			this.pin = pin;
			this.balance = openingBalance;
		}

		private boolean verifyPin(String enteredPin) {
			return pin.equals(enteredPin);
		}
	}

	private static final class ATM {
		private static final int MAX_PIN_ATTEMPTS = 3;
		private final Account account;
		private final Scanner input = new Scanner(System.in);
		private SessionState state = SessionState.IDLE;

		private ATM(Account account) {
			this.account = account;
		}

		private void start() {
			System.out.println("Welcome to the ATM");
			if (!authenticate()) {
				state = SessionState.EXITED;
				System.out.println("Account locked. Session ended.");
				return;
			}

			state = SessionState.AUTHENTICATED;
			while (state == SessionState.AUTHENTICATED) {
				System.out.println("\n1. Check balance\n2. Withdraw\n3. Deposit\n4. Exit");
				switch (readInt("Choose an option: ")) {
					case 1 -> System.out.printf("Balance: $%.2f%n", account.balance);
					case 2 -> withdraw();
					case 3 -> deposit();
					case 4 -> endSession();
					default -> System.out.println("Invalid option.");
				}
			}
		}

		private boolean authenticate() {
			for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
				System.out.print("Enter PIN: ");
				if (account.verifyPin(input.nextLine().trim())) return true;
				System.out.println("Incorrect PIN. Attempts remaining: " + (MAX_PIN_ATTEMPTS - attempt));
			}
			return false;
		}

		private void withdraw() {
			double amount = readAmount("Withdrawal amount: ");
			if (amount <= 0 || amount > account.balance) {
				System.out.println(amount <= 0 ? "Amount must be positive." : "Insufficient funds.");
				return;
			}
			account.balance -= amount;
			System.out.printf("Please take your cash. New balance: $%.2f%n", account.balance);
		}

		private void deposit() {
			double amount = readAmount("Deposit amount: ");
			if (amount <= 0) {
				System.out.println("Amount must be positive.");
				return;
			}
			account.balance += amount;
			System.out.printf("Deposit accepted. New balance: $%.2f%n", account.balance);
		}

		private double readAmount(String prompt) {
			while (true) {
				try {
					System.out.print(prompt);
					return Double.parseDouble(input.nextLine().trim());
				} catch (NumberFormatException e) {
					System.out.println("Enter a valid amount.");
				}
			}
		}

		private int readInt(String prompt) {
			try {
				System.out.print(prompt);
				return Integer.parseInt(input.nextLine().trim());
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		private void endSession() {
			state = SessionState.EXITED;
			System.out.println("Thank you. Session ended securely.");
		}
	}
}
