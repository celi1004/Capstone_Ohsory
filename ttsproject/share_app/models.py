from django.db import models
from django.contrib.auth.models import User

# Create your models here.
#공유해준 사람(sharer), 공유받은 사람(sharee) 목록
class Share(models.Model):

    sharer_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='+',
        db_column = 'sharer_id',
        )
    sharee_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='+',
        db_column = 'sharee_id',
        )

#공유 사이 간 tts요청 목록 저장 테이블
class Share_info(models.Model):

    share_id = models.ForeignKey(Share, on_delete=models.CASCADE, db_column='share_id')
    req_type = models.PositiveSmallIntegerField(null=False) #0 is just for listening, 1 is for downloat
    req_text = models.TextField(null=False)
    if_approve = models.PositiveSmallIntegerField(default=0) #0 is not reply, 1 is approve, 2 is not approve
    timestamp = models.DateTimeField(auto_now_add=True)
