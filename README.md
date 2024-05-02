# Mind Squares App

Mind Squares App is an application that 
allows users to create 
a mind map or a simple visual plan of 
anything they'd like.

## The goal of this project
This project's main focus was to create a simple
app with GUI, which would be user-friendly and 
which would run reliably. The app should allow
users to create simple mind maps, with enough
freedom to move items around, and to add new items 
or to remove them. 

I chose Java for this project because I wanted to
improve my Java programming skills.

## Instalation of the program
### 1. Initialise the database
All data is saved in a simple database, which
can be initialised using Docker. First start 
the [Docker Daemon](https://docs.docker.com/config/daemon/start/)
and then run

  ```bash
  docker compose up
  ```
With the database, [Adminer](https://www.adminer.org/) 
is initialised, which allows for a simple data
visualisation.

**NOTE**: You can also initialise the database differently,
using the `init.sql` directly.

**NOTE 2**: If you don't need to save your progress you can
run the app without initialising the database. Just make
sure **not** to click the `Save` and `Load` buttons, as that
would crash the app.

### 2. Run the app
#### IDE
You can run the app in an IDE which supports Java, 
such as [IntelliJ](https://www.jetbrains.com/idea/)
and then running the ```MindSquares.java``` file.

#### Command line
If you want to run the program using the command
line, first make sure you have the 
[Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/#jdk22-linux)
installed. You might need to set some PATH after installation
in order for `javac` to work. ChatGPT can help with that for your
particular OS.

1. **Compile your Java code**:
Navigate to the folder with saved class files, then run:
   ```
   javac -d output_dir mySquareApp/MindSquares.java
   ```

   Replace `output_dir` with the directory where you want the compiled class files to be placed.

2. **Navigate to the output folder**:
   ```
   cd output_dir
   ```

3. **Run your Java application**:
   ```
   java -cp "../../postgresql-42.7.2.jar;." mySquareApp.MindSquares
   ```
   Assuming you have the postgresql driver in the same folder as this repository.
   
Make sure that the database is running before starting
the app, as otherwise your wonderful creation will not
be saved. If you have problems connecting to the database,
try restarting the database container.

## Features
- **Add Square:** Click the "Add □" button to add a new square to the canvas.
- **Remove square:** Right-click the square to remove it and its connections.
- **Resize Square:** Toggle the "Resize" mode and use the "+" and "-" buttons to resize the selected square.
- **Move Square:** Click and drag a square to move it around the canvas.
- **Edit Square Text:** Double-click a square to edit its text.
- **Connect Squares:** Click one node and then another to create a connection between squares. For removing the connection, use right-click.
- **Save and Load:** Save the current state of the canvas to the database and load it later.

## Database structure
There are two simple tables:
- **Connections** - stores the start and end points of each connecting line
- **Squares** - stores the squares with their particular size, colour and text

When the `Save` button is clicked, the app rewrites
all entries in the database. Similarly, when the `Load`
button is clicked, all data on canvas is 
rewritten by the data
from the database.

## Sources
Java Swing - I found the introduction to Java Swing at
[Geeks for Geeks](https://www.geeksforgeeks.org/introduction-to-java-swing/).
I also watched parts of several YouTube tutorials, such as
[this one](https://www.youtube.com/watch?v=Kmgo00avvEw&ab_channel=BroCode).
Then, I mostly used the detailed information outlined at
[Java Point](https://www.javatpoint.com/java-swing).
I was quite disappointed with the performance 
of ChatGPT 3.5
during the debugging process, so most 
of the issues I encountered I solved by searching
on StackOverflow.

## About the project
This project was created by Katka Morovicsová
during the winter semester 2023/2024 at FJFI CVUT
(specialisation: Mathematical informatics),
as part of Computer Graphics 1 (PGR1) class.