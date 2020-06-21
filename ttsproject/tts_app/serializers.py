from rest_framework import serializers
from .models import TrainData, TTS

class FileUploadSerializer(serializers.ModelSerializer):

    class Meta:
        model = TrainData
        fields = "__all__"

class TTSDownloadSerializer(serializers.ModelSerializer):

    class Meta:
        model = TTS
        fields = "__all__"