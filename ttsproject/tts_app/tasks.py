from __future__ import absolute_import

from ttsproject.celery import app
import os
from auth_app.models import Profile
from django.contrib.auth.models import User
from django.shortcuts import get_object_or_404
import sys
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\Tacotron2-Wavenet-Korean-TTS\datasets')
import script2json
import subprocess
import requests
import json
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\DenoisingModule')
import audio_denoise
import vad
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\KakaoSTT')
import rest
from auth_app.models import Profile
from django.contrib.auth.models import User

@app.task
def tacotron_request(userlist):     # 실제 백그라운드에서 작업할 내용을 task로 정의한다.
    
    #print('Hello')
    #multi-speaker
    #Tacotron/datasets/{username}
    base_dir = 'Tacotron2-Wavenet-Korean-TTS'
    dataset_dir = "datasets"
    data_dir = "data"
    dataset = []

    currentPath = os.getcwd()
    
    for username in userlist:
        # 1. alignment.json 생성
        if not os.path.isfile(os.path.join(base_dir, dataset_dir, username, "alignment.json")):
            try:
                script2json.script2json(username)
                print("make script for ", username)
            except Exception as e:
                print("making script error ",username)
                print(e)
                #return Response(status=status.HTTP_412_PRECONDITION_FAILED)
        
        # 2. preprocess.py로 data 생성 Tacotron/data/{username}
        if not os.path.isdir(os.path.join(base_dir, data_dir, username )):
            try:
                print(">>>>>>>>>> preprocess <<<<<<<<<<<<<<<<")
                os.chdir(r'C:\Users\pjpp8\tts_project\ttsproject\Tacotron2-Wavenet-Korean-TTS')
                subprocess.call('python preprocess.py --num_workers 10 --name '+username+' --in_dir datasets\\'+username+' --out_dir data\\'+username, shell=True)
                os.chdir(currentPath)
            except:
                print("preprocess error")
                #return Response(status=status.HTTP_409_CONFLICT) 

        dataset.append('.\\data\\'+username)
        
    # 3. train_tacotron2.py 학습 시작
    data = ','.join(dataset)
    os.chdir(r'C:\Users\pjpp8\tts_project\ttsproject\Tacotron2-Wavenet-Korean-TTS')
    output = subprocess.check_output('python train_tacotron2.py --data_paths '+data, shell=True)
    os.chdir(currentPath)

    print(output)
    output = output.decode('utf-8')
    temp = output.find("MODEL dir: ")
    temp_star = output.find("[*]", temp)
    logdir = output[temp+11:temp_star-3]
    print(logdir)

    loss_index = output.find("last loss")
    print(output[loss_index+10:loss_index+18])
    loss = float(output[loss_index+10:loss_index+18].replace(' ', ''))
    print(loss)
    # 4. loss * 학습 데이터 양으로 progress 판단
    
    for user in userlist:
        user_id = get_object_or_404(User, username = user).id
        profile = get_object_or_404(Profile, user_pk=user_id)

        if loss  > 1:
            loss = loss * 0.01
            progress = int(loss * 100)
        else:
            if profile.audio_time/360 <= 1:
                progress = int((1-loss) * profile.audio_time/360 * 100)
            else:
                progress = int((1-loss) * 100)

        profile.progress = progress
    
        # 5. profile에 model directory, num_speaker, speaker_id 저장
        num_speaker = str(len(userlist))
        speaker_id = str(userlist.index(user))
        profile.model = str(logdir)+'|'+num_speaker+'|'+speaker_id

        # 5. 사용자에게 알림
        ids = profile.fcm_key
        url = "https://fcm.googleapis.com/fcm/send"

        headers = {
            'Authorization': 'key=AAAATbUzWAk:APA91bEEuV5XjsItkpqBSXAXKNVo2_A4U1ZnzCRK2tfrqwQVDTPcSPE20reyZRpaKoqaRey8bSAMnX2xEbvKRZxneih8LzrcBvzq1hgT2AsBFqSL7CGzLeuxNTeEvrJbRrofhs0k6407',
            'Content-Type': 'application/json; UTF-8',
        }

        content = {
            'to': ids,
            'notification':{
                'title': "Ohsory: "+user+"님의 학습이 완료되었습니다.",
                'body': "모델의 완성도는 "+str(progress)+"% 입니다."
            }
        }

        requests.post(url, data=json.dumps(content), headers=headers)
        
        profile.save()


@app.task
def preprocess(username, file_name):
    
    file_path = os.path.join('C:/Users/pjpp8/tts_project/ttsproject/Tacotron2-Wavenet-Korean-TTS/datasets/', username)
    file_path = os.path.abspath(file_path)
    '''
    try:
        if not(os.path.isdir(file_path)):
            os.makedirs(file_path)
    except OSError as e:
        if e.errno != errno.EEXIST:
            print("[!] Failed to create directory.")
    '''

    audio_denoise.convert(file_path+'\\', file_name)
    file_num, total_time = vad.voice_activity_dectection(file_path+"\\", file_name)
    
    if file_num == 0:
        stt = rest.speech_to_text(file_path+'\\', file_name)
        print(stt)
    else:
        for n in range(file_num):
            stt = rest.speech_to_text(file_path+'\\', file_name+'_'+str(n))
            print(stt)

    user_id = get_object_or_404(User, username = username).id
    profile = get_object_or_404(Profile, user_pk = user_id)
    profile.audio_time = profile.audio_time + total_time
    profile.save()
    
@app.task
def denoising(username, file_name):
    file_path = os.path.join('C:/Users/pjpp8/tts_project/ttsproject/Tacotron2-Wavenet-Korean-TTS/datasets/', username)
    file_path = os.path.abspath(file_path)
    audio_denoise.convert(file_path+'\\', file_name)
