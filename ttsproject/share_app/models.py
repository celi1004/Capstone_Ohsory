from django.db import models
from django.contrib.auth.models import User
from tts_app.models import TTS

# Create your models here.
#공유해준 사람(sharer), 공유받은 사람(sharee) 목록
class Share(models.Model):

    class Meta:
        unique_together = (('sharer_name', 'sharee_name'),)

    
    sharer_name = models.CharField(max_length=50, null=False)
    sharee_name = models.CharField(max_length=50, null=False)
    listening_noti = models.BooleanField(default=True)
    download_noti = models.BooleanField(default=True) 
    download_auth = models.BooleanField(default=True) 

#공유 사이 간 tts요청 목록 저장 테이블
class Share_info(models.Model):

    share_id = models.ForeignKey(Share, on_delete=models.CASCADE, related_name="share_infos", db_column='share_id')
    req_type = models.PositiveSmallIntegerField(null=False) #0 is just for listening, 1 is for download
    req_text = models.TextField(null=False)
    if_approve = models.PositiveSmallIntegerField(default=0) #0 is not reply, 1 is approve, 2 is not approve
    timestamp = models.DateTimeField(auto_now_add=True)
