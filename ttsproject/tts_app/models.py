from django.db import models
from auth_app.models import Profile
from django.contrib.auth.models import User

# Create your models here.
def user_directory_path(instance, filename):
    ext = filename.split('.')[-1]
    file_name = '{}.{}'.format(instance.file_name, ext)
    return 'datasets/{}/{}'.format(instance.user_id, file_name)
    
class TrainData(models.Model):

    user_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='traindatas',
        db_column='user_id')
    file_name = models.CharField(max_length=20, null=True)
    voice = models.FileField(upload_to=user_directory_path, blank=False, null=False)
    text = models.TextField(null=False)
    duration = models.FloatField(null=False, default=0)

class TTS(models.Model):
    user_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='ttsmodel',
        db_column='user_id')
    req_text = models.TextField(null=False)
    tts_file = models.CharField(max_length=200, null=True)