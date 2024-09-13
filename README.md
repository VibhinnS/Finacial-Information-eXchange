A Financial Information eXchange protocol on Java. 
Run the following commands to start - 

1.) `mvn clean install`
2.) `mvn test`
3.) `mvn clean complie`
4.) `mvn exec:java`

( the last command creates a .jar standalone file in the target dir )

Make sure you get - **FIX Protocol running on port 5000** in the terminal

In a separate terminal, use `cd` command to enter into main dir and run - `python autotest.py` to test clients. See if you get the desired output in the FIX terminal. 
