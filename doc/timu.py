# coding=gb2312

import re
import os
from sets import Set

timu = r'<div class=".*">第[0-9]+题:<p>(.*)</p>'

timus = Set(['']) 

name="综合"
path="C:\\Users\\19002850.CORP\\Documents\\pmpway\\"+name  # insert the path to the directory of interest
dirList=os.listdir(path)
for fname in dirList:
	#print fname
	filepath = path+"\\"+fname
	if os.path.isdir(filepath) == True:
		continue
	fileIN = open(filepath, "r")
	line = fileIN.readline()
	while line:
		m = re.search(timu,line)
		if m is not None:
			timus.add(m.group(1)) 		
		line = fileIN.readline() 
	
print name + ": "+ str(len(timus)) + "/"+ str(len(dirList)) + "="+ str((len(timus)*100)/(len(dirList)*15))
	
 	