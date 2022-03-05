// Deron Gentles
// Programming Assignment #2
// Problem 1: Minotaur’s Birthday Party (50 points)
// COP4520 Spring 2022

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty {

	static boolean cupcake = true;
	static boolean stillGoing;
	static int numGuests;
	static int totalTries; // Total tries it took to get everyone through the maze
	public static ReentrantLock lock = new ReentrantLock(false);
	public ArrayList<Thread> threads;

	public static void main(String[] args) {
		System.out.print("Enter the number of guest invited to the birthday party: ");
		Scanner stdin = new Scanner(System.in);
		String errStr = "ERROR: No one invited to the party. Invite at least 2 ";

		if (!stdin.hasNext()) {
			System.out.println(errStr);
			return;
		}

		numGuests = stdin.nextInt();

		if (numGuests < 2) {
			System.out.println(errStr);
			return;
		}

		MinotaurBirthdayParty party = new MinotaurBirthdayParty();
		ArrayList<Guest> minotaur = new ArrayList<>();
		party.threads = new ArrayList<>();
		party.totalTries = 0;
		stillGoing = true;

		 // Make guests and add threads to a list
		for (int i = 0; i < numGuests; i++) {
			Guest guest = new Guest(party, i);
			Thread th = new Thread(guest);
			party.threads.add(th);
			th.start();
			minotaur.add(guest);
		}

		boolean keepPartyGoing = true;
		Random rng = new Random();

		while(keepPartyGoing) {
			int randGuess = rng.nextInt(numGuests);
			Guest guest = minotaur.get(randGuess);
			guest.inLabyrinth = true;
			totalTries++;

			if (!stillGoing) {
				keepPartyGoing = false;
			}
		}

		System.out.println("It took a total of " + totalTries + " to figure out if every guest entered the labyrinth");
	}

	public void eatCupcake() {
		this.cupcake = false;
	}

	public void requestNewCupcake() {
		this.cupcake = true;
	}

	public void endParty() {
		this.stillGoing = false;

		for (Thread th : threads) {
			try {
				th.interrupt();
			} catch(Exception e) {
				System.out.println("In endParty " + e);
			}
		}

		System.out.println("The party is over! All " + numGuests + " guest have entered the labyrinth.");
	}
}


class Guest implements Runnable {
	static MinotaurBirthdayParty party;
	static int guestCount; // Only Thread 1 can increment the counter
	boolean hadCupcake; // A guest only has one cupcake so the leader can count who has visisted the maze
	boolean isLeader; // Thread 1 is the leader
	boolean inLabyrinth;
	int guestNumber;

	public Guest(MinotaurBirthdayParty party, int guestNumber) {
		this.party = party;
		this.guestCount = 0;
		this.hadCupcake = false;
		this.guestNumber = guestNumber;
		this.isLeader = false;
		this.inLabyrinth = false;

		if (guestNumber == 1)
			this.isLeader = true;
	}

	//  When a guest finds a way out of the labyrinth, he or she has the following options:
	// If there is a cupcake:
	// (1) Eat it if they have not had one already
	// (2) If its the leader increase the count after eating
	// No cupcake:
	// (1) Leader increases count and requests new cupcake
	// (2) Others just leave it
	@Override
	public void run() {
			while (true) {
				try{
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					return;
				}

				synchronized(this) {
					if(inLabyrinth) {
						party.lock.lock();
						try {

							if (isLeader && guestCount == party.numGuests) {
								party.totalTries++;
								party.endParty();
								return;
							}

							if (party.cupcake) {

								if (!hadCupcake) {
									party.eatCupcake();
									party.totalTries++;
									hadCupcake = true;

									if (isLeader) {
										guestCount++;
										party.requestNewCupcake();
									}
								}
							}

							else if (isLeader) {
								guestCount++;
								party.totalTries++;
								//System.out.println("Yea something is fishy");
								party.requestNewCupcake();
							}

							if (isLeader && guestCount == party.numGuests) {
								party.totalTries++;
								party.endParty();
								return;
							}
							inLabyrinth = false;
						} finally {
							party.lock.unlock();
						}
					}
				}

			}
	}
}
