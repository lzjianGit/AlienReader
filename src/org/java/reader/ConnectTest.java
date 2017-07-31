package org.java.reader;

import com.alien.uhf.MinaSerialSingle;

public class ConnectTest {
	
	public static void main(String[] args) {
		ReadWriteTest read = new ReadWriteTest();
		read.before();
		read.First_ReadTID();
		read.Second_ReadEPC();
		read.after();
	}
}
