package nz.ac.vuw.swen301.tuts.log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * The purpose of this class is to read and merge financial transactions, and print a summary:
 * - total amount
 * - highest/lowest amount
 * - number of transactions
 * @author jens dietrich
 */
public class MergeTransactions {

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.getDefault());

	private static final Logger FILE_LOGGER = Logger.getLogger("FILE");
	private static final Logger TRANSACTIONS_LOGGER = Logger.getLogger("TRANSACTIONS");

	public static void main(String[] args) {
		List<Purchase> transactions = new ArrayList<Purchase>();

		// read data from 4 files
		readData("transactions1.csv", transactions);
		readData("transactions2.csv", transactions);
		readData("transactions3.csv", transactions);
		readData("transactions4.csv", transactions);

		// print some info for the user
		TRANSACTIONS_LOGGER.info("" + transactions.size() + " transactions imported");
		TRANSACTIONS_LOGGER.info("total value: " + CURRENCY_FORMAT.format(computeTotalValue(transactions)));
		TRANSACTIONS_LOGGER.info("max value: " + CURRENCY_FORMAT.format(computeMaxValue(transactions)));
	}

	private static double computeTotalValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v = v + p.getAmount();
		}
		return v;
	}

	private static double computeMaxValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v = Math.max(v, p.getAmount());
		}
		return v;
	}

	// read transactions from a file, and add them to a list
	private static void readData(String fileName, List<Purchase> transactions) {

		File file = new File(fileName);
		String line = null;
		// print info for user
		FILE_LOGGER.info("import data from " + fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Purchase purchase = new Purchase(
						values[0],
						Double.parseDouble(values[1]),
						DATE_FORMAT.parse(values[2])
				);
				transactions.add(purchase);
				// this is for debugging only
				FILE_LOGGER.debug("imported transaction " + purchase);
			}
		} catch (FileNotFoundException x) {
			// print warning
			FILE_LOGGER.warn("file " + fileName + " does not exist - skip");
		} catch (IOException x) {
			// print error message and details
			FILE_LOGGER.error("problem reading file " + fileName, x);
		} catch (ParseException x) {
			// print error message and details
			FILE_LOGGER.error("cannot parse date from string - please check whether syntax is correct: " + line, x);
		} catch (NumberFormatException x) {
			// print error message and details
			FILE_LOGGER.error("cannot parse double from string - please check whether syntax is correct: " + line, x);
		} catch (Exception x) {
			// any other exception
			// print error message and details
			FILE_LOGGER.error("exception reading data from file " + fileName + ", line: " + line, x);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// print error message and details
				FILE_LOGGER.error("cannot close reader used to access " + fileName, e);
			}
		}
	}
}