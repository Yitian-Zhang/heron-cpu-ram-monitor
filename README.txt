*************************************************************
	Name: CPU & RAM USAGE MONITOR SHELL TOOLS
                   Install and configuration
*************************************************************
1. Copy the root directory that named "monitor" to each VM that you want to monitor
their CPU and Memory usage during running Heron topology.

2. Configurate the hostname (for each workernode): modified the "node-name" in the "monitor-conf.ini" file.

3. Running this shell:
[1] put your .jar file to the "/monitor/bash-shell" directory.
[2] chmod +x this jar file
[3] running the commands:
Starting the monitor:
	yitian@heron01:~/monitor/bash-shell$ ./start-monitor.sh 
Stopping the monitor:
	yitian@heron01:~/monitor/bash-shell$ ./stop-monitor.sh 

4. Data collection:
	If you ran the start command, you can get the cpu and ram monitor data in "/monitor/cpu-usage" and "/monitor/memory-usage", they named "cpu-usage-data.txt" and "memory-usage-data.txt". And the you can copy these data file to WINS OS, where you can change these TXT data to EXCEL data using "ExportCPUUsage.java" and "ExportMemoryUsage.java" in the "heron-scheduler-data-analysis" java project.

*************************************************************
PAY ATTENTION: 
	Do not starting this monitor many times, the best is you running this shell only one time. Otherwise, the monitor data will stange for you, because there will many java process to monitor this VM. 
*************************************************************
