from django.shortcuts import render, get_object_or_404
from rest_framework import viewsets, permissions, generics, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.decorators import api_view
from .models import Share, Share_info
from auth_app.models import Profile
from django.contrib.auth.models import User
from .serializers import MyShareInfoSerializer,ShareInfoListSerializer, ShareSerializer, ShareListSerializer, ShareAuthSerializer, ShareInfoSerializer, ShareInfoApproveSerializer
import requests
import json
from .tasks import send_fcm_notification

# Create your views here.
class ShareAPI(APIView):

    def get_object(self, pk):
        return get_object_or_404(Share, pk=pk)

    def post(self, request, format=None):
        """
            새로운 공유 생성 API
            
            ---
            # 내용
                - sharer_name: 공유해주는 사람의 username
                - sharee_name: 공유받을 사람의 username
                - listening_noti: boolean, True면 청취 시 모델 주인에게 알림
                - download_noti: boolean, True면 다운로드 시 모델 주인에게 알림
                - download_auth(optional): 공유 모델을 이용한 다운로드 시 허락받기를 요구할 지(default=True, 허락을 받아야 함)
        
            # 응답 예시 (성공 : 201_CREATED, sharee_name 틀림: 404_NOT_FOUND, 본인username으로 공유요청: 406_Not_Acceptable, 저장실패: 400)
                {
                  "id": 13,
                  "sharer_name": testuser00,
                  "sharee_name": testuser01,
                  "listening_noti": true,
                  "download_noti": true,
                  "download_auth": true
                }
        """
        serializer = ShareSerializer(data=request.data)
        
        sharee_name = get_object_or_404(User, username = request.data.get('sharee_name'))

        if sharee_name == request.data.get('sharer_name'):
            return Response("wrong sharee_name", status=status.HTTP_406_NOT_ACCEPTABLE)
        
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk):
        """
            공유 삭제 API

            ---
            # 내용
                - id(pk)
        """
        share = self.get_object(pk)
        share.delete()
        return Response(status=status.HTTP_200_OK)

# 내가 공유해준 애들 sharer_id = 나
class MyShareAPI(APIView):

    def post(self, request, format=None):
        """
            내가 공유 해준 목록 확인

            ---
            # 내용
                - sharer_name: sharer의 username 값

            # 응답 예시 {'sharer_name': testuser00}
                [
                    {
                        "id": 4,
                        "sharer_name": testuser00,
                        "sharee_name": testuser01,
                        "listening_noti": true,
                        "download_noti": true,
                        "download_auth": true
                    }
                ]
        """
        queryset = Share.objects.filter(sharer_name=request.data.get('sharer_name'))
        serializer = ShareSerializer(queryset, many=True)
        if len(serializer.data) != 0:
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response("no data", status=status.HTTP_400_BAD_REQUEST)

#내가 공유해준 모델 사용된 기록
class MyShareInfo(APIView):

    def post(self, request, format=None):
        """
            내가 공유해준 모델 사용된 기록

            ---
            # 내용
                - sharer_name: sharer의 username 값

            # 응답 예시 {"sharer_name": "testuser00"}
                [
                    {
                        "id": 14,
                        "sharee_name": "test1234",
                        "share_infos": [
                        {
                            "id": 13,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다",
                            "if_approve": 1,
                            "timestamp": "2020-05-18T00:18:16.978035+09:00",
                            "share_id": 14
                        },
                        {
                            "id": 14,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다2",
                            "if_approve": 0,
                            "timestamp": "2020-05-18T00:38:55.035567+09:00",
                            "share_id": 14
                        },
                        {
                            "id": 15,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다3",
                            "if_approve": 0,
                            "timestamp": "2020-05-18T00:39:01.625522+09:00",
                            "share_id": 14
                        }
                        ]
                    }
                ]
        """
        queryset = Share.objects.filter(sharer_name=request.data.get('sharer_name'))

        serializer = MyShareInfoSerializer(queryset, many=True)
        if len(serializer.data) != 0:
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response("no data", status=status.HTTP_400_BAD_REQUEST)

#내가 요청한 공유모델 사용 기록
class ShareInfoList(APIView):

    def post(self, request, format=None):
        """
            내가 요청한 공유모델 사용 기록

            ---
            # 내용
                - sharee_name: sharee의 username 값

            # 응답 예시 {"sharee_name": "test1234"}
                [
                    {
                        "id": 14,
                        "sharer_name": "testuser00",
                        "share_infos": [
                        {
                            "id": 13,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다",
                            "if_approve": 1,
                            "timestamp": "2020-05-18T00:18:16.978035+09:00",
                            "share_id": 14
                        },
                        {
                            "id": 14,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다2",
                            "if_approve": 0,
                            "timestamp": "2020-05-18T00:38:55.035567+09:00",
                            "share_id": 14
                        },
                        {
                            "id": 15,
                            "req_type": 1,
                            "req_text": "tts 테스트 텍스트입니다3",
                            "if_approve": 0,
                            "timestamp": "2020-05-18T00:39:01.625522+09:00",
                            "share_id": 14
                        }
                        ]
                    }
                ]
        """
        queryset = Share.objects.filter(sharee_name=request.data.get('sharee_name'))

        serializer = ShareInfoListSerializer(queryset, many=True)
        if len(serializer.data) != 0:
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response("no data", status=status.HTTP_400_BAD_REQUEST)

# 내가 공유받은 것 sharee_id = 나
class ShareListAPI(APIView):

    def post(self, request, format=None):
        """
            내가 공유 받은 목록 확인

            ---
            # 내용
                - sharee_name: sharee의 username 값

            # 응답 예시 {'sharee_name': 15}
                [
                  {
                    "id": 6,
                    "sharer_name": 19
                  }
                ]
        """
        queryset = Share.objects.filter(sharee_name=request.data.get('sharee_name'))
        serializer = ShareListSerializer(queryset, many=True)
        if len(serializer.data) != 0:
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response("no data", status=status.HTTP_400_BAD_REQUEST)

# 공유 모델 알림설정, 다운로드 권한 수정
class ShareAuthAPI(APIView):

    def get_object(self, pk):
        return get_object_or_404(Share, pk=pk)

    def put(self, request, pk):
        """
            공유 모델의 알림설정, 다운로드 권한 수정

            ---
            # 내용
                - listening_noti: boolean, True면 청취 시 모델 주인에게 알림
                - download_noti: boolean, True면 다운로드 시 모델 주인에게 알림
                - download_auth: boolean, True면 다운로드 시 허락요청 필수
            
            # 응댭 예시
                {
                    "id": 11,
                    "listening_noti": false,
                    "download_noti": true,
                    "download_auth": true
                }
        """
        share = self.get_object(pk)
        serializer = ShareAuthSerializer(share, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

#공유 모델 알림보내기, 허락하기
class ShareInfoNotiAPI(APIView):

    def get_object(self, pk):
        return get_object_or_404(Share_info, pk=pk)

    def post(self, request, format=None):
        """
            공유 모델 사용 시 알림 요청

            ---
            # 내용
                - sharer_name: sharer의 username
                - sharee_name: sharee의 username
                - req_type: 0은 듣기, 1은 다운로드
                - req_text

            # 응답 예시
                {
                  "id": 12,
                  "req_type": 1,
                  "req_text": "tts 테스트 텍스트입니다",
                  "if_approve": 0,
                  "timestamp": "2020-04-19T20:13:01.373378+09:00",
                  "share_id": 11
                }  
        """
        sharer_name = request.data.get('sharer_name')
        sharee_name = request.data.get('sharee_name')
        share = get_object_or_404(Share, sharee_name=sharee_name, sharer_name=sharer_name)
        sharee_id = get_object_or_404(User, username = sharee_name).id
        sharer_id = get_object_or_404(User, username = sharer_name).id
        listening_noti = share.listening_noti
        download_noti = share.download_noti
        download_auth = share.download_auth
        ids = get_object_or_404(Profile, user_pk=sharer_id).fcm_key
        
        if(request.data.get('req_type')==0):
            if listening_noti is True:
                print(sharee_name+"의 모델 사용 알림"+ "\n"+ "text: "+request.data.get("req_text"))
                send_fcm_notification.delay(ids, sharee_name+"의 모델 사용 알림", "요청 문장 : "+request.data.get("req_text"))
        else:
            if download_auth is True:
                print(sharee_name+"의 다운로드 허락 요청"+"\n"+ "text: "+request.data.get("req_text"))
                send_fcm_notification.delay(ids, sharee_name+"의 다운로드 허락 요청", "요청 문장 : "+request.data.get("req_text"))
                new_data = request.data
                new_data.pop('sharer_name')
                new_data.pop('sharee_name')
                new_data['share_id'] = share.id

                serializer = ShareInfoSerializer(data=new_data)
                if serializer.is_valid():
                    serializer.save()
                    return Response(serializer.data, status=status.HTTP_202_ACCEPTED)
                return Response(serializers.errors, status=status.HTTP_400_BAD_REQUEST)
            else:
                if download_noti is True:
                    print(sharee_name+"의 다운로드 알림"+"\n" +"text: "+request.data.get("req_text"))
                    send_fcm_notification.delay(ids, sharee_name+"의 다운로드 알림", "요청 문장 : "+request.data.get("req_text"))
    
        new_data = request.data
        new_data['if_approve'] = 1
        new_data.pop('sharer_name')
        new_data.pop('sharee_name')
        new_data['share_id'] = share.id

        serializer = ShareInfoSerializer(data=new_data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializers.errors, status=status.HTTP_400_BAD_REQUEST)

    #허락할때, if_approve만 변경 1이면 허락, 2면 거절
    def put(self, request, pk):
        """
            sharee의 다운로드 요청 허락

            ---
            # 내용
                - id: share_info의 pk 값
                - if_approve: 1은 허락, 2는 거절
            
            # 응답 예시
                {
                    "id": 12,
                    "if_approve": 1
                }
        """
        share_info = self.get_object(pk)
        serializer = ShareInfoApproveSerializer(share_info, data=request.data)
        
        #sharee에게 알림보내기
        share_id = share_info.share_id
        share = get_object_or_404(Share, id=share_id.id)
        sharee_name = share.sharee_name
        sharer_name = share.sharer_name
        sharee_id = get_object_or_404(User, username=sharee_name).id

        ids = get_object_or_404(Profile, user_pk=sharee_id).fcm_key
        
        print(sharer_name+"(이)가 모델 사용을 허락했습니다.\n"+ "요청 문장: "+ share_info.req_text)
        send_fcm_notification.delay(ids, sharer_name+"(이)가 모델 사용을 허락했습니다.", "요청 문장: "+ share_info.req_text)

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

#FCM 보내기
'''
def send_fcm_notification(ids, title, body):

    url = "https://fcm.googleapis.com/fcm/send"

    headers = {
        'Authorization': 'key=AAAATbUzWAk:APA91bEEuV5XjsItkpqBSXAXKNVo2_A4U1ZnzCRK2tfrqwQVDTPcSPE20reyZRpaKoqaRey8bSAMnX2xEbvKRZxneih8LzrcBvzq1hgT2AsBFqSL7CGzLeuxNTeEvrJbRrofhs0k6407',
        'Content-Type': 'application/json; UTF-8',
    }

    content = {
        'to': ids,
        'notification':{
            'title': title,
            'body': body
        }
    }

    requests.post(url, data=json.dumps(content), headers=headers)
'''