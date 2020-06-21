import os

dir_text = 'C:/Users/junhy/Desktop/textcollect/'
list_text = os.listdir(dir_text)
file = open("C:/Users/junhy/Desktop/textcollect/total.txt", 'w')
n = -1
for data_text in list_text :
    n = n+1
    if n == 0:
        continue
    f = open(dir_text + data_text, 'r')
    line = f.readline()
    file.write(str(n) + ' : ' + line)
    f.close()

file.close()