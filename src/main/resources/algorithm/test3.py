import threading
import time

times = []
threadLock = threading.Lock()
threads = []


def getTime(i):
    s = "ID: " + str(i) + ", time:"+ str(time.ctime(time.time()))
    threadLock.acquire()
    times.append(s)
    threadLock.release()


for i in range(10):
    t = threading.Thread(target=getTime, args=(i,))
    t.start()
    threads.append(t)

for t in threads:
    t.join()
print ("退出主线程")
print(times)