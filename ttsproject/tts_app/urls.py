from django.urls import path, include
from .views import FileUploadAPI, TrainingAPI, TTSDownloadAPI, ExtraFileUploadAPI

urlpatterns = [
   path("train/file-upload", FileUploadAPI.as_view()),
   path("train/extra-file-upload", ExtraFileUploadAPI.as_view()),
   path("train", TrainingAPI.as_view()),
   path("tts", TTSDownloadAPI.as_view())
]