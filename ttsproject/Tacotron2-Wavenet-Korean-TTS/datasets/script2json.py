
import json
import collections.abc as collect
from collections import OrderedDict
import os

def script2json(username):

    json_data = OrderedDict()
    
    basedir = r"C:\Users\pjpp8\tts_project\ttsproject\Tacotron2-Wavenet-Korean-TTS\datasets"
    fullpath = os.path.join(basedir, username)
    dirlist = os.listdir(fullpath)
    
    for f in dirlist:

        ext = os.path.splitext(f)[1]
        filename = os.path.basename(f)
        name = os.path.splitext(f)[0]

        if ext == '.wav':
            full_filepath = "./Tacotron2-Wavenet-Korean-TTS/datasets/"+username+"/"+f
            text_path = os.path.join(basedir, username, name)
            try:
                with open(text_path+".txt", 'r', encoding="utf-8") as txtfile:

                    json_data[str(full_filepath)] = txtfile.readline()
                    txtfile.close
            except UnicodeDecodeError:
                with open(text_path+".txt", 'r') as txtfile:

                    json_data[str(full_filepath)] = txtfile.readline()
                    txtfile.close
            except FileNotFoundError:
                continue


    json_path = os.path.join(fullpath+'/alignment.json')
    with open(json_path, 'w', encoding="utf-8") as make_file:
        json.dump(json_data, make_file, ensure_ascii=False, indent='\t')
