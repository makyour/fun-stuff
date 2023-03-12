package com.mark;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author ****
 * 
 * 'BinaryParser' Utility class 
 * - Read the file 'readFile(path)'
 * - Parse out the binary records
 * - Print the report to terminal
 *
 */
public class BinaryParser {

	int startAutoPay, endAutoPay = 0;
	double totalCredit, totalDebit, totalUserBalance = 0.0;
	Long userBalance = new Long("2456938384156277127");
	private static String MAGIC = "MPS7";
	ArrayList<LogEntry> records = new ArrayList<LogEntry>();

	public boolean readFile(Path path) throws IOException {

		System.out.println("\nReading data from " + path.toString());
		
		boolean response = false;

		try (SeekableByteChannel sbc = Files.newByteChannel(path)) {

			ByteBuffer buf = ByteBuffer.allocate(256 * 1024);// blind

			sbc.read(buf);
			buf.flip();

			byte[] magic = new byte[4];
			buf.get(magic, 0, 4);
			byte version = buf.get();
			int recordCount = buf.getInt();

			// verify protocol
			if (new String(magic).equals(MAGIC)) {
			
				System.out.println(String.format("Header: magic=%s version=%d record-count=%d", new String(magic), version, recordCount));
				response = parseRecords(buf, recordCount);

			} else {
				System.out.println("ERROR: Read Failed, Ivalid Protocol. Exiting the app");
				System.exit(0);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		return response;
	}

	public boolean parseRecords(ByteBuffer buf, int recordCount) throws IOException {

		byte type = -1; // (0,1,2,3)
		int timestamp = 0;
		long id = 0;
		double amount = 0.0;
		boolean response = false;
		LogEntry entry = null;

		try {
			/* We have no EOF or Record delimiter so its byte-by-byte with little error handling*/
			for (int i = 0; i < recordCount; i++) {

				timestamp = 0;
				id = 0;
				amount = 0;
				type = -1;

				type = buf.get();
				timestamp = buf.getInt();
				id = buf.getLong();

				if (type == 0 || type == 1)//only move the buffer pointer for Debit/Credit
					amount = buf.getDouble();

				entry = new LogEntry(type, timestamp, id, amount);
				processType(type, entry);
				records.add(entry);

				//System.out.println(entry.toString());

			}
			response = true;
		} catch (java.nio.BufferUnderflowException ex) {

			System.out.println(
					String.format("Overrun Exception  !! %d Records processed " + "\n Type=%d Timestamp=%d ID=%d\n",
							recordCount, type, timestamp, id));
			System.out.println(ex.getMessage());
		}
		return response;

	}

	public void printReport() {
		/*
		 * total credit amount=0.00 total debit amount=0.00 autopays started=0 autopays
		 * ended=0 balance for user 2456938384156277127=0.00
		 */

		System.out.println(String.format(
				"\nTotal credit amount = $%,.2f\n" + "Total debit amount =  $%,.2f\n" + "Autopays Started = %s\n" + "Autopays Ended = %s\n"
						+ "Balance for user %s = $%,.2f\n ",
				this.totalDebit, this.totalCredit, this.startAutoPay, this.endAutoPay, userBalance,calculateBalance(userBalance)));
	}

	public double calculateBalance(Long id) {

		double balance = 0.0;
		Iterator<LogEntry> iterator = records.iterator();
		
		
		//using streams
		records.stream().filter(entry -> entry.getId() == id)
        .forEach(entry -> totalUserBalance += entry.getAmount());

		//iterator + loop
	/*while (iterator.hasNext()) {
			LogEntry entry = iterator.next();
			if (entry.getId() == id) {
				balance += entry.getAmount();
				
				//System.out.println(String.format("Using loop for %s is %s ", id, balance));
			}
		}*/
		return totalUserBalance;
	}

	public void processType(int type, LogEntry entry) {
		switch (type) {
		case 0:// Debit
			this.totalDebit += entry.getAmount();
			break;

		case 1:// Credit
			this.totalCredit += entry.getAmount();
			break;

		case 2:// StartAP
			this.startAutoPay++;
			break;
		case 3:// EndAp
			this.endAutoPay++;
			break;

		default:
			System.out.println("ERROR: INVALID RECORD TYPE " + entry.toString());
			break;
		}
	}
}
