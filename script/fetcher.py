import urllib2,codecs,string
from threading import Thread,Lock
from Queue import Queue
import time
 
class Fetcher:
    def __init__(self,threads):

        #proxy_support = urllib2.ProxyHandler({"http":"http://ip-static-94-242-251-110.as5577.net:8118"})
        #self.opener = urllib2.build_opener(proxy_support)
        #urllib2.install_opener(self.opener)
        self.opener = urllib2.build_opener(urllib2.HTTPHandler)

        self.lock = Lock() #线程锁
        self.q_req = Queue() #任务队列
        self.q_ans = Queue() #完成队列
        self.threads = threads
        for i in range(threads):
            t = Thread(target=self.threadget)
            t.setDaemon(True)
            t.start()
        self.running = 0
 
    def __del__(self): #解构时需等待两个队列完成
        time.sleep(0.5)
        self.q_req.join()
        self.q_ans.join()
 
    def taskleft(self):
        return self.q_req.qsize()+self.q_ans.qsize()+self.running
 
    def push(self,req):
        self.q_req.put(req)
 
    def pop(self):
        return self.q_ans.get()
 
    def threadget(self):
        while True:
            req = self.q_req.get()
            with self.lock: #要保证该操作的原子性，进入critical area
                self.running += 1
            try:
                ans = self.opener.open(req).read()
            except Exception, what:
                ans = ''
                print what
            self.q_ans.put((req,ans))
            with self.lock:
                self.running -= 1
            self.q_req.task_done()
            time.sleep(0.1) # don't spam
 
if __name__ == "__main__":

 
    links = [ 'http://www.xxx2fff.cn/%d.shtml'%i for i in range(6173,9999) ]

    f = Fetcher(threads=1)
    for url in links:
        f.push(url)
    while f.taskleft():
        url,content = f.pop()
        if not "404 Not Found" in content:
            fp = open("D:/tmp/f202/web1/"+string.split(url,"/")[4],mode='w+')
            fp.write(content)
            fp.close() 	  
        else:
            print(string.split(url,"/")[4])      

