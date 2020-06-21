import os
import subprocess
import shutil
from scipy.io import wavfile
from scipy.io.wavfile import write
import numpy as np
import time
import matplotlib.pyplot as plt



# EXAMPLE
# voice_activity_dectection('C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/', 'testing_final_001')

def voice_activity_dectection(directory, audio_name):
    fs , data = wavfile.read(directory + audio_name + '.wav')
    data_max = np.max(abs(data))
    gap = 1600
    length_data = data.shape[0]
    sum = 0
    energy=[]
    data_norm=[] 
    max = 0

    for i in range(length_data):
        data_norm.append(data[i]/data_max)

    for i in range(length_data-gap+1):
        if i==0:
            for j in range(gap):
                sum = sum + data_norm[i+j]*data_norm[i+j]
        else:
            sum = sum - (data_norm[i-1]*data_norm[i-1]) + (data_norm[i+gap-1]*data_norm[i+gap-1])
        energy.append(sum)
        max = max if max>sum else sum

    for i in range(length_data-gap+1):
        energy[i] = energy[i]/max
    onCheck = False
    point_s = 0
    point_e = length_data-gap
    timer = -1
    cnt = 0
    total_time = 0


    X = range(length_data-gap+1)
    Y = energy
    line1 = plt.plot(X,Y,'k-',label='A',linewidth=1)
    plt.xlim(X[0],X[-1])
    plt.ylim(np.min(Y), np.max(Y))
    plt.show()



    for i in range(length_data-gap+1):
        if energy[i]>0.1:
            if not onCheck:
                onCheck=True
                point_s = i
            timer = 16000
        else:
            timer = timer-1
            if onCheck:
                if timer == 0:
                    point_e=i
                    onCheck=False
                    if point_e-point_s>24000: #safe margin
                        point_s_safe = point_s-3200
                        if point_s_safe < 0 :
                            point_s_safe = 0
                        write(directory + audio_name + '_' + str(cnt) + '.wav', 16000, data[point_s_safe:point_e])
                        total_time = total_time + point_e - point_s_safe
                        cnt = cnt+1
    total_time = float(total_time/16000)
    return cnt, total_time


# EXAMPLE
file_num, total_time = voice_activity_dectection('C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/', 'testing_final_001')
print(file_num)
print(total_time)