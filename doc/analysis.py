# coding=gb2312

import re
import os
from sets import Set
 
def format(input):
	out = input.split("</p>")[0]
	out = out.replace('<br>','')
	out = out.split("（分值")[0]
	return out
	 
def analysisf(filepath,fo):
	r_timu = r'<div class=".*">第[0-9]+题:<p>(.*)[</p>]?'
	r_c_a = r'<span class="pcur">A</span>(.*)</label>'
	r_c_b = r'<span class="pcur">B</span>(.*)</label>'
	r_c_c = r'<span class="pcur">C</span>(.*)</label>'
	r_c_d = r'<span class="pcur">D</span>(.*)</label>'
	r_c_right = r'正确答案：<span class="pcur">(.*)</span></div>'
	r_c_jd = r'试题解答：(.*)'
	fileIN = open(filepath, "r")
	line = fileIN.readline()
	t_start = False
	t_title_start = False
	t_title = ""
	c_a = ""
	c_b = ""
	c_c = ""
	c_d = ""
	c_right = ""
	c_jd = ""
	while line:
		if not t_start:
			m = re.search(r_timu,line)			
			if m is not None:  
				fo.write("\n\n") 
				t_title += format(m.group(1))
				fo.write(format(m.group(1) ))
				if "</p>" in line:
					fo.write("\n")
				else:
					t_start = True
					t_title_start = True
		else:
			if t_title_start:
				t_title += format(line)
				fo.write(format(line))
				if "</p>" in line: 
					fo.write("\n") 
					t_title_start = False
					t_start = False
			
		m = re.search(r_c_a,line)
		if m is not None:
			c_a = format(m.group(1))
			fo.write("A:"+c_a)
			fo.write("\n")
		m = re.search(r_c_b,line)
		if m is not None:
			c_b = format(m.group(1))
			fo.write("B:"+c_b)
			fo.write("\n")
		m = re.search(r_c_c,line)
		if m is not None:
			c_c = format(m.group(1))
			fo.write("C:"+c_c)
			fo.write("\n")
		m = re.search(r_c_d,line)
		if m is not None:
			c_d = format(m.group(1))	
			fo.write("D:"+c_d)
			fo.write("\n")
		m = re.search(r_c_right,line)
		if m is not None:
			c_right = format(m.group(1))	
			fo.write(c_right)
			fo.write("\n")
		m = re.search(r_c_jd,line)
		if m is not None:
			c_jd = format(m.group(1))	
			fo.write(c_jd)
			fo.write("\n")
			
		line = fileIN.readline() 		
	
	
outpath="E:\\TDDOWNLOAD\\zhejia\\pmpway\\analysisf.out"
fo = open(outpath, "wb") 
analysisf("E:\\TDDOWNLOAD\\zhejia\\pmpway\\12248_20120508_091835.htm",fo)
fo.close()