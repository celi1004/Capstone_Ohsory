import os
import audio_denoise as denoise_model


foldername_data   = 'recorded_data'

dir_root         = os.path.dirname(os.path.realpath(__file__))
dir_unprocessed  = dir_root + '/' +foldername_data + '/'

list_unprocessed = os.listdir(dir_unprocessed)

for data_unprocessed in list_unprocessed :
    print(data_unprocessed)
    denoise_model.convert(dir_unprocessed, os.path.splitext(data_unprocessed)[0])