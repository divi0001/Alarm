(only for internal info rn)
cd C:\Users\[...]\AppData\Local\Android\Sdk\platform-tools
tasklist
Taskkill /PID [PID HERE] /F

When AndroidStudio doesnt launch, because you had the avd running while exiting, after opening androidStudio, taskkill the pid of adb, so it can restart :)