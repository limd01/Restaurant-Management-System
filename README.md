# CS-2114-Project-3

Line and Dine

## How to Download and Run in Eclipse

1. Go to our GitHub repository and click **Code → Download ZIP**.
2. Extract the ZIP file.
3. Open Eclipse and go to **File → Import → General → Existing Projects into Workspace**.
4. Select the project folder containing `.project` and `src`.
5. Click **Finish**.
6. Right-click `Main.java` and choose **Run As → Java Application**.
7. To run tests, right-click a test class and choose **Run As → JUnit Test**.

---

## Reflection

### What Changed

We separated our program into different classes to make it easier to manage. We used `DLinkedDeque` and `WaitQueue` for the waiting list, and added `HostController`, `ConsoleView`, and `Main` to run the program.

We also added validation to reject invalid names, party sizes, and other bad inputs.

Our program now supports adding parties, seating customers, showing restaurant status, registering VIP members, and canceling waiting parties.

### What We Learned

We learned how to use a deque for a waiting list and how to connect different classes. We also learned how to write tests and handle invalid inputs.

We learned how to connect the console menu to the restaurant system.

### Does the Model Work Independently?

Our model does not depend on the console view. We can test classes like `WaitQueue`, `Reservation`, and `Table` separately.

We ran our test classes in Eclipse, and all tests passed. We also ran `Main.java` and checked that the console program works.

### What Does Not Work Yet

We still need to finish the complete food ordering system and CSV saving and loading.

We also want to test more situations and improve the program for the next deliverable.

---

## Next Steps

### 1. Finishing the MVP

We plan to finish these features in this order:

1. Test more situations and fix any remaining problems.
2. Improve reservation and table management.
3. Finish ordering and checkout.
4. Improve VIP member verification.
5. Add CSV saving and loading.

### 2. New Change Feature

We plan to add a **skip feature** so the host can move a waiting party back in line.

We think this will require changes to:

- `WaitQueue`: Add a method to skip a party.
- `Restaurant`: Handle skipping reservations.
- `HostController`: Connect the skip feature.
- `ConsoleView`: Add a skip option to the menu.

We do not expect to change `Reservation`, `Table`, or `MenuItem`.

### 3. Stretch Goals

We will focus on finishing the MVP first. We will keep CSV saving and loading as a planned feature and drop extra features like a graphical interface for now.

### 4. New Tests Needed

We already have tests for `WaitQueue`, `Table`, `Reservation`, `MenuItem`, `DLinkedDeque`, `Restaurant`, and `HostController`.

Our existing tests passed in Eclipse.

For the next deliverable, we plan to:

- Add more `WaitQueueTest` tests for the new skip feature.
- Add more `RestaurantTest` tests for seating and checkout.
- Add more `TableTest` tests if needed.
- Add more `HostControllerTest` tests for new features.
- Create tests for CSV saving and loading when implemented.

We will also test more invalid inputs to make sure the program does not crash.

### 5. Division of Work

| **Member** | **Next Tasks** |
| --- | --- |
| Anna-Maria | Restaurant, WaitQueue, skip feature |
| Chandrika | Reservations, tables, ordering, tests |
| Don | Controller, console view, Main, integration testing |
| Everyone | Test the program and fix errors |

---

## Attribution

| **Class** | **Who Worked on it** | **How** |
| --- | --- | --- |
| DLinkedDeque | Anna-Maria | Wrote and revised the class |
| DLinkedDequeTest | To confirm | Wrote tests for the deque |
| MenuItem | Chandrika | Wrote and tested the class with GenAI assistance |
| MenuItemTest | Chandrika | Wrote test cases with GenAI assistance |
| Restaurant | Anna-Maria | Wrote and revised the class |
| RestaurantTest | Chandrika | Wrote test cases with GenAI assistance |
| Reservation | Chandrika | Wrote and tested the class with GenAI assistance |
| ReservationTest | Chandrika | Wrote test cases with GenAI assistance |
| Table | Chandrika | Wrote and tested the class with GenAI assistance |
| TableTest | Chandrika | Wrote test cases with GenAI assistance |
| WaitQueue | Anna-Maria | Wrote and revised the class |
| WaitQueueTest | Don, Chandrika | Wrote and updated tests with GenAI assistance |
| HostController | Don | Wrote the controller with GenAI assistance |
| HostControllerTest | Chandrika | Wrote test cases with GenAI assistance |
| ConsoleView | Don | Wrote the console menu with GenAI assistance |
| Main | Don | Wrote the class to start the program with GenAI assistance |
| GitHub Repository | Don | Created and set up the repository |
| Attribution Table | Chandrika | Made the original table |

---

## GenAI Note

We used Claude and ChatGPT to help with coding, test cases, and improving our README. We reviewed the suggestions and made changes to fit our project. We ran our tests in Eclipse, and all tests passed. We also ran the program and checked that its main features worked.
