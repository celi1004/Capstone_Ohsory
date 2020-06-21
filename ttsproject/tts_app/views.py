from django.shortcuts import render, get_object_or_404
from django.http import  HttpResponse
from wsgiref.util import FileWrapper
from rest_framework import viewsets, permissions, generics, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.parsers import MultiPartParser, FormParser
from .models import TrainData, TTS
from auth_app.models import Profile
from django.contrib.auth.models import User
from .serializers import FileUploadSerializer, TTSDownloadSerializer
import requests
import json
import wave
import os
import sys
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\Tacotron2-Wavenet-Korean-TTS\datasets')
import script2json
import subprocess
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\KakaoSTT')
import rest
from .tasks import tacotron_request, preprocess, denoising
sys.path.insert(0, r'C:\Users\pjpp8\tts_project\ttsproject\DenoisingModule')
import audio_denoise
import random
import string
# Create your views here.

def get_duration(audio_path):
    audio = wave.open(audio_path)
    frames = audio.getnframes()
    rate = audio.getframerate()
    duration = frames/float(rate)
    return duration

class FileUploadAPI(APIView):

    parser_classes = (MultiPartParser, FormParser, )

    def post(self, request, *args, **kwargs):

        """
            음성 녹음 파일 업로드 API

            ---
            # 내용
                - user_id
                - voice: 파일

            # 응답 예시
                {
                    "id": 11,
                    "voice": "/media/user_test1234/1_0001.wav",
                    "text": "테스트 텍스트입니다.",
                    "duration": 3.9721768707482994,
                    "user_id": 20
                }
        """

        new_data = request.data.dict()

        user_id = request.data['user_id']
        voice = request.FILES['voice']

        new_data['user_id'] = int(new_data['user_id'])
        new_data['duration'] = get_duration(voice)
        username = get_object_or_404(User, id = user_id).username

        char_set = string.ascii_letters + string.digits
        file_name = ''.join(random.sample(char_set*6, 6))
        
        new_data['file_name'] = file_name
        new_data['text'] = "임시데이터"

        #사용자 db에 duration 더해주기
        '''
        profile = get_object_or_404(Profile, user_pk = user_id)
        profile.audio_time = profile.audio_time + new_data['duration']
        profile.save()
        '''

        serializer = FileUploadSerializer(data = new_data)
        
        if serializer.is_valid():
            serializer.save()
            
            preprocess.delay(username, file_name)

            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(status=status.HTTP_400_BAD_REQUEST)
    

class ExtraFileUploadAPI(APIView):

    parser_classes = (MultiPartParser, FormParser, )

    def post(self, request, *args, **kwargs):

        """
            음성 녹음 파일 업로드 API

            ---
            # 내용
                - user_id
                - voice: 파일
                - text: 대본

            # 응답 예시
                {
                    "id": 11,
                    "voice": "/media/user_test1234/1_0001.wav",
                    "text": "테스트 텍스트입니다.",
                    "duration": 3.9721768707482994,
                    "user_id": 20
                }
        """

        new_data = request.data.dict()

        user_id = request.data['user_id']
        text = request.data['text']
        voice = request.FILES['voice']

        new_data['user_id'] = int(new_data['user_id'])
        new_data['duration'] = get_duration(voice)
        username = get_object_or_404(User, id = user_id).username

        char_set = string.ascii_letters + string.digits
        file_name = ''.join(random.sample(char_set*6, 6))
        
        new_data['file_name'] = file_name

        file_path = os.path.join('C:/Users/pjpp8/tts_project/ttsproject/Tacotron2-Wavenet-Korean-TTS/datasets/', username)
        file_path = os.path.abspath(file_path)
        f = open(os.path.join(file_path, file_name+'.txt'), 'w', encoding='utf-8')
        f.write(text.strip('"'))
        f.close()

        #사용자 db에 duration 더해주기
        profile = get_object_or_404(Profile, user_pk = user_id)
        profile.audio_time = profile.audio_time + new_data['duration']
        profile.save()

        serializer = FileUploadSerializer(data = new_data)
        
        if serializer.is_valid():
            serializer.save()

            denoising.delay(username, file_name)
            return Response(status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

userlist = ['testdemo1', 'testdemo2', 'testdemo3']
train_running = False
class TrainingAPI(APIView):

    def post(self, request, *args, **kwargs):


        """
            모델 생성 요청 API

            ---
            # 내용
                - user_id
        """
        global userlist, train_running
        user_id = request.data['user_id']
        #profile = get_object_or_404(Profile, user_pk=user_id)
        username = get_object_or_404(User, id = user_id).username

        if train_running == True:
            userlist.append(username)
        else:
            if len(userlist) <= 2:
                userlist.append(username)
            else:
                userlist.append(username)
                tacotron_request.delay(userlist)
                train_running = True
                userlist = []

        print(userlist)
        
        return Response(status=status.HTTP_200_OK)

class TTSDownloadAPI(APIView):

    serializer_class = TTSDownloadSerializer

    def post(self, request):

        
        """
            TTS 요청 API

            ---
            # 내용
                - username
                - req_text : tts 요청할 텍스트

            # 응답 예시
                {
                    "id": null,
                    "req_text": "테스트 tts 요청입니다.",
                    "tts_file": "/media/1_0000.wav",
                    "user_id": 18
                }
        """

        username = request.data['username']
        user = get_object_or_404(User, username = username)
        req_text = request.data['req_text']
        if req_text is "":
            return Response(status=status.HTTP_400_BAD_REQUEST)
        profile = get_object_or_404(Profile, user_pk = user.id)
        model = profile.model.split('|')
        logdir = model[0]
        num_speaker = model[1]
        speaker_id = model[2]
        
        #user_id 이용해서 맞는 모델로 req_text tts진행 -> 음성파일 생성됨
        #음성파일,, 이름을 정할 수 있나?
        #일단, 음성파일 저장됐고 파일 경로, 파일명 알 수 있다고 가정
        file_path = '/Tacotron2-Wavenet-Korean-TTS/logdir-tacotron2/generate/'
        output = subprocess.check_output('python C:/Users/pjpp8/tts_project/ttsproject/Tacotron2-Wavenet-Korean-TTS/synthesizer.py --load_path C:/Users/pjpp8/tts_project/ttsproject/Tacotron2-Wavenet-Korean-TTS/'+logdir+' --num_speakers '+num_speaker+' --speaker_id '+speaker_id+' --text "'+req_text+'"', shell=True)
        #print(output)
        output = output.decode('utf-8')
        png = output.find(".png")
        wavname = output[png-19: png]

        file_path = file_path+wavname+".wav"
        tts = TTS(user_id = user, req_text = req_text, tts_file = str(file_path))

        serializer = TTSDownloadSerializer(tts)
        return Response(serializer.data, status=status.HTTP_200_OK)

