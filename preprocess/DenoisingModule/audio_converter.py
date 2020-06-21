import os
import subprocess
import shutil
from scipy.io import wavfile
from scipy.io.wavfile import write
import numpy as np

def audio_merge(dir_root, dir_denoised, dir_processed, audiofile):
    print(dir_root)
    print(dir_denoised)
    print(dir_processed)
    print(audiofile)
    list_denoised = os.listdir(dir_denoised)
    print(len(list_denoised))
    data_full_denoised = []
    for data_denoised in list_denoised:
        fs, data = wavfile.read(dir_denoised + '/' + data_denoised)
        data_full_denoised = np.append(data_full_denoised, data)
        print('appending')
        print(data_denoised)
        print(data.shape)
        print(data_full_denoised.shape)
        print(type(data_full_denoised))
    write(dir_processed + '/1' + audiofile, 16000, data_full_denoised)
    print('write_dir : ' + dir_processed + '/1' + audiofile )
    s = ['sox', dir_processed + '/1' + audiofile, '-r', '16000', '-b',  '16', '-e', 'signed-integer', dir_processed + '/' + audiofile]# 32 float
    subprocess.run(s)
    os.remove(dir_processed + '/1' + audiofile)


def audio_denoise(dir_root, dir_noise):
    # python senet_infer.py -d folder_name -m model_folder
    # os.chdir(dir_root)
    s = ['python', 'senet_infer.py', '-d', dir_noise, '-m', 'out_0606_4000_1000_180'] # out_0523_4000_1000_120
    subprocess.run(s)


def audio_cut(dir_root, dir_temp, audiofile):
    fs, data = wavfile.read(dir_temp + '/' + audiofile)
    length_data = data.shape[0]
    audiofile_noex = os.path.splitext(audiofile)[0]
    for i in range(1, length_data, 64000): 
        if i + 64000 > length_data:
            data_sliced = np.array(data[i:])
        else:
            data_sliced = np.array(data[i:i+64000])
        write(dir_temp + '/' +audiofile_noex + '_' +str(int(i/64000)) + '.wav'  , 16000, data_sliced)
    # for i in range(64000, length_data, 64000): 
    #     if i + 64000 > length_data:
    #         data_sliced = np.array(data[i-64000:])
    #     else:
    #         data_sliced = np.array(data[i-64000:i])
    #     write(dir_temp + '/' +audiofile_noex + '_' +str(int(i/64000)) + '.wav'  , 16000, data_sliced)
    os.remove(dir_temp + '/' + audiofile)
    audio_denoise(dir_root, dir_temp)

def convert(dir_root, dir_unprocessed, dir_processed, audiofile):
    audiofile_noex = os.path.splitext(audiofile)[0]
    dir_temp_noex = dir_root + '/' + audiofile_noex
    try:
        os.mkdir(dir_temp_noex)
    except OSError:
        shutil.rmtree(dir_temp_noex, ignore_errors=True)
        os.mkdir(dir_temp_noex)
    s = ['sox', dir_unprocessed + '/' + audiofile, '-r', '16000', '-b',  '32', '-e', 'float', dir_temp_noex + '/' + audiofile]
    subprocess.run(s)

    audio_cut(dir_root, dir_temp_noex, audiofile)
    audio_merge(dir_root, dir_temp_noex + '_denoised', dir_processed, audiofile)

    shutil.rmtree(dir_temp_noex, ignore_errors=True)
    shutil.rmtree(dir_temp_noex + '_denoised', ignore_errors=True)