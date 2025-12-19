Restaurant Order System
📖 Introduction
The Restaurant Order System is a desktop-based Java application designed to simulate real-world restaurant operations. It provides a graphical interface for managing tables, adding customer orders, calculating bills, and saving/loading data persistently. The project demonstrates object-oriented programming, GUI development with Swing, and custom data structures.

⚙️ Features
• 	Visual table layout with color-coded statuses:
• 	Green → Available
• 	Yellow → Pending
• 	Red → Served
• 	Add orders with item name, price, and quantity.
• 	Calculate and display total bill for each table.
• 	Save and load all tables and orders from file.
• 	Custom  implementation for order storage.

🛠️ Technologies Used
• 	Language: Java (JDK 17+ recommended)
• 	GUI Toolkit: Java Swing (, , , )
• 	IDE: VS Code / IntelliJ IDEA
• 	Data Structures: Custom 

🚀 Installation & Setup
1. 	Install Java JDK (17 or higher).
2. 	Clone or download the project repository.
3. 	Open the project in your IDE (VS Code, IntelliJ, Eclipse).
4. 	Compile and run the  class.

🔄 Workflow
1. 	Launch application → GUI initializes.
2. 	Tables and orders are loaded from file.
3. 	User selects a table via mouse click or combo box.
4. 	Add orders → stored in linked list.
5. 	Show bill → calculate total and display.
6. 	Save all data before exit.

📊 Performance Evaluation
• 	Environment: Windows 11, Java 17, VS Code
• 	Results:
    • 	Accurate table hit detection.
    • 	Orders appended correctly.
    • 	Bills calculated consistently.
    • 	Data persistence verified across sessions.
• 	Limitations:
    • 	Fixed layout size.
    • 	No multi-user support.
    • 	Limited input validation.

✅ Conclusion
The Restaurant Order System successfully integrates GUI interaction, custom data structures, and file I/O to simulate restaurant operations. It is a robust foundation for further enhancements.

⚠️ Limitations
• 	No multi-user support.
• 	Fixed layout size.
• 	No error handling for invalid input.

🔮 Scope of Future Work
• 	Add database support for scalable storage.
• 	Enable multi-user concurrency.
• 	Improve UI responsiveness and layout flexibility.
• 	Add reporting and analytics features.
