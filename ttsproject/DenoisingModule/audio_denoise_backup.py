import os
import subprocess
import shutil
from scipy.io import wavfile
from scipy.io.wavfile import write
import numpy as np
import time 

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
    

def convert(directory, audio_name):
    print('convert in!')
    print(time.time()-tt)
    audio_dir = directory + audio_name + '/'

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
print('0')
convert('C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/recorded_data/', 'voice_013')
print('Finish')
print(time.time()-tt)