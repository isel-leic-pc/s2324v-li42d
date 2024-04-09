package pt.isel.pc.jht.lecture18.bad3;

import java.util.concurrent.locks.*;

public class Singleton {
		private long ts = System.nanoTime();

		private long   number  = 0;
		private String message = "";

		public long   getNumber()  { return number; }
		public String getMessage() { return message; }

		public void printInfo(String prefix) {
			System.out.printf("%s { number: %d; message: %s }\n", prefix, getNumber(), getMessage());
		}

		private Singleton() {
			System.out.printf(":: CREATING THE INSTANCE (%d) ::\n", ts);
		}

		/* This is separated from the constructor for demo purposes only. */
		private void initialize() {
			System.out.printf(":: INITIALIZING THE INSTANCE (%d) ::\n", ts);
			sleep(2000);
			number = ts;
			sleep(2000);
			message = "ISEL";
		}

		/* Implementing the singleton pattern.
		 *
		 * Unfortunately, this version is just "almost" correct.
		 * Execution flexibility may end up swaping the effects
		 * of the instructions in lines 53 and 54, which will
		 * cause access errors (observing uninitialized fields). 
		 *
		 * In Kotlin, use "object".
		 */

		private static ReentrantLock lock = new ReentrantLock();

		private static Singleton theInstance = null;

		public static Singleton getInstance() {
			var instance = theInstance;
			if (instance == null) {
				lock.lock();
				try {
					instance = theInstance;
					if (instance == null) {
						instance = new Singleton();
						instance.initialize();
						theInstance = instance;
					}
				} finally {
					lock.unlock();
				}
			}
			return instance;
		}

		/* Testing code */
		public static void main(String[] args) {
			/*
			Singleton.getInstance().printInfo("[1]");
			Singleton.getInstance().printInfo("[2]");
			Singleton.getInstance().printInfo("[3]");
			*/
			thread(() -> { Singleton.getInstance().printInfo("[T1]"); });
			thread(() -> { Singleton.getInstance().printInfo("[T2]"); });
			thread(() -> { Singleton.getInstance().printInfo("[T3]"); });
			thread(() -> { sleep(1000); Singleton.getInstance().printInfo("[T4]"); });
			thread(() -> { sleep(3000); Singleton.getInstance().printInfo("[T5]"); });
			thread(() -> { sleep(5000); Singleton.getInstance().printInfo("[T6]"); });
		}

		private static void sleep(long ms) {
			try { Thread.sleep(ms); } catch (InterruptedException ie) {}
		}
		
		private static Thread thread(Runnable r) {
			final var th = new Thread(r);
			th.start();
			return th;
		}
}
