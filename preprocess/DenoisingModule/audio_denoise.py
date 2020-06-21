import os
import subprocess
import shutil
from scipy.io import wavfile
from scipy.io.wavfile import write
import numpy as np
import time

import matplotlib.pyplot as plt

### USAGE EXAMPLE   convert('C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/recorded_data/', 'mh97') 


def audio_merge(directory, audio_name, index):
    print('audio merge in!')
    print(time.time()-tt)
    data_full_denoised = []
    for i in range(int(index)+1):
        fs, data = wavfile.read(directory + audio_name  + str(i) + '.wav')
        data_full_denoised = np.append(data_full_denoised, data)
        os.remove(directory + audio_name  + str(i) + '.wav')
    write(directory + audio_name + '_temp.wav', 16000, data_full_denoised)
    s = ['sox', directory + audio_name + '_temp.wav', '-r', '16000', '-b',  '16', '-e', 'signed-integer', directory + audio_name + '.wav']# 32 float
    subprocess.run(s)
    print('audio merge out!')
    print(time.time()-tt)

def audio_denoise(audio_file):
    print('audio denoise in!')
    print(time.time()-tt)
    print('py : ' + os.path.dirname(os.path.realpath(__file__)) + '/senet_infer.py')
    print('audio file : ' + audio_file)
    # python senet_infer.py -d folder_name -m model_folder
    os.chdir(os.path.dirname(os.path.realpath(__file__)))
    s = ['python', 'senet_infer.py', '-d', audio_file, '-m', 'out_0606_4000_1000_180'] # out_0523_4000_1000_120
    subprocess.run(s)
    print('audio denoise out!')
    print(time.time()-tt)

def audio_cut(directory, audio_name):
    print('audio cut in!')
    print(time.time()-tt)
    fs , data = wavfile.read(directory + audio_name + '.wav')
    length_data = data.shape[0]
    for i in range(1, length_data, 64000): 
        if i + 64000 > length_data:
            data_sliced = np.array(data[i:])
        else:
            data_sliced = np.array(data[i:i+64000])
        index = str(int(i/64000))
        write(directory + audio_name + index + '.wav', 16000, data_sliced)
    os.remove(directory + audio_name + '.wav')
    audio_denoise(directory[:-1])
    shutil.rmtree(directory[:-1], ignore_errors=True)
    audio_merge(directory[:-1] + '_denoised/', audio_name, index)
    print('audio cut out!')
    print(time.time()-tt)

    
def voice_activity_dectection(directory, audio_name):
    fs , data = wavfile.read(directory + audio_name + '.wav')
    data_max = np.max(abs(data))
    gap = 1600
    gap_h = 800
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
    X = range(length_data-gap+1)
    Y = energy
    line1 = plt.plot(X,Y,'k-',label='A',linewidth=1)
    plt.xlim(X[0],X[-1])
    plt.ylim(np.min(Y), np.max(Y))
    plt.show()
    onCheck = False
    point_s = 0
    point_e = length_data-gap
    timer = -1
    cnt = 0
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
                    if point_e-point_s>24000:
                        write(directory + audio_name + '_' + str(cnt) + '.wav', 16000, data[point_s-3200:point_e])
                        cnt = cnt+1






# voice_activity_dectection('C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/', 'testing_final_001')
    
    
    



def convert(directory, audio_name):
    print('convert in!')
    print(time.time()-tt)

    audio_dir = directory + audio_name + '/'

    # try : 
    #     os.mkdir(audio_dir)
    # except FileExistsError :
    #     print('Folder may already exist. Please check your directory')
    #     return

    # voice_activity_dectection(audio_dir, directory + audio_name + '.wav')



    # # where for loop starts
    # audio_dir = audio_dir + audio_name + '/'
    
    try : 
        os.mkdir(audio_dir)
    except FileExistsError :
        print('Folder may already exist. Please check your directory')
        return

    s = ['sox', directory + audio_name + '.wav', '-r', '16000', '-b',  '32', '-e', 'float', audio_dir + audio_name + '.wav']
    subprocess.run(s)
    os.remove(directory + audio_name + '.wav')
    audio_cut(audio_dir, audio_name)
    os.rename(directory + audio_name + '_denoised/' + audio_name + '.wav', directory + audio_name + '.wav')
    shutil.rmtree(directory + audio_name + '_denoised/', ignore_errors=True)



### USAGE EXAMPLE   convert('C:\Users\junhy\Documents\2019\vsCodeTest\DenoisingModule\recorded_data\', 'mh97') 
tt = time.time();
# print('0')
# print(os.path.dirname(os.path.realpath(__file__)) + '/recorded_data/')
# convert(os.path.dirname(os.path.realpath(__file__)) + '/recorded_data/', 'mh4')
# print('END!')
# print(time.time()-tt)
