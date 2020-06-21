import wave
import os

# The parameters are prerequisite information. More specifically,
# channels, bit_depth, sampling_rate must be known to use this function.
def pcm2wav( pcm_file, wav_file, channels=1, bit_depth=16, sampling_rate=16000 ):

    # Check if the options are valid.
    if bit_depth % 8 != 0:
        raise ValueError("bit_depth "+str(bit_depth)+" must be a multiple of 8.")
        
    # Read the .pcm file as a binary file and store the data to pcm_data
    with open( pcm_file, 'rb') as opened_pcm_file:
        pcm_data = opened_pcm_file.read()
        
        obj2write = wave.open( wav_file, 'wb')
        obj2write.setnchannels( channels )
        obj2write.setsampwidth( bit_depth // 8 )
        obj2write.setframerate( sampling_rate )
        obj2write.writeframes( pcm_data )
        obj2write.close()

dir = "./"
import os

def search(dirname):
    filenames = os.listdir(dirname)
    for filename in filenames:
        full_filename = os.path.join(dirname, filename)
        filenames2 = os.listdir(full_filename)
        for filename2 in filenames2:
            full_filename2 = os.path.join(full_filename,filename2)
            ext = os.path.splitext(full_filename2)[-1]
            
            if ext == '.pcm': 
                temp = full_filename2
                temp = temp.replace(".pcm",".wav")
                print(full_filename2)
                print(temp)
                pcm2wav( full_filename2, temp, 1, 16, 16000 )

search(dir)