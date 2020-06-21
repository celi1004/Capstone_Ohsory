import os
import audio_converter as aconv


foldername_data   = 'recorded_data'

dir_root         = os.path.dirname(os.path.realpath(__file__))
dir_unprocessed  = dir_root + '/' +foldername_data
dir_processed    = dir_unprocessed + '_processed'

list_unprocessed = os.listdir(dir_unprocessed)
list_processed   = os.listdir(dir_processed)


for data_unprocessed in list_unprocessed :
    if data_unprocessed not in list_processed : 
        aconv.convert(dir_root, dir_unprocessed, dir_processed, data_unprocessed)
        