#!/usr/bin/python
#whois.py
import sys
import socket

def query(name): 
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	#for com,net后缀的域名
	s.connect(("whois.internic.net", 43))
	s.send(name)
	response = ''
	while True:
		data = s.recv(4096)
		response += data
		if data == '':
			break
	if "No match for" in response:
		print name
	s.close()
	return response
	
 
#for .org 后缀的域名
#s.connect(("whois.publicinterestregistry.net", 43))
#for .cn 后缀的域名
#s.connect(("whois.cnnic.net.cn", 43))
'''另外其他后缀的whois server暂时还没有找到，下面的一些whois server，应该是有ip访问许可，并不对普通用户开放。
'''
#s.connect(("whois.networksolutions.com", 43))
#s.connect(("whois.onlinenic.com", 43))
#s.connect(("grs.hichina.com",43))
for i in range(0, 99): 
	query(str(i) + "edp.com" + "\r\n") 

#for i in range(0, 99): 
#	query("pmp"+str(i) + ".com" + "\r\n") 

 