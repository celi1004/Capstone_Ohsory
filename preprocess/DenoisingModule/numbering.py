import os

dir_text = 'C:/Users/junhy/Desktop/textcollect_audio _1/'
list_text = os.listdir(dir_text)
n = 500

for data_text in list_text :
    n=n+1
    print(dir_text + data_text)
    print(dir_text + 'voice' + str(n) + '.wav')
    os.rename(dir_text + data_text, dir_text + 'voice' + str(n) + '.wav')