import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

/**
 * This is a program for a hangman game that uses a csv file for selecting a word.
 *
 * When a word is chosen, an array is created with length equal to the amount of letters inside the chosen word.
 * The array is then populated with symbols and when the player successfully guesses a letter inside the chosen word, the symbols inside the array are replaced with the correct letters.
 */
public class HangmanGame
{
    public static void main(String[] args)
    {
        GameManager();
        return;
    }

    // The csv file containing all the words.
    static File hangmanFile = new File("HangmanWords.csv");

    // Words to be used for the game
    static ArrayList<String> wordsToPickForGame = new ArrayList<String>();

    // Pseudo random number generator
    static Random psrng = new Random();

    // Gameplay bookkeeping variables
    static int totalGamesPlayed = 0;
    static String gameWord; // Stores the chosen word
    static char encryptionSymbol = '-'; // Displayed when the letter of a word has not been guessed.

    // Will store the char indexes for the user response.
    static char[] currentlyDecodedWord;

    /**
     * Handles the functions to call for the game.
     */
    static void GameManager()
    {
        Scanner userMenuInput = new Scanner(System.in);
        int userMenuChoice = 0;

        while (userMenuChoice != 4)
        {
            // First create the word list.
            CreateWordList();

            // Print the things the player/user can do.
            PrintUserMenu();
            userMenuChoice = userMenuInput.nextInt();

            if(userMenuChoice == 1)
            {
                PlayGame();
            }
        }
        return;
    }

    /**
     * Goes through the HangmanWords.csv file and adds the words
     * inside that file to our ArrayList of words.
     */
    static void CreateWordList()
    {
        // Clear our old list
        wordsToPickForGame.clear();

        // Add the words to our list of words (if we can)
        try
        {
            Scanner inputStream = new Scanner(hangmanFile);
            while(inputStream.hasNext())
            {
                String word = inputStream.next();
                wordsToPickForGame.add(word);
            }
            inputStream.close();
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
    }

    /**
     * Prints the menu of things the user can do.
     */
    static void PrintUserMenu()
    {
        System.out.println("HANGMAN GAME");
        System.out.println("\n1 -- Play a game");
        System.out.println("4 -- Quit\n");
        return;
    }

    /**
     * Handles the gameplay.
     */
    static void PlayGame()
    {
        // Reset the needed game variables

        Scanner scnr = new Scanner(System.in);

        int maxChances = 5;
        int currentChancesRemaining = maxChances;

        // Get a random word from our list of words
        int index = psrng.nextInt(wordsToPickForGame.size());
        gameWord = wordsToPickForGame.get(index);

        /* The length of the decoded array == to the length of the chosen word. */
        currentlyDecodedWord = new char[gameWord.length()];

        /* Add the character at the index of the chosen word into
         * secret word array */
        for(int i = 0; i < gameWord.length(); i++)
        {
            currentlyDecodedWord[i] = encryptionSymbol;
        }
        System.out.println();

        totalGamesPlayed++; // Increment amount of games played.

        // Checks if the user has won.
        boolean victory = false;

        while(currentChancesRemaining > 0)
        {
            // If currentlyDecodedWord is fully decoded, then the user wins.
            if(WordIsDecoded() == true)
            {
                victory = true;
                break;
            }

            System.out.printf("\nCurrent guesses remaining: %d\n", currentChancesRemaining);

            System.out.println("\nWhat you currently have:");

            // Print the indices currently decoded word
            for(int x = 0; x < currentlyDecodedWord.length; x++)
            {
                System.out.printf(" %s ", currentlyDecodedWord[x]);
            }
            System.out.println();

            /* NOTE: For debugging purposes, show the word that was chosen.
            *        Remember to comment this out for the "real" thing. */
            System.out.printf("\n%s\n", gameWord);

            System.out.println("\nTry to guess the word.");
            String userResponse = scnr.next();

            // Convert user entry to uppercase
            String convertedUserResponse = userResponse.toUpperCase();

            // If the user guessed the word correctly, the user wins
            if(convertedUserResponse.equals(gameWord))
            {
                victory = true;
                break;
            }

            // Otherwise, check what the user has entered and subtract the remaining chances as needed.
            else
            {
                currentChancesRemaining -= CheckUserEnteredWord(convertedUserResponse);
            }
        }

        if(victory == true)
        {
            System.out.println("\nYou correctly guessed the word!\n");
        }
        else
        {
            System.out.println("\nYou could not guess the word correctly.");
            System.out.printf("\nThe correct word was: %s\n", gameWord);
        }
        return;
    }

    /**
     * Indexes the user entered string (or char) to the chosen word. If the char at an index is located in the chosen word,
     * the encryption symbol(s) at the corresponding index in the currentlyDecodedWord array is replaced with that char.
     *
     * The variable "valueToReturn" is incremented when the user entered string or char is not located within the secret.
     */
    static int CheckUserEnteredWord(String entryToCheck)
    {
        int valueToReturn = 0; // When the indexed char is not inside the chosen word, this value will be raised.
        boolean indexedCharIsInChosenWord = false;

        if(entryToCheck.length() == 1)
        {
            for(int i = 0; i < currentlyDecodedWord.length; i++)
            {
                if(entryToCheck.charAt(0) == gameWord.charAt(i))
                {
                    indexedCharIsInChosenWord = true;
                    currentlyDecodedWord[i] = entryToCheck.charAt(0);
                }
            }

            if(indexedCharIsInChosenWord == false)
            {
                valueToReturn += 1;
            }
        }

        else if(entryToCheck.length() > 1)
        {
            for(int i = 0; i < entryToCheck.length(); i++)
            {
                for(int j = 0; j < currentlyDecodedWord.length; j++)
                {
                    if(entryToCheck.charAt(i) == gameWord.charAt(j))
                    {
                        indexedCharIsInChosenWord = true;
                        currentlyDecodedWord[j] = entryToCheck.charAt(i);
                    }

                    else if(entryToCheck.charAt(i) != gameWord.charAt(j))
                    {

                    }
                }

                if(indexedCharIsInChosenWord == false)
                {
                    valueToReturn += 1;
                }
            }
        }

        return valueToReturn;
    }

    /**
     * Checks if currentlyDecodedWord is completely decoded
     */
    static boolean WordIsDecoded()
    {
        int i = 0;
        boolean wordIsDecoded = false;
        for(i = 0; i < currentlyDecodedWord.length; i++)
        {
            if(encryptionSymbol == currentlyDecodedWord[i])
            {
                wordIsDecoded = false;
                break;
            }

            else
            {
                wordIsDecoded = true;
            }
        }

        return wordIsDecoded;
    }
}
