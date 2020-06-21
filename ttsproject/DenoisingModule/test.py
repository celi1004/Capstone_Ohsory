import os
import audio_denoise as denoise_model


foldername_data   = 'jinyoung-2'

dir_root         = os.path.dirname(os.path.realpath(__file__))
dir_unprocessed  = dir_root + '/' +foldername_data + '/'

list_unprocessed = os.listdir(dir_unprocessed)

for data_unprocessed in list_unprocessed :
    if os.path.splitext(data_unprocessed)[1] == ".wav":
        denoise_model.convert(dir_unprocessed, os.path.splitext(data_unprocessed)[0])

print("End-jinyoung")

foldername_data   = 'minhee-2'

dir_root         = os.path.dirname(os.path.realpath(__file__))
dir_unprocessed  = dir_root + '/' +foldername_data + '/'

list_unprocessed = os.listdir(dir_unprocessed)

for data_unprocessed in list_unprocessed :
    if os.path.splitext(data_unprocessed)[1] == ".wav":
        denoise_model.convert(dir_unprocessed, os.path.splitext(data_unprocessed)[0])

print("End-minhee")