package com.mark;

//could use a 'Record' type here...
public class LogEntry {

	byte type = 0;
	int timestamp = 0;
	long id = 0;
	double amount = 0.0;

	public LogEntry(byte type, int timestamp, long id, double amount) {
		this.setType(type);
		this.setTimestamp(timestamp);
		this.setId(id);
		this.setAmount(amount);

	}

	public LogEntry() {
	};

	@Override
	public String toString() {

		return String.format("Record type: %s Timestamp: %s User ID: %s Amount$$: %s", type, timestamp, id, amount);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LogEntry)) {
			return false;
		}
		LogEntry entry = (LogEntry) obj;
		return this.id == entry.getId();
	}
	

	public byte getType() {return type;}
	public void setType(byte type) {this.type = type;}
	public int getTimestamp() {return timestamp;}
	public void setTimestamp(int timestamp) {this.timestamp = timestamp;}
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public double getAmount() {return amount;}
	public void setAmount(double amount) {this.amount = amount;}

}
