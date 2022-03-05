// Deron Gentles
// Programming Assignment #2
// Problem 2: Minotaur’s Crystal Vase (50 points)
// COP4520 Spring 2022

import java.util.*;
import java.util.concurrent.atomic.*;

public class MinotaurCrystalVase {

	public static ALock vaseLine;
	public ArrayList<Thread> threads;
	public static int numGuests;
	public static int num;

	public static void main(String[] args) {
		System.out.print("Enter the number of people who want to see the vase: ");
		Scanner stdin = new Scanner(System.in);
		String errStr = "ERROR: There needs to be at least 2 people to form a line";

		if (!stdin.hasNext())
			System.out.println(errStr);

		numGuests = stdin.nextInt();

		if (numGuests < 2)
			System.out.println(errStr);

		MinotaurCrystalVase showRoom = new MinotaurCrystalVase();
		showRoom.threads = new ArrayList<>();
		showRoom.vaseLine = new ALock(numGuests);

		// Make guests and add threads to a list
	 for (int i = 0; i < numGuests; i++) {
		 Guest guest = new Guest(showRoom, i);
		 Thread th = new Thread(guest);
		 showRoom.threads.add(th);
		 th.start();
	 }
	 System.out.println("The show is over go home.");

	}
}

class Guest implements Runnable {
	static MinotaurCrystalVase showRoom;
	int guestNum;
	boolean sawVase;
	boolean canGoAgain;

	public Guest(MinotaurCrystalVase showRoom, int guestNum) {
		this.showRoom = showRoom;
		this.guestNum = guestNum;
		this.sawVase = false;
		this.canGoAgain = false;
		// The first half of the line decides to go again because its a short wait
		int half = showRoom.numGuests / 2;
		if (guestNum < half) {
			this.canGoAgain = true;
		}
	}

	public void run() {
		while(true) {
			synchronized(this) {
				try {
					showRoom.vaseLine.lock();
					if (!sawVase) {
						// See the vase for the first time
						sawVase = true;
					}

					else if (canGoAgain) {
						canGoAgain = false;
					}

					else {
						showRoom.vaseLine.unlock();
						break;
					}
				} catch (Exception e) {
					return;
				}
			}
		}
	}
}

// The following code for an array-based queue lock is from Chapter 7 of the textbook
// The Art of Multiprocessor Programming. Fig 7.7
// Instead of implementing Lock the class extends an AbstractLock which I credit to github user javaf
// https://github.com/javaf/mcs-lock/blob/master/AbstractLock.java
class ALock extends AbstractLock {
	ThreadLocal<Integer> mySlotIndex = new ThreadLocal<Integer> () {
		protected Integer initialValue() {
			return 0;
		}
	};
	AtomicInteger tail;
	volatile boolean[] flag;
	int size;

	public ALock(int capacity) {
		size = capacity;
		tail = new AtomicInteger(0);
		flag = new boolean[capacity];
		flag[0] = true;
	}

	public synchronized void lock() {
		int slot = tail.getAndIncrement() % size;
		mySlotIndex.set(slot);
		while (! flag[slot]) {return;};
	}

	public synchronized void unlock() {
		int slot = mySlotIndex.get();
		flag[slot] = false;
		flag[(slot + 1) % size] = true;
	}
}
