import requests
import json
import os
import sys
import numpy as np
import chardet
import shutil



kakao_speech_url = "https://kakaoi-newtone-openapi.kakao.com/v1/recognize"

rest_api_key = '486d7fec635fe22892b275c044229a18'
# c4bf9318fb140ac9b970dd5e23b6b2b2
# a43694e772d7ee356827ce32324f267e

#minhee : 486d7fec635fe22892b275c044229a18

headers = {
    "Content-Type": "application/octet-stream",
    "X-DSS-Service": "DICTATION",
    "Authorization": "KakaoAK " + rest_api_key,
}


foldername_data   = 'denoised'
foldername_result = 'stt_pair'
dir_root         = os.path.dirname(os.path.realpath(__file__))
dir_denoised     = dir_root + '/' +foldername_data
dir_result       = dir_root + '/' +foldername_result

list_denoised = os.listdir(dir_denoised)


for data_denoised in list_denoised :
    print(data_denoised)
    with open(dir_denoised + '/' + data_denoised, 'rb') as fp:
        audio = fp.read()

    res = requests.post(kakao_speech_url, headers=headers, data=audio)

    print(res.text)
    result_json_string = res.text[res.text.index('{"type":"finalResult"'):res.text.rindex('}')+1]

    result = json.loads(result_json_string)
    data_denoised_noex = os.path.splitext(data_denoised)[0]

    try:
        os.mkdir(dir_result)
    except OSError:
        print('Folder already exists')

    with open(dir_result + '/' + data_denoised_noex + '.txt', 'wb') as file:
        file.write(result['value'].encode('utf8'))

    shutil.copy(dir_denoised + '/' + data_denoised, dir_result + '/' + data_denoised)
    os.remove(dir_denoised + '/' + data_denoised)
