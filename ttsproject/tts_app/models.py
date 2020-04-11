from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class TrainData(models.Model):

    user_id = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='+',
        db_column='user_id')
    voice = models.FileField()
    text = models.TextField(null=False)