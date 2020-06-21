import requests
import json
import os
import sys
import numpy as np
import chardet
import shutil



kakao_speech_url = "https://kakaoi-newtone-openapi.kakao.com/v1/recognize"

rest_api_key = 'a43694e772d7ee356827ce32324f267e'

headers = {
    "Content-Type": "application/octet-stream",
    "X-DSS-Service": "DICTATION",
    "Authorization": "KakaoAK " + rest_api_key,
}

def speech_to_text(directory, audio_name):
    print('hello')
    try : 
        with open(directory + audio_name + '.wav', 'rb') as fp:
            audio = fp.read()
    except FileNotFoundError :
        print('Please check your audiofile and it\'s directory')
        return

    res = requests.post(kakao_speech_url, headers=headers, data=audio)
    try : 
        result_json_string = res.text[res.text.index('{"type":"finalResult"'):res.text.rindex('}')+1]
    except : 
        print('Please check your audiofile. It may be too small or too short')
        return

    result = json.loads(result_json_string)
    print('hi')
    print(result)


    with open(directory + audio_name + '.txt', 'wb') as file:
        file.write(result['value'].encode('utf8'))

speech_to_text('C:/Users/junhy/Documents/2019/vsCodeTest\DenoisingModule/recorded_data/', 'mh452') #example