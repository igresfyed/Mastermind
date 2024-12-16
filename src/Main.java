import java.util.Random;
import java.util.Scanner;

class SecretCode {
    private String[] code;

    public SecretCode(int length, boolean useColors, boolean mixed) {
        this.code = generateCode(length, useColors, mixed);
    }

    private String[] generateCode(int length, boolean useColors, boolean mixed) {
        Random random = new Random();
        String[] code = new String[length];
        String[] colors = {"R", "G", "B", "Y", "P", "O"};
        for (int i = 0; i < length; i++) {
            if (mixed) {
                if (random.nextBoolean()) {
                    code[i] = String.valueOf(random.nextInt(6) + 1);
                } else {
                    code[i] = colors[random.nextInt(colors.length)];
                }
            } else if (useColors) {
                code[i] = colors[random.nextInt(colors.length)];
            } else {
                code[i] = String.valueOf(random.nextInt(6) + 1);
            }
        }
        return code;
    }

    public String[] getCode() {
        return this.code;
    }
}

class Guess {
    private String[] guess;

    public Guess(String input, int length, boolean useColors, boolean mixed) throws IllegalArgumentException {
        if (input.length() != length) {
            throw new IllegalArgumentException("Invalid guess length");
        }

        this.guess = new String[length];
        for (int i = 0; i < length; i++) {
            char ch = input.charAt(i);
            if (mixed) {
                if (!(Character.isDigit(ch) && ch >= '1' && ch <= '6') && !(ch >= 'A' && ch <= 'F')) {
                    throw new IllegalArgumentException("Invalid input character");
                }
            } else if (useColors) {
                if (!(ch >= 'A' && ch <= 'F')) {
                    throw new IllegalArgumentException("Invalid color input");
                }
            } else {
                if (!(Character.isDigit(ch) && ch >= '1' && ch <= '6')) {
                    throw new IllegalArgumentException("Invalid number input");
                }
            }
            this.guess[i] = String.valueOf(ch);
        }
    }

    public Guess(String[] formattedGuess, int length, boolean useColors, boolean mixed) {
    }

    public String[] getGuess() {
        return this.guess;
    }
}

class MastermindGame {
    SecretCode secretCode;
    int maxAttempts;
    boolean useColors;
    boolean mixed;

    public MastermindGame(int codeLength, int maxAttempts, boolean useColors, boolean mixed) {
        this.secretCode = new SecretCode(codeLength, useColors, mixed);
        this.maxAttempts = maxAttempts;
        this.useColors = useColors;
        this.mixed = mixed;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Mastermind!");
        System.out.println("Try to guess the secret code. You have " + maxAttempts + " attempts.");
        System.out.println("Use digits 1-6 for numbers and letters A-F for colors.");

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print("Attempt " + attempt + ": ");
            try {
                String input = scanner.nextLine().toUpperCase();
                Guess guess = new Guess(input, secretCode.getCode().length, useColors, mixed);
                int[] feedback = checkGuess(secretCode.getCode(), guess.getGuess());

                if (feedback[0] == secretCode.getCode().length) {
                    System.out.println("Congratulations! You guessed the secret code correctly.");
                    return;
                }

                System.out.println("Correct digits/colors in correct positions: " + feedback[0]);
                System.out.println("Correct digits/colors in wrong positions: " + feedback[1]);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                attempt--;
            }
        }

        System.out.print("Out of attempts! The secret code was: ");
        for (String elem : secretCode.getCode()) {
            System.out.print(elem);
        }
    }

    int[] checkGuess(String[] secretCode, String[] guess) {
        int correctPositions = 0;
        int correctNumbersOrColors = 0;
        boolean[] usedInSecret = new boolean[secretCode.length];
        boolean[] usedInGuess = new boolean[guess.length];

        for (int i = 0; i < secretCode.length; i++) {
            if (secretCode[i].equals(guess[i])) {
                correctPositions++;
                usedInSecret[i] = true;
                usedInGuess[i] = true;
            }
        }

        for (int i = 0; i < guess.length; i++) {
            if (usedInGuess[i]) continue;

            for (int j = 0; j < secretCode.length; j++) {
                if (!usedInSecret[j] && guess[i].equals(secretCode[j])) {
                    correctNumbersOrColors++;
                    usedInSecret[j] = true;
                    break;
                }
            }
        }

        return new int[]{correctPositions, correctNumbersOrColors};
    }
}

class Mastermind {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Game Mode:");
        System.out.println("1. Numbers Only");
        System.out.println("2. Colors Only");
        System.out.println("3. Mixed (Numbers and Colors)");

        int mode = scanner.nextInt();
        boolean useColors = mode == 2;
        boolean mixed = mode == 3;

        MastermindGame game = new MastermindGame(4, 10, useColors, mixed);
        game.start();
    }
}
