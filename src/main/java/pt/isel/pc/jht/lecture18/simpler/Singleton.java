package pt.isel.pc.jht.lecture18.simpler;

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
		 * In Kotlin, use "object".
		 */

		public static final Singleton instance = createInstance();

		private static Singleton createInstance() {
			var theInstance = new Singleton();
			theInstance.initialize();
			return theInstance;
		}

		/* Testing code */
		public static void main(String[] args) {
			thread(() -> { Singleton.instance.printInfo("[T1]"); });
			thread(() -> { Singleton.instance.printInfo("[T2]"); });
			thread(() -> { Singleton.instance.printInfo("[T3]"); });
			thread(() -> { sleep(1000); Singleton.instance.printInfo("[T4]"); });
			thread(() -> { sleep(3000); Singleton.instance.printInfo("[T5]"); });
			thread(() -> { sleep(5000); Singleton.instance.printInfo("[T6]"); });
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
